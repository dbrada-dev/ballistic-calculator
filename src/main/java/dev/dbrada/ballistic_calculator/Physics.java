package dev.dbrada.ballistic_calculator;

import dev.dbrada.ballistic_calculator.units.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.*;
import java.util.*;
//TODO test this
@Data
@AllArgsConstructor
public class Physics {
    private Parameters param;

    private final Pressure vaporPressure;
    private final double airDensityKGPM3;
    private final Speed speedOfSound;
    private final double frontalAreaM2;
    private final double formFactor;
    private final double stabilityFactor;
    /**
     * First [] -> keys; second [] -> values
     */
    private final double[][] dragCoefStd;
    private double angleOfDepartureRAD;

    public Physics(Parameters param) {
        this.param = param;
        this.vaporPressure = vaporPressure();
        this.airDensityKGPM3 = airDensityKGPM3();
        this.speedOfSound = speedOfSound();
        this.frontalAreaM2 = frontalAreaM2();
        this.formFactor = formFactor();
        this.stabilityFactor = stabilityFactor();
        this.dragCoefStd = dragCoefStd();
        this.angleOfDepartureRAD = angleOfDepartureRAD();
    }

    private static LinkedList<double[]> positionIntegration(Physics p) {
        //0-X,1-Y,2-Z
        double[] pos = new double[]{0,0,0};
        double[] velocity = new double[]{
                p.param.getVelocity().getMPS()*Math.cos(p.angleOfDepartureRAD),
                p.param.getVelocity().getMPS()*Math.sin(p.angleOfDepartureRAD),
                0
        };
        //0-X,1-Z
        double[] wind = new double[]{
                p.param.getWindSpeed().getMPS()*Math.cos(p.param.getWindAzimuth().getRAD()),
                p.param.getWindSpeed().getMPS()*Math.sin(p.param.getWindAzimuth().getRAD())
        };
        //0-X,1-Y
        double[] gravity = new double[]{
                Constants.GRAVITY*Math.sin(p.param.getShotAngle().getRAD()),
                Constants.GRAVITY*Math.cos(p.param.getShotAngle().getRAD())
        };

        int[] prevIndex = new int[]{-2};
        int i = 0;
        double nextTarget = p.param.getRangeStep().getM();

        LinkedList<double[]> result = new LinkedList<>();

        do {
            double[] vApp = new double[]{
                    velocity[0]+wind[0],
                    velocity[1],
                    velocity[2]+wind[1]
            };

            double vAppAbs = Math.sqrt(vApp[0]*vApp[0]+vApp[1]*vApp[1]+vApp[2]*vApp[2]);
            double drag = p.dragDeceleration(vAppAbs, prevIndex);

            double[] acceleration = new double[]{
                    -drag * vApp[0]/vAppAbs - gravity[0],
                    -drag * vApp[1]/vAppAbs - gravity[1],
                    -drag * vApp[2]/vAppAbs
            };

            pos[0] += velocity[0]*Constants.TIME_STEP + 0.5*acceleration[0]*Constants.TIME_STEP*Constants.TIME_STEP;
            pos[1] += velocity[1]*Constants.TIME_STEP + 0.5*acceleration[1]*Constants.TIME_STEP*Constants.TIME_STEP;
            pos[2] += velocity[2]*Constants.TIME_STEP + 0.5*acceleration[2]*Constants.TIME_STEP*Constants.TIME_STEP;

            velocity[0] += acceleration[0] * Constants.TIME_STEP;
            velocity[1] += acceleration[1] * Constants.TIME_STEP;
            velocity[2] += acceleration[2] * Constants.TIME_STEP;

            i++;
            double currentTime = i * Constants.TIME_STEP;

            while (pos[0] > nextTarget) {
                result.add(new double[]{
                        nextTarget, currentTime,
                        pos[0],pos[1]-p.param.getSightHeight().getM(),pos[2]+p.spinDrift(currentTime).getM(),
                        velocity[0],velocity[1],velocity[2]
                });
                nextTarget += p.param.getRangeStep().getM();
            }
        } while (pos[0]<p.param.getMaxRange().getM());

        return result;
    }

//█ █▄░█ ▀█▀ █▀▀ █▀▀ █▀█ ▄▀█ ▀█▀ █ █▀█ █▄░█  █▀▀ █▀█ █▀█ █▀▄▀█ █░█ █░░ ▄▀█ █▀
//█ █░▀█ ░█░ ██▄ █▄█ █▀▄ █▀█ ░█░ █ █▄█ █░▀█  █▀░ █▄█ █▀▄ █░▀░█ █▄█ █▄▄ █▀█ ▄█
    private double dragDeceleration(double velocityMPS, int[] prevIndex) {
        return (airDensityKGPM3 * velocityMPS * velocityMPS * (frontalAreaM2 * (formFactor * getDragCoef(velocityMPS, prevIndex))))/(2*param.getMass().getKG());
    }

    private double getDragCoef(double velocityMPS, int[] prevIndex) {
        double mach = machNumber(velocityMPS);
        int i = prevIndex[0];
        double[] machNodes = dragCoefStd[0];
        double[] cdValues = dragCoefStd[1];
        int maxIndex = machNodes.length - 1;

        if (mach <= machNodes[0]) {
            prevIndex[0] = -1;
            return cdValues[0];
        }
        if (mach >= machNodes[maxIndex]) {
            prevIndex[0] = maxIndex;
            return cdValues[maxIndex];
        }

        if (i >= 0 && i < maxIndex) {
            if (mach >= machNodes[i] && mach < machNodes[i + 1]) {
                return dragLinearInterpolation(machNodes[i], cdValues[i], machNodes[i + 1], cdValues[i + 1], mach);
            }
            if (i > 0 && mach >= machNodes[i - 1] && mach < machNodes[i]) {
                prevIndex[0] = i - 1;
                return dragLinearInterpolation(machNodes[i - 1], cdValues[i - 1], machNodes[i], cdValues[i], mach);
            }
            if (i + 2 <= maxIndex && mach >= machNodes[i + 1] && mach < machNodes[i + 2]) {
                prevIndex[0] = i + 1;
                return dragLinearInterpolation(machNodes[i + 1], cdValues[i + 1], machNodes[i + 2], cdValues[i + 2], mach);
            }
        }

        int index = Arrays.binarySearch(machNodes, mach);
        if (index >= 0) {
            prevIndex[0] = index;
            return cdValues[index];
        } else {
            int lowIndex = -index - 2;
            prevIndex[0] = lowIndex;
            return dragLinearInterpolation(machNodes[lowIndex], cdValues[lowIndex], machNodes[lowIndex + 1], cdValues[lowIndex + 1], mach);
        }
    }


    private double dragLinearInterpolation(double low, double lowValue, double high, double highValue, double point) {
        if (high == low) return lowValue;
        return lowValue + (point-low)*(highValue-lowValue)/(high-low);
    }

    private double machNumber(double velocityMPS) {
        return velocityMPS/speedOfSound.getMPS();
    }

//█▀█ █▀█ █▀ ▀█▀ ▄▄ █ █▄░█ ▀█▀ █▀▀ █▀▀ █▀█ ▄▀█ ▀█▀ █ █▀█ █▄░█  █▀▀ █▀█ █▀█ █▀▄▀█ █░█ █░░ ▄▀█ █▀
//█▀▀ █▄█ ▄█ ░█░ ░░ █ █░▀█ ░█░ ██▄ █▄█ █▀▄ █▀█ ░█░ █ █▄█ █░▀█  █▀░ █▄█ █▀▄ █░▀░█ █▄█ █▄▄ █▀█ ▄█
    private Lenght spinDrift(double timeS) {
        double value = 1.25*(stabilityFactor + 1.2)*Math.pow(timeS, 1.83);
        return new Lenght(value, Lenght.ELenght.IN);
    }

//█▀▀ ▀▄▀ █▀▀ █▀▀ ▄▄ █▀█ █▄░█ █▀▀ █▀▀  █▀▀ █▀█ █▀█ █▀▄▀█ █░█ █░░ ▄▀█ █▀
//██▄ █░█ ██▄ █▄▄ ░░ █▄█ █░▀█ █▄▄ ██▄  █▀░ █▄█ █▀▄ █░▀░█ █▄█ █▄▄ █▀█ ▄█
    private Pressure vaporPressure() {
        double saturationVaporPressure = Constants.SATURATION_WATER_PRESSURE * Math.exp(Constants.EMPIRICAL_WATER_VAPOR_CONSTANT*param.getTemperature().getC()/(Constants.TEMPERATURE_SCALING_CONSTANT+param.getTemperature().getC()));
        double value = param.getHumidity()/100 * saturationVaporPressure;
        return new Pressure(value, Pressure.EPressure.PA);
    }

    private double airDensityKGPM3() {
        return (param.getPressure().getPA()-Constants.VAPOR_PRESSURE_FACTOR*vaporPressure.getPA())/(Constants.AIR_GAS_CONSTANT*param.getTemperature().getK());
    }

    private Speed speedOfSound() {
        double virtualTemperature = param.getTemperature().getK()/(1-Constants.VAPOR_PRESSURE_FACTOR*(vaporPressure.getPA()/param.getPressure().getPA()));
        double value = Math.sqrt(Constants.DRY_AIR_HEAT_CAPACITY*Constants.AIR_GAS_CONSTANT*virtualTemperature);
        return new Speed(value, Speed.ESpeed.MPS);
    }

    private double frontalAreaM2() {
        return Math.PI * param.getDiameter().getM()*param.getDiameter().getM()/4;
    }

    private double formFactor() {
        double sectionalDensityLBPIN2 = param.getMass().getLB()/(param.getDiameter().getIN()*param.getDiameter().getIN());
        return sectionalDensityLBPIN2/param.getBalCoef().getValue();
    }

    private double stabilityFactor() {
        double bulletLengthCAL = param.getMass().getKG()/(Constants.BULLET_VOLUME_FACTOR*Constants.BULLET_DENSITY*frontalAreaM2)/param.getDiameter().getM();
        double twistRateCAL = param.getTwistRate().getM()/param.getDiameter().getM();
        double fixedStabilityFactor = 30*param.getMass().getGR()/(twistRateCAL*twistRateCAL*param.getDiameter().getIN()*param.getDiameter().getIN()*param.getDiameter().getIN()*bulletLengthCAL*(1+bulletLengthCAL*bulletLengthCAL));
        double densityCorrection = Constants.SEA_LEVEL_AIR_DENSITY/airDensityKGPM3;
        double velocityCorrection = Math.cbrt(param.getVelocity().getFPS()/2800);
        return fixedStabilityFactor*densityCorrection*velocityCorrection;
    }

    private double[][] dragCoefStd() {
        try {
            BufferedReader rd = new BufferedReader(new FileReader(param.getBalCoef().getType().getResource()));
            String line;
            ArrayList<Double> keys = new ArrayList<>();
            ArrayList<Double> values = new ArrayList<>();
            while((line = rd.readLine()) != null) {
                keys.add(Double.parseDouble(line.split(",")[0]));
                values.add(Double.parseDouble(line.split(",")[1]));
            }
            double[][] result = new double[2][keys.size()];
            for (int i = 0; i < keys.size(); i++) {
                result[0][i] = keys.get(i);
                result[1][i] = values.get(i);
            }
            return result;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private double angleOfDepartureRAD() {
        Parameters p = param.copy();
        p.setMaxRange(param.getZeroRange());
        p.setRangeStep(param.getZeroRange());
        Physics estimationEnv = this.copy();
        estimationEnv.setParam(p);

        double prevEstimatedAngleRAD = Math.atan(param.getSightHeight().getM() / param.getZeroRange().getM());
        estimationEnv.setAngleOfDepartureRAD(prevEstimatedAngleRAD);
        double prevError = positionIntegration(estimationEnv).getFirst()[3];

        double estimatedAngleRAD = prevEstimatedAngleRAD + Math.asin((Constants.GRAVITY * param.getZeroRange().getM())/(param.getVelocity().getMPS() * param.getVelocity().getMPS()));
        estimationEnv.setAngleOfDepartureRAD(estimatedAngleRAD);
        double error;

        int i = 0;

        while(true) {
            error = positionIntegration(estimationEnv).getFirst()[3];
            if (Math.abs(error) < 0.0001) {
                return estimatedAngleRAD;
            }
            i++;
            if(i > 2000) throw new IllegalStateException("Zero angle could not been found");

            double tmp = estimatedAngleRAD;
            estimatedAngleRAD -= error*(estimatedAngleRAD - prevEstimatedAngleRAD)/(error - prevError);
            prevEstimatedAngleRAD = tmp;
            prevError = error;
            estimationEnv.setAngleOfDepartureRAD(estimatedAngleRAD);
        }
    }

//█▀█ █░█ █▄▄ █░░ █ █▀▀  █▀▀ █▀█ █▀█ █▀▄▀█ █░█ █░░ ▄▀█ █▀
//█▀▀ █▄█ █▄█ █▄▄ █ █▄▄  █▀░ █▄█ █▀▄ █░▀░█ █▄█ █▄▄ █▀█ ▄█
    public static Pressure calculatePressure(Lenght altitude) {
        double value = Constants.SEA_LEVEL_PRESSURE * Math.pow(1.0-Constants.TEMPERATURE_LAPS_RATE*altitude.getM()/Constants.SEA_LEVEL_TEMPERATURE, Constants.GRAVITY*Constants.AIR_MOLAR_MASS/(Constants.GAS_CONSTANT*Constants.TEMPERATURE_LAPS_RATE));
        return new Pressure(value, Pressure.EPressure.PA);
    }

    public Physics copy() {
        return new Physics(param, vaporPressure, airDensityKGPM3, speedOfSound, frontalAreaM2, formFactor, stabilityFactor, dragCoefStd, angleOfDepartureRAD);
    }
}

package dev.dbrada.ballistic_calculator;

import dev.dbrada.ballistic_calculator.units.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.*;
import java.util.*;

@Data
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

    public Physics(Parameters param, boolean noAngleOfDeparture) {
        this.param = param;
        this.vaporPressure = vaporPressure();
        this.airDensityKGPM3 = airDensityKGPM3();
        this.speedOfSound = speedOfSound();
        this.frontalAreaM2 = frontalAreaM2();
        this.formFactor = formFactor();
        this.stabilityFactor = stabilityFactor();
        this.dragCoefStd = dragCoefStd();
        this.angleOfDepartureRAD = 0;
    }

    public static LinkedList<double[]> positionIntegration(Physics p) {
        //0-X,1-Y,2-Z
        double[] pos = new double[]{0,0,0};
        double[] velocity = new double[]{
                p.param.velocity().getMPS()*Math.cos(p.angleOfDepartureRAD),
                p.param.velocity().getMPS()*Math.sin(p.angleOfDepartureRAD),
                0
        };
        //0-X,1-Z
        double[] wind = new double[]{
                p.param.windSpeed().getMPS()*Math.cos(p.param.windAzimuth().getRAD()),
                p.param.windSpeed().getMPS()*Math.sin(p.param.windAzimuth().getRAD())
        };
        //0-X,1-Y
        double[] gravity = new double[]{
                Constants.GRAVITY*Math.sin(p.param.shotAngle().getRAD()),
                Constants.GRAVITY*Math.cos(p.param.shotAngle().getRAD())
        };

        int[] prevIndex = new int[]{-2};
        int i = 0;
        //if max < step
        double nextTarget = Math.min(p.param.rangeStep().getM(), p.param.maxRange().getM());

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
                        pos[0],pos[1]-p.param.sightHeight().getM(),pos[2]+p.spinDrift(currentTime).getM(),
                        velocity[0],velocity[1],velocity[2]
                });
                nextTarget += p.param.rangeStep().getM();
            }
        } while (pos[0]<p.param.maxRange().getM() && velocity[0] > 0.5);

        return result;
    }

//█ █▄░█ ▀█▀ █▀▀ █▀▀ █▀█ ▄▀█ ▀█▀ █ █▀█ █▄░█  █▀▀ █▀█ █▀█ █▀▄▀█ █░█ █░░ ▄▀█ █▀
//█ █░▀█ ░█░ ██▄ █▄█ █▀▄ █▀█ ░█░ █ █▄█ █░▀█  █▀░ █▄█ █▀▄ █░▀░█ █▄█ █▄▄ █▀█ ▄█
    private double dragDeceleration(double velocityMPS, int[] prevIndex) {
        return (airDensityKGPM3 * velocityMPS * velocityMPS * (frontalAreaM2 * (formFactor * getDragCoef(velocityMPS, prevIndex))))/(2*param.mass().getKG());
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
    private Length spinDrift(double timeS) {
        double value = Math.signum(param.twistRate().getValue()) * 1.25*(stabilityFactor + 1.2)*Math.pow(timeS, 1.83);
        return new Length(value, Length.ELength.IN);
    }

//█▀▀ ▀▄▀ █▀▀ █▀▀ ▄▄ █▀█ █▄░█ █▀▀ █▀▀  █▀▀ █▀█ █▀█ █▀▄▀█ █░█ █░░ ▄▀█ █▀
//██▄ █░█ ██▄ █▄▄ ░░ █▄█ █░▀█ █▄▄ ██▄  █▀░ █▄█ █▀▄ █░▀░█ █▄█ █▄▄ █▀█ ▄█
    private Pressure vaporPressure() {
        double saturationVaporPressure = Constants.SATURATION_WATER_PRESSURE * Math.exp(Constants.EMPIRICAL_WATER_VAPOR_CONSTANT*param.temperature().getC()/(Constants.TEMPERATURE_SCALING_CONSTANT+param.temperature().getC()));
        double value = param.humidity()/100 * saturationVaporPressure;
        return new Pressure(value, Pressure.EPressure.PA);
    }

    private double airDensityKGPM3() {
        return (param.pressure().getPA()-Constants.VAPOR_PRESSURE_FACTOR*vaporPressure.getPA())/(Constants.AIR_GAS_CONSTANT*param.temperature().getK());
    }

    private Speed speedOfSound() {
        double virtualTemperature = param.temperature().getK()/(1-Constants.VAPOR_PRESSURE_FACTOR*(vaporPressure.getPA()/param.pressure().getPA()));
        double value = Math.sqrt(Constants.DRY_AIR_HEAT_CAPACITY*Constants.AIR_GAS_CONSTANT*virtualTemperature);
        return new Speed(value, Speed.ESpeed.MPS);
    }

    private double frontalAreaM2() {
        return Math.PI * param.diameter().getM()*param.diameter().getM()/4;
    }

    private double formFactor() {
        double sectionalDensityLBPIN2 = param.mass().getLB()/(param.diameter().getIN()*param.diameter().getIN());
        return sectionalDensityLBPIN2/param.balCoef().getValue();
    }

    private double stabilityFactor() {
        if (param.twistRate().getValue() == 0) return 0;
        double bulletLengthCAL = param.mass().getKG()/(Constants.BULLET_VOLUME_FACTOR*Constants.BULLET_DENSITY*frontalAreaM2)/param.diameter().getM();
        double twistRateCAL = param.twistRate().getM()/param.diameter().getM();
        double fixedStabilityFactor = 30*param.mass().getGR()/(twistRateCAL*twistRateCAL*param.diameter().getIN()*param.diameter().getIN()*param.diameter().getIN()*bulletLengthCAL*(1+bulletLengthCAL*bulletLengthCAL));
        double densityCorrection = Constants.SEA_LEVEL_AIR_DENSITY/airDensityKGPM3;
        double velocityCorrection = Math.cbrt(param.velocity().getFPS()/2800);
        return fixedStabilityFactor*densityCorrection*velocityCorrection;
    }

    private double[][] dragCoefStd() {
        try(InputStream in = getClass().getResourceAsStream(param.balCoef().getType().getResource())) {
            if (in == null) throw new IOException("No resource found");
            BufferedReader rd = new BufferedReader(new InputStreamReader(in));
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
        Parameters p = new Parameters(param.diameter(), param.mass(), param.velocity(), param.balCoef(), param.zeroRange(), param.sightHeight(), param.twistRate(), new Temperature(15, Temperature.ETemperature.C), 50, new Speed(0, Speed.ESpeed.MPS), new Angle(0, Angle.EAngle.DEG), calculatePressure(new Length(250, Length.ELength.M)), new Angle(0, Angle.EAngle.DEG), param.zeroRange(), param.zeroRange(), null,null, null, false, false);
        Physics estimationEnv = new Physics(p, false);

        double prevEstimatedAngleRAD = Math.atan(param.sightHeight().getM() / param.zeroRange().getM());
        estimationEnv.setAngleOfDepartureRAD(prevEstimatedAngleRAD);
        double prevError = positionIntegration(estimationEnv).getFirst()[3];

        double estimatedAngleRAD = prevEstimatedAngleRAD + Math.asin((Constants.GRAVITY * param.zeroRange().getM())/(param.velocity().getMPS() * param.velocity().getMPS()));
        estimationEnv.setAngleOfDepartureRAD(estimatedAngleRAD);
        double error;

        int i = 0;

        while(true) {
            error = positionIntegration(estimationEnv).getFirst()[3];
            if (Math.abs(error) < 0.0001) {
                return estimatedAngleRAD;
            }
            i++;
            if(i > 200) throw new IllegalStateException("Zero angle could not been found");

            double tmp = estimatedAngleRAD;
            estimatedAngleRAD -= error*(estimatedAngleRAD - prevEstimatedAngleRAD)/(error - prevError);
            prevEstimatedAngleRAD = tmp;
            prevError = error;
            estimationEnv.setAngleOfDepartureRAD(estimatedAngleRAD);
        }
    }

//█▀█ █░█ █▄▄ █░░ █ █▀▀  █▀▀ █▀█ █▀█ █▀▄▀█ █░█ █░░ ▄▀█ █▀
//█▀▀ █▄█ █▄█ █▄▄ █ █▄▄  █▀░ █▄█ █▀▄ █░▀░█ █▄█ █▄▄ █▀█ ▄█
    public static Pressure calculatePressure(Length altitude) {
        double value = Constants.SEA_LEVEL_PRESSURE * Math.pow(1.0-Constants.TEMPERATURE_LAPS_RATE*altitude.getM()/Constants.SEA_LEVEL_TEMPERATURE, Constants.GRAVITY*Constants.AIR_MOLAR_MASS/(Constants.GAS_CONSTANT*Constants.TEMPERATURE_LAPS_RATE));
        return new Pressure(value, Pressure.EPressure.PA);
    }
}

package dev.dbrada.ballistic_calculator;

import dev.dbrada.ballistic_calculator.units.*;

import java.io.*;
import java.util.*;

public class Physics {
    private final Parameters param;

    private final Pressure saturationVaporPressure;
    private final Pressure vaporPressure;
    private final double airDensityKGPM3; //
    private final Speed speedOfSound;
    private final double frontalAreaM2; //
    private final double sectionalDensityLBPIN2;
    private final double formFactor; //
    private final double bulletLengthCAL;
    private final double twistRateCAL;
    private final double stabilityFactor;
    private final double stabilityDensityCorrection;

    /**
     * First [] -> keys; second [] -> values
     */
    private final double[][] dragCoefStd;
    /**
     * -1 -> lower than index 0
     */
    private int previousDragCoefIndex;

    public Physics(Parameters param) {
        this.param = param;
        this.saturationVaporPressure = saturationVaporPressure();
        this.vaporPressure = vaporPressure();
        this.speedOfSound = speedOfSound();
        this.frontalAreaM2 = frontalAreaM2();
        this.sectionalDensityLBPIN2 = sectionalDensityLBPIN2();
        this.formFactor = formFactor();
        this.bulletLengthCAL = bulletLengthCAL();
        this.airDensityKGPM3 = airDensityKGPM3();
        this.dragCoefStd = dragCoefStd();
        this.previousDragCoefIndex = initializePrevDragCoefIndex();
        this.twistRateCAL = twistRateCAL();
        this.stabilityFactor = stabilityFactor();
        this.stabilityDensityCorrection = stabilityDensityCorrection();
    }

    private double dragDeceleration(double velocityMPS) {
        return (airDensityKGPM3 * velocityMPS * velocityMPS * (frontalAreaM2 * (formFactor * getDragCoef(velocityMPS))))/(2*param.getMass().getKG());
    }

    private double getDragCoef(double velocityMPS) {
        double mach = machNumber(velocityMPS);
        if(previousDragCoefIndex == -1) {
            if(mach < dragCoefStd[0][0]) {
                return dragCoefStd[1][1];
            } else previousDragCoefIndex++;
        } else if (previousDragCoefIndex == dragCoefStd[0].length - 1) {
            if (mach > dragCoefStd[0][dragCoefStd[0].length-1]) {
                return dragCoefStd[1][dragCoefStd[0].length-1];
            } else previousDragCoefIndex--;
        }
        if (mach >= dragCoefStd[0][previousDragCoefIndex]) {
            for (int i = previousDragCoefIndex; i < dragCoefStd.length-1; i++) {
                if (mach > dragCoefStd[0][i] && mach < dragCoefStd[0][i + 1]) {
                    previousDragCoefIndex = i;
                    return linearInterpolation(dragCoefStd[0][i], dragCoefStd[1][i], dragCoefStd[0][i + 1], dragCoefStd[1][i + 1], mach);
                }
            }
        } else {
            for (int i = previousDragCoefIndex; i > 0; i--) {
                if (mach < dragCoefStd[0][i] && mach >= dragCoefStd[0][i - 1]) {
                    previousDragCoefIndex = i-1;
                    return linearInterpolation(dragCoefStd[0][i - 1], dragCoefStd[1][i - 1], dragCoefStd[0][i], dragCoefStd[1][i], mach);
                }
            }
        }
        throw new IllegalStateException("Drag Coefficient for mach number " + mach + " at " + velocityMPS + "m/s could not be found");
    }


    private double linearInterpolation(double low, double lowValue, double high, double highValue, double point) {
        if (high == low) return lowValue;
        return lowValue + (point-low)*(highValue-lowValue)/(high-low);
    }

    private double machNumber(double velocityMPS) {
        return velocityMPS/speedOfSound.getMPS();
    }


    private Pressure saturationVaporPressure() {
        double value = Constants.SATURATION_WATER_PRESSURE * Math.exp(Constants.EMPIRICAL_WATER_VAPOR_CONSTANT*param.getTemperature().getC()/(Constants.TEMPERATURE_SCALING_CONSTANT+param.getTemperature().getC()));
        return new Pressure(value, Pressure.EPressure.PA);
    }

    private Pressure vaporPressure() {
        double value = param.getHumidity()/100 * saturationVaporPressure.getPA();
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

    private double sectionalDensityLBPIN2() {
        return param.getMass().getLB()/(param.getDiameter().getIN()*param.getDiameter().getIN());
    }

    private double formFactor() {
        return sectionalDensityLBPIN2/param.getBalCoef().getValue();
    }

    private double bulletLengthCAL() {
        return param.getMass().getKG()/(Constants.BULLET_VOLUME_FACTOR*Constants.BULLET_DENSITY*frontalAreaM2)/param.getDiameter().getM();
    }

    private double twistRateCAL() {
        return param.getTwistRate().getM()/param.getDiameter().getM();
    }

    private double stabilityFactor() {
        return 30*param.getMass().getGR()/(twistRateCAL*twistRateCAL*param.getDiameter().getIN()*param.getDiameter().getIN()*param.getDiameter().getIN()*bulletLengthCAL*(1+bulletLengthCAL*bulletLengthCAL));
    }

    private double stabilityDensityCorrection() {
        return Constants.SEA_LEVEL_AIR_DENSITY*airDensityKGPM3;
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

    private int initializePrevDragCoefIndex() {
        double mach = machNumber(param.getVelocity().getMPS());
        if (mach > dragCoefStd[0][dragCoefStd[0].length - 1]) {
            return previousDragCoefIndex = dragCoefStd[0].length-1;
        } else if (mach < dragCoefStd[0][0]) {
            return previousDragCoefIndex = -1;
        } else {
            if (mach < dragCoefStd[0][dragCoefStd[0].length / 2]) {
                for (int i = dragCoefStd[0].length / 2; i > 0; i--) {
                    if (mach < dragCoefStd[0][i] && mach >= dragCoefStd[0][i - 1]) {
                        return previousDragCoefIndex = i-1;
                    }
                }
            } else {
                for (int i = dragCoefStd[0].length / 2; i < dragCoefStd[0].length - 1; i++) {
                    if (mach > dragCoefStd[0][i] && mach < dragCoefStd[0][i + 1]) {
                        return previousDragCoefIndex = i;
                    }
                }
            }
        }
        throw new IllegalStateException("Drag Coefficient for mach number " + mach + " at " + param.getVelocity().getMPS() + "m/s could not be found");
    }

    public static Pressure calculatePressure(Lenght altitude) {
        double value = Constants.SEA_LEVEL_PRESSURE * Math.pow(1.0-Constants.TEMPERATURE_LAPS_RATE*altitude.getM()/Constants.SEA_LEVEL_TEMPERATURE, Constants.GRAVITY*Constants.AIR_MOLAR_MASS/(Constants.GAS_CONSTANT*Constants.TEMPERATURE_LAPS_RATE));
        return new Pressure(value, Pressure.EPressure.PA);
    }
}

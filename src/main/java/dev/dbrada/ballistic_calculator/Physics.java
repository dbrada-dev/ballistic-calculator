package dev.dbrada.ballistic_calculator;

import dev.dbrada.ballistic_calculator.units.*;

import java.io.*;
import java.util.*;
//TODO write integration loop and zero angle calculator
public class Physics {
    private final Parameters param;

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

    public Physics(Parameters param) {
        this.param = param;
        this.vaporPressure = vaporPressure();
        this.airDensityKGPM3 = airDensityKGPM3();
        this.speedOfSound = speedOfSound();
        this.frontalAreaM2 = frontalAreaM2();
        this.formFactor = formFactor();
        this.stabilityFactor = stabilityFactor();
        this.dragCoefStd = dragCoefStd();
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

//█▀█ █░█ █▄▄ █░░ █ █▀▀  █▀▀ █▀█ █▀█ █▀▄▀█ █░█ █░░ ▄▀█ █▀
//█▀▀ █▄█ █▄█ █▄▄ █ █▄▄  █▀░ █▄█ █▀▄ █░▀░█ █▄█ █▄▄ █▀█ ▄█
    public static Pressure calculatePressure(Lenght altitude) {
        double value = Constants.SEA_LEVEL_PRESSURE * Math.pow(1.0-Constants.TEMPERATURE_LAPS_RATE*altitude.getM()/Constants.SEA_LEVEL_TEMPERATURE, Constants.GRAVITY*Constants.AIR_MOLAR_MASS/(Constants.GAS_CONSTANT*Constants.TEMPERATURE_LAPS_RATE));
        return new Pressure(value, Pressure.EPressure.PA);
    }
}

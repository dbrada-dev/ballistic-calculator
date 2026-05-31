package dev.dbrada.ballistic_calculator.units;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * Handles speed variables
 */
@Data
@AllArgsConstructor
public class Speed {
    private double value;
    private ESpeed unit;

    /**
     * @param targetUnit wanted unit
     * @return a value of the wanted unit
     */
    public double get(ESpeed targetUnit) {
        return switch(targetUnit) {
            case MPS -> getMPS();
            case FPS -> getFPS();
            case KMPH -> getKMPH();
            case MPH -> getMPH();
        };
    }

    /**
     * @param targetUnit wanted unit
     * @param decimals number of decimal places
     * @return a value of the wanted unit
     */
    public double get(ESpeed targetUnit, int decimals) {
        if (decimals < 0) return get(targetUnit);
        double round = Math.pow(10, decimals);
        return Math.round(get(targetUnit) * round)/round;
    }

    /**
     * @return value of m/s
     */
    public double getMPS() {
        return switch(unit) {
            case MPS -> value;
            case FPS -> value * 0.3048;
            case KMPH -> value / 3.6;
            case MPH -> value * 0.44704;
        };
    }

    /**
     * @return value of fps
     */
    public double getFPS() {
        return switch(unit) {
            case MPS -> value * 3.28084;
            case FPS -> value;
            case KMPH -> value * 0.911344;
            case MPH -> value * 22.0/15.0;
        };
    }

    /**
     * @return value of km/h
     */
    public double getKMPH() {
        return switch(unit) {
            case MPS -> value * 3.6;
            case FPS -> value * 1.09728;
            case KMPH -> value;
            case MPH -> value * 1.609344;
        };
    }

    /**
     * @return value of mph
     */
    public double getMPH() {
        return switch(unit) {
            case MPS -> value / 0.44704;
            case FPS -> value * 15.0/22.0;
            case KMPH -> value * 0.621371;
            case MPH -> value;
        };
    }

    /**
     * Handles speed units
     */
    @Getter
    @AllArgsConstructor
    public enum ESpeed implements NamedUnit {
        MPS("m/s"),
        FPS("fps"),
        KMPH("km/h"),
        MPH("mph");

        private final String name;
    }
}

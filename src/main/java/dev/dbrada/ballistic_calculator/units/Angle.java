package dev.dbrada.ballistic_calculator.units;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * Handles angle variables
 */
@Data
@AllArgsConstructor
public class Angle {
    private double value;
    private EAngle unit;

    /**
     * @param targetUnit wanted unit
     * @return a value of the wanted unit
     */
    public double get(EAngle targetUnit) {
        return switch(targetUnit) {
            case DEG -> getDEG();
            case RAD -> getRAD();
            case MOA -> getMOA();
            case MRAD -> getMRAD();
        };
    }

    /**
     * @param targetUnit wanted unit
     * @param decimals number of decimal places
     * @return a value of the wanted unit
     */
    public double get(EAngle targetUnit, int decimals) {
        if (decimals < 0) return get(targetUnit);
        double round = Math.pow(10, decimals);
        return Math.round(get(targetUnit) * round)/round;
    }

    /**
     * @return value of deg
     */
    public double getDEG() {
        return switch(unit) {
            case DEG -> value;
            case RAD -> value * (180/Math.PI);
            case MOA -> value / 60.0;
            case MRAD -> value * (180/Math.PI) / 1000.0;
        };
    }

    /**
     * @return value of rad
     */
    public double getRAD() {
        return switch(unit) {
            case DEG -> value * (Math.PI/180.0);
            case RAD -> value;
            case MOA -> value * (Math.PI/180.0) / 60.0;
            case MRAD -> value / 1000.0;
        };
    }

    /**
     * @return value of MOA
     */
    public double getMOA() {
        return switch(unit) {
            case DEG -> value * 60;
            case RAD -> value * (180/Math.PI) * 60;
            case MOA -> value;
            case MRAD -> value * (180/Math.PI) / 1000.0 * 60;
        };
    }

    /**
     * @return value of mrad
     */
    public double getMRAD() {
        return switch(unit) {
            case DEG -> value * (Math.PI/180.0) * 1000;
            case RAD -> value * 1000;
            case MOA -> value * (Math.PI/180.0) * 1000 / 60.0;
            case MRAD -> value;
        };
    }

    /**
     * Handles the angle units
     */
    @Getter
    @AllArgsConstructor
    public enum EAngle {
        DEG("deg"),
        RAD("rad"),
        MOA("MOA"),
        MRAD("MIL");

        private final String name;

    }
}

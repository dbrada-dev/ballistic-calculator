package dev.dbrada.ballistic_calculator.units;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * Handles length variables
 */
@Data
@AllArgsConstructor
public class Length {
    private double value;
    private ELength unit;

    /**
     * @param targetUnit wanted unit
     * @return a value of the wanted unit
     */
    public double get(ELength targetUnit) {
        return switch(targetUnit) {
            case MM -> getMM();
            case CM -> getCM();
            case M -> getM();
            case IN -> getIN();
            case FT -> getFT();
            case YD -> getYD();
        };
    }

    /**
     * @param targetUnit wanted unit
     * @param decimals number of decimal places
     * @return a value of the wanted unit
     */
    public double get(ELength targetUnit, int decimals) {
        if (decimals < 0) return get(targetUnit);
        double round = Math.pow(10, decimals);
        return Math.round(get(targetUnit) * round)/round;
    }

    /**
     * @return value of mm
     */
    public double getMM() {
        return switch(unit) {
            case MM -> value;
            case CM -> value * 10;
            case M -> value * 1000;
            case IN -> value * 25.4;
            case FT -> value * 304.8;
            case YD -> value * 914.4;
        };
    }

    /**
     * @return value of cm
     */
    public double getCM() {
        return switch(unit) {
            case MM -> value / 10.0;
            case CM -> value;
            case M -> value * 100;
            case IN -> value * 2.54;
            case FT -> value * 30.48;
            case YD -> value * 91.44;
        };
    }

    /**
     * @return value of m
     */
    public double getM() {
        return switch(unit) {
            case MM -> value * 1e-03;
            case CM -> value * 1e-02;
            case M -> value;
            case IN -> value * 0.0254;
            case FT -> value * 0.3048;
            case YD -> value * 0.9144;
        };
    }

    /**
     * @return value of in
     */
    public double getIN() {
        return switch(unit) {
            case MM -> value * 0.0393701;
            case CM -> value * 0.393701;
            case M -> value * 39.3701;
            case IN -> value;
            case FT -> value * 12;
            case YD -> value * 36;
        };
    }

    /**
     * @return value of ft
     */
    public double getFT() {
        return switch(unit) {
            case MM -> value * 0.00328084;
            case CM -> value * 0.0328084;
            case M -> value * 3.28084;
            case IN -> value / 12.0;
            case FT -> value;
            case YD -> value / 3.0;
        };
    }

    /**
     * @return value of yd
     */
    public double getYD() {
        return switch(unit) {
            case MM -> value * 0.00109361;
            case CM -> value * 0.0109361;
            case M -> value * 1.09361;
            case IN -> value / 36.0;
            case FT -> value / 3.0;
            case YD -> value;
        };
    }

    /**
     * Handles length units
     */
    @Getter
    @AllArgsConstructor
    public enum ELength implements NamedUnit {
        MM("mm"),
        CM("cm"),
        M("m"),
        IN("in"),
        FT("ft"),
        YD("yd");

        private final String name;
    }
}

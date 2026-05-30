package dev.dbrada.ballistic_calculator.units;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * Handles temperature variables
 */
@Data
@AllArgsConstructor
public class Temperature {
    private double value;
    private ETemperature unit;

    /**
     * @param targetUnit wanted unit
     * @return a value of the wanted unit
     */
    public double get(ETemperature targetUnit) {
        return switch(targetUnit) {
            case C -> getC();
            case K -> getK();
            case F -> getF();
        };
    }

    /**
     * @param targetUnit wanted unit
     * @param decimals number of decimal places
     * @return a value of the wanted unit
     */
    public double get(ETemperature targetUnit, int decimals) {
        if (decimals < 0) return get(targetUnit);
        double round = Math.pow(10, decimals);
        return Math.round(get(targetUnit) * round)/round;
    }

    /**
     * @return value of °C
     */
    public double getC() {
        return switch(unit) {
            case C -> value;
            case K -> value - 273.15;
            case F -> (value - 32) * 5/9.0;
        };
    }

    /**
     * @return value of K
     */
    public double getK() {
        return switch(unit) {
            case C -> value + 273.15;
            case K -> value;
            case F -> (value - 32) * 5/9.0 + 273.15;
        };
    }

    /**
     * @return value of °F
     */
    public double getF() {
        return switch(unit) {
            case C -> value * 1.8 + 32;
            case K -> (value - 273.15) * 1.8 + 32;
            case F -> value;
        };
    }

    /**
     * Handles temperature units
     */
    @Getter
    @AllArgsConstructor
    public enum ETemperature {
        C("°C"),
        K("K"),
        F("°F");

        private final String name;
    }
}

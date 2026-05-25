package dev.dbrada.ballistic_calculator.units;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Temperature {
    private double value;
    private ETemperature unit;
    private ETemperature[] allowed;
    private Temperature min;
    private Temperature max;

    public Temperature(double value, ETemperature unit) {
        this.value = value;
        this.unit = unit;
    }

    public double get(ETemperature targetUnit) {
        return switch(targetUnit) {
            case C -> getC();
            case K -> getK();
            case F -> getF();
        };
    }

    public double get(ETemperature targetUnit, int decimals) {
        if (decimals < 0) return get(targetUnit);
        double round = Math.pow(10, decimals);
        return Math.round(get(targetUnit) * round)/round;
    }

    public double getC() {
        return switch(unit) {
            case C -> value;
            case K -> value - 273.15;
            case F -> (value - 32) * 5/9.0;
        };
    }

    public double getK() {
        return switch(unit) {
            case C -> value + 273.15;
            case K -> value;
            case F -> (value - 32) * 5/9.0 + 273.15;
        };
    }

    public double getF() {
        return switch(unit) {
            case C -> value * 1.8 + 32;
            case K -> (value - 273.15) * 1.8 + 32;
            case F -> value;
        };
    }

    public enum ETemperature {
        C, K, F
    }
}

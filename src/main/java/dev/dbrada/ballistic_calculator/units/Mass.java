package dev.dbrada.ballistic_calculator.units;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Mass {
    private double value;
    private EMass unit;
    private EMass[] allowed;
    private Mass min;
    private Mass max;

    public Mass(double value, EMass unit) {
        this.value = value;
        this.unit = unit;
    }

    public double get(EMass targetUnit) {
        return switch(targetUnit) {
            case G -> getG();
            case KG -> getKG();
            case GR -> getGR();
            case LB -> getLB();
        };
    }

    public double get(EMass targetUnit, int decimals) {
        if (decimals < 0) return get(targetUnit);
        double round = Math.pow(10, decimals);
        return Math.round(get(targetUnit) * round)/round;
    }

    public double getG() {
        return switch(unit) {
            case G -> value;
            case KG -> value * 1e+03;
            case GR -> value * 0.0647989;
            case LB -> value * 453.592;
        };
    }

    public double getKG() {
        return switch(unit) {
            case G -> value * 1e-03;
            case KG -> value;
            case GR -> value * 0.0000647989;
            case LB -> value * 0.453592;
        };
    }

    public double getGR() {
        return switch(unit) {
            case G -> value * 15.4324;
            case KG -> value * 15432.4;
            case GR -> value;
            case LB -> value * 7000;
        };
    }

    public double getLB() {
        return switch(unit) {
            case G -> value * 0.00220462;
            case KG -> value * 2.20462;
            case GR -> value / 7000.0;
            case LB -> value;
        };
    }

    public enum EMass {
        G, KG, GR, LB
    }
}

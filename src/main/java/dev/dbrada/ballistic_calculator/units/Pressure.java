package dev.dbrada.ballistic_calculator.units;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class Pressure {
    private double value;
    private EPressure unit;

    public double get(EPressure targetUnit) {
        return switch(targetUnit) {
            case PA -> getPA();
            case HPA -> getHPA();
            case KPA -> getKPA();
        };
    }

    public double get(EPressure targetUnit, int decimals) {
        if (decimals < 0) return get(targetUnit);
        double round = Math.pow(10, decimals);
        return Math.round(get(targetUnit) * round)/round;
    }

    public double getPA() {
        return switch(unit) {
            case PA -> value;
            case HPA -> value * 1e+02;
            case KPA -> value * 1e+03;
        };
    }

    public double getHPA() {
        return switch(unit) {
            case PA -> value * 1e-02;
            case HPA -> value;
            case KPA -> value * 10;
        };
    }

    public double getKPA() {
        return switch(unit) {
            case PA -> value * 1e-03;
            case HPA -> value * 1e-02;
            case KPA -> value;
        };
    }

    @Getter
    @AllArgsConstructor
    public enum EPressure {
        PA("Pa"),
        HPA("hPa"),
        KPA("kPa");

        private final String name;
    }
}

package dev.dbrada.ballistic_calculator.units;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Pressure {
    private double value;
    private EPressure unit;
    private EPressure[] allowed;

    public Pressure(double value, EPressure unit) {
        this.value = value;
        this.unit = unit;
    }

    public double getPA() {
        return switch(unit) {
            case PA -> value;
            case KPA -> value * 1e+03;
        };
    }

    public double getKPA() {
        return switch(unit) {
            case PA -> value * 1e-03;
            case KPA -> value;
        };
    }

    public enum EPressure {
        PA, KPA
    }
}

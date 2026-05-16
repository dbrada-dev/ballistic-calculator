package dev.dbrada.ballistic_calculator.units;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Speed {
    private double value;
    private ESpeed unit;
    private Speed min;
    private Speed max;

    public Speed(double value, ESpeed unit) {
        this.value = value;
        this.unit = unit;
    }

    public double getMPS() {
        return switch(unit) {
            case MPS -> value;
            case FPS -> value * 0.3048;
        };
    }

    public double getFPS() {
        return switch(unit) {
            case MPS -> value * 3.28084;
            case FPS -> value;
        };
    }

    public enum ESpeed {
        MPS, FPS
    }
}

package dev.dbrada.ballistic_calculator.units;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Angle {
    private double value;
    private EAngle unit;
    private EAngle[] allowed;

    public Angle(double value, EAngle unit) {
        this.value = value;
        this.unit = unit;
    }

    public double getDEG() {
        return switch(unit) {
            case DEG -> value;
            case RAD -> value * (180/Math.PI);
        };
    }

    public double getRAD() {
        return switch(unit) {
            case DEG -> value * (Math.PI/180);
            case RAD -> value;
        };
    }

    public enum EAngle {
        DEG, RAD
    }
}

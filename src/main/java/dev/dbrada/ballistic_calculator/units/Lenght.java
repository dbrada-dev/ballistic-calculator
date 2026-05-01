package dev.dbrada.ballistic_calculator.units;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Lenght {
    private double value;
    private ELenght unit;
    private ELenght[] allowed;

    public Lenght(double value, ELenght unit) {
        this.value = value;
        this.unit = unit;
    }

    public double getMM() {
        return switch(unit) {
            case MM -> value;
            case M -> value * 1e+03;
            case IN -> value * 25.4;
            case FT -> value * 304.8;
            case YD -> value * 914.4;
        };
    }

    public double getM() {
        return switch(unit) {
            case MM -> value * 1e-03;
            case M -> value;
            case IN -> value * 0.0254;
            case FT -> value * 0.3048;
            case YD -> value * 0.9144;
        };
    }

    public double getIN() {
        return switch(unit) {
            case MM -> value * 0.0393701;
            case M -> value * 39.3701;
            case IN -> value;
            case FT -> value * 12;
            case YD -> value * 36;
        };
    }

    public double getFT() {
        return switch(unit) {
            case MM -> value * 0.00328084;
            case M -> value * 3.28084;
            case IN -> value / 12;
            case FT -> value;
            case YD -> value / 3;
        };
    }

    public double getYD() {
        return switch(unit) {
            case MM -> value * 0.00109361;
            case M -> value * 1.09361;
            case IN -> value / 36;
            case FT -> value / 3;
            case YD -> value;
        };
    }

    public enum ELenght {
        MM, M, IN, FT, YD
    }
}

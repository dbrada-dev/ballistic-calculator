package dev.dbrada.ballistic_calculator.units;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BallisticCoefficient {
    private double value;
    private EBallisticCoefficient type;

    public enum EBallisticCoefficient {
        G1, G7
    }
}

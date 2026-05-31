package dev.dbrada.ballistic_calculator.units;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * Handles ballistic coefficient variables
 */
@Data
@AllArgsConstructor
public class BallisticCoefficient {
    private double value;
    private EBallisticCoefficient type;

    /**
     * Handles ballistic coefficient typed
     */
    @Getter
    @AllArgsConstructor
    public enum EBallisticCoefficient implements NamedUnit {
        G1 ("/dragCoefficients/g1_dragCoefStd.csv", "G1"),
        G7 ("/dragCoefficients/g7_dragCoefStd.csv", "G7");

        private final String resource;
        private final String name;
    }
}

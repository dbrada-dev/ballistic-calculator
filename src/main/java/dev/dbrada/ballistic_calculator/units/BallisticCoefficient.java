package dev.dbrada.ballistic_calculator.units;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class BallisticCoefficient {
    private double value;
    private EBallisticCoefficient type;

    @Getter
    @AllArgsConstructor
    public enum EBallisticCoefficient {
        G1 ("/g1_dragCoefStd.csv", "G1"),
        G7 ("/g7_dragCoefStd.csv", "G7");

        private final String resource;
        private final String name;
    }
}

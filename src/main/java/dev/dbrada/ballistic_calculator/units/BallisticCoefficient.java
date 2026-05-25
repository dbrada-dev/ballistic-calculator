package dev.dbrada.ballistic_calculator.units;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class BallisticCoefficient {
    private double value;
    private EBallisticCoefficient type;

    public enum EBallisticCoefficient {
        G1 ("src/main/resources/g1_dragCoefStd.csv"),
        G7 ("src/main/resources/g7_dragCoefStd.csv");

        @Getter
        private final String resource;

        EBallisticCoefficient(String resource) {
            this.resource = resource;
        }
    }
}

package dev.dbrada.ballistic_calculator;

import dev.dbrada.ballistic_calculator.units.*;
import lombok.Data;

@Data
public class Parameters {
    private Lenght diameter;
    private Mass mass;
    private Speed velocity;
    private BallisticCoefficient balCoef;

    private Lenght zeroRange;
    private Lenght sightHeight;
    private Lenght TwistRate;

    private Temperature temperature;
    private double humidity;
    private Speed windSpeed;
    private Angle windAzimuth;
    private Pressure pressure;

    private Angle shotAngle;
}

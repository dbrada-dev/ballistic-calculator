package dev.dbrada.ballistic_calculator;

import dev.dbrada.ballistic_calculator.units.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Parameters {
    private Length diameter;
    private Mass mass;
    private Speed velocity;
    private BallisticCoefficient balCoef;

    private Length zeroRange;
    private Length sightHeight;
    private Length twistRate;

    private Temperature temperature;
    private double humidity;
    private Speed windSpeed;
    private Angle windAzimuth;
    private Pressure pressure;

    private Angle shotAngle;

    private Length maxRange;
    private Length rangeStep;

    public Parameters copy(){
        return new Parameters(diameter, mass, velocity, balCoef, zeroRange, sightHeight, twistRate, temperature, humidity, windSpeed, windAzimuth, pressure, shotAngle, maxRange, rangeStep);
    }
}

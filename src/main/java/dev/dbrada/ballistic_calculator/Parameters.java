package dev.dbrada.ballistic_calculator;

import dev.dbrada.ballistic_calculator.units.*;

public record Parameters(Length diameter, Mass mass, Speed velocity, BallisticCoefficient balCoef, Length zeroRange,
                         Length sightHeight, Length twistRate, Temperature temperature, double humidity,
                         Speed windSpeed, Angle windAzimuth, Pressure pressure, Angle shotAngle, Length maxRange,
                         Length rangeStep, Length.ELength range, Length.ELength outputDeviationL, Angle.EAngle[] outputDeviationA,
                         boolean outputTime, boolean outputVelocity) {
}

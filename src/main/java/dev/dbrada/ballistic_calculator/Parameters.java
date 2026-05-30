package dev.dbrada.ballistic_calculator;

import dev.dbrada.ballistic_calculator.units.*;

/**
 * Handles parameters for calculation
 * @param diameter projectile diameter
 * @param mass projectile mass
 * @param velocity projectile muzzle velocity
 * @param balCoef projectile ballistic coefficient
 * @param zeroRange rifle zero range
 * @param sightHeight rifle sight overbore
 * @param twistRate rifle muzzle twist rate
 * @param temperature env temperature
 * @param humidity env humidity
 * @param windSpeed env wind speed
 * @param windAzimuth env speed rotation
 * @param pressure env pressure
 * @param shotAngle rotation of muzzle
 * @param maxRange max calculation range
 * @param rangeStep steps to max range
 * @param range output range unit
 * @param outputDeviationL output deviation unit
 * @param outputDeviationA output deviation units
 * @param outputTime output time of flight?
 * @param outputVelocity output velocity?
 */
public record Parameters(Length diameter, Mass mass, Speed velocity, BallisticCoefficient balCoef, Length zeroRange,
                         Length sightHeight, Length twistRate, Temperature temperature, double humidity,
                         Speed windSpeed, Angle windAzimuth, Pressure pressure, Angle shotAngle, Length maxRange,
                         Length rangeStep, Length.ELength range, Length.ELength outputDeviationL, Angle.EAngle[] outputDeviationA,
                         boolean outputTime, boolean outputVelocity) {
}

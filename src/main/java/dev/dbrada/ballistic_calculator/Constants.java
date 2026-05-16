package dev.dbrada.ballistic_calculator;

public class Constants {
    /**
     * Acceleration due to gravity (g) [m/s^2]
     */
    public static final double GRAVITY = 9.80665;
    /**
     * Standard sea-level pressure (P_0) [Pa]
     */
    public static final double SEA_LEVEL_PRESSURE = 101325;
    /**
     * Standard temperature lapse rate (L_r) [K/m]
     */
    public static final double TEMPERATURE_LAPS_RATE = 0.0065;
    /**
     * Standard sea-level temperature (T_0) [K]
     */
    public static final double SEA_LEVEL_TEMPERATURE = 288.15;
    /**
     * Molar mass of Earth's air (M_air) [kg/mol]
     */
    public static final double AIR_MOLAR_MASS = 0.0289644;
    /**
     * Universal gas constant (R_univ) [mol*K]
     */
    public static final double GAS_CONSTANT = 8.3144598;
    /**
     * Saturation vapor pressure of water at 0°C [Pa]
     */
    public static final double SATURATION_WATER_PRESSURE = 610.78;
    /**
     * Empirical constant derived from the latent heat of vaporization and the gas constant for water vapor [-]
     */
    public static final double EMPIRICAL_WATER_VAPOR_CONSTANT = 17.269;
    /**
     * Constant relating to the "Magnus" coefficients for the experimental data for the boiling point and vapor pressure curve of water [°C]
     */
    public static final double TEMPERATURE_SCALING_CONSTANT = 237.3;
    /**
     * Represents 1 - ε, where ε is the ratio of the molecular weight of water vapor to the molecular weight of dry air.
     */
    public static final double VAPOR_PRESSURE_FACTOR = 0.378;
    /**
     * Heat capacity ratio for dry air [-]
     */
    public static final double DRY_AIR_HEAT_CAPACITY = 1.4;
    /**
     * Specific gas constant for air (R_spec) [J/(kg*K)]
     */
    public static final double AIR_GAS_CONSTANT = 287.05;
    /**
     * Approximately 70% to 75% of spitzer projectile bounding cylindrical volume [-]
     */
    public static final double BULLET_VOLUME_FACTOR = 0.73;
    /**
     * Approximate density of a lead-core, copper-jacketed projectile [kg/m^3]
     */
    public static final double BULLET_DENSITY = 10400;
    /**
     * Standard sea level air density (ρ_std) [kg/m3]
     */
    public static final double SEA_LEVEL_AIR_DENSITY = 1.225;
}

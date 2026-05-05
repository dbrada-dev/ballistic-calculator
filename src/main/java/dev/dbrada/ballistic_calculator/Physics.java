package dev.dbrada.ballistic_calculator;

public class Physics {
    private Parameters param;

    private double vaporPressurePA;
    private double airDensityKGPM3;
    private double speedOfSoundMPS;
    private double frontAreaM2;
    private double sectionalDensityLBPIN2;
    private double formFactor;
    private double bulletLenghtCAL;

    public Physics(Parameters parameters) {
        this.param = parameters;
        this.vaporPressurePA = vaporPressurePA();
        this.airDensityKGPM3 = airDensityKGPM3();
        this.speedOfSoundMPS = speedOfSoundMPS();
        this.frontAreaM2 = frontAreaM2();
        this.sectionalDensityLBPIN2 = sectionalDensityLBPIN2();
        this.formFactor = formFactor();
        this.bulletLenghtCAL = bulletLenghtCAL();
    }

    private double machNumber(double velocity) {
        return velocity/speedOfSoundMPS;
    }

    private double vaporPressurePA() {
        return param.getHumidity()/100 * 6.1078 * Math.exp(17.269*param.getTemperature().getC()/(237.3+param.getTemperature().getC())) * 100;
    }

    private double airDensityKGPM3() {
        return (param.getPressure().getPA() - 0.378*vaporPressurePA)/(287.05*param.getTemperature().getK());
    }

    private double speedOfSoundMPS() {
        return Math.sqrt(1.4 * 287.05 * param.getTemperature().getK()/(1-0.378*vaporPressurePA/param.getPressure().getPA()));
    }

    private double frontAreaM2() {
        return Math.PI*param.getDiameter().getM()*param.getDiameter().getM()/4;
    }

    private double sectionalDensityLBPIN2() {
        return param.getMass().getLB()/(param.getDiameter().getIN()*param.getDiameter().getIN());
    }

    private double formFactor() {
        return sectionalDensityLBPIN2/param.getBalCoef().getValue();
    }

    private double bulletLenghtCAL() {
        return param.getMass().getKG()/(0.73*10400*frontAreaM2)/param.getDiameter().getM();
    }

    public static double calculatePressurePA(double altitude) {
        return 101325 * Math.pow(1 - (0.0065*altitude)/288.15, 5.2558);
    }
}

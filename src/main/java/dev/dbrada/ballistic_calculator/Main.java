package dev.dbrada.ballistic_calculator;

import dev.dbrada.ballistic_calculator.units.*;

import java.util.LinkedList;

public class Main {
    static void main() {
        test1();
        test2();
    }

    //test of FMJ Boat-Tail 55gr 5.56x45mm, resulting in < 1% deviation from www.federalpremium.com/ballistics-calculator (federal does not use humidity, twist rate and wind azimuth)
    static void test1() {
        Parameters p = new Parameters(
                new Length(5.56, Length.ELength.MM),
                new Mass(55, Mass.EMass.GR),
                new Speed(3165, Speed.ESpeed.FPS),
                new BallisticCoefficient(0.246, BallisticCoefficient.EBallisticCoefficient.G1),
                new Length(100, Length.ELength.M),
                new Length(38, Length.ELength.MM),
                new Length(7, Length.ELength.IN),
                new Temperature(15, Temperature.ETemperature.C),
                50,
                new Speed(10, Speed.ESpeed.MPS),
                new Angle(90, Angle.EAngle.DEG),
                Physics.calculatePressure(new Length(1000, Length.ELength.M)),
                new Angle(0, Angle.EAngle.DEG),
                new Length(500, Length.ELength.M),
                new Length(50, Length.ELength.M)
        );

        LinkedList<double[]> ll = Physics.positionIntegration(new Physics(p));

        BallisticCurve bc = new BallisticCurve(ll);
        System.out.println(bc);
    }

    //test of OTM 168gr 7.62x51mm
    static void test2() {
        Parameters p = new Parameters(
                new Length(7.62, Length.ELength.MM),
                new Mass(168, Mass.EMass.GR),
                new Speed(2650, Speed.ESpeed.FPS),
                new BallisticCoefficient(0.46, BallisticCoefficient.EBallisticCoefficient.G1),
                new Length(100, Length.ELength.YD),
                new Length(1.5, Length.ELength.IN),
                new Length(0, Length.ELength.IN),
                new Temperature(59, Temperature.ETemperature.F),
                50,
                new Speed(10, Speed.ESpeed.MPH),
                new Angle(90, Angle.EAngle.DEG),
                Physics.calculatePressure(new Length(2000, Length.ELength.FT)),
                new Angle(0, Angle.EAngle.DEG),
                new Length(500, Length.ELength.YD),
                new Length(50, Length.ELength.YD)
        );

        LinkedList<double[]> ll = Physics.positionIntegration(new Physics(p));

        BallisticCurve bc = new BallisticCurve(ll);
        System.out.println(bc);
    }
}

package dev.dbrada.ballistic_calculator;

import dev.dbrada.ballistic_calculator.units.Angle;
import dev.dbrada.ballistic_calculator.units.Length;
import dev.dbrada.ballistic_calculator.units.Speed;

import java.util.List;

public class BallisticCurve {

    private final Node[] curve;

    public BallisticCurve(List<double[]> list) {
        curve = new Node[list.size()];

        for (int i = 0; i < curve.length; i++) {
            curve[i] = new Node(list.get(i));
        }
    }

    public static class Node {
        private final Length xPos;
        private final Length yPos;
        private final Length zPos;
        private final Angle drop;
        private final Angle drift;
        private final double timeMS;
        private final Speed velocity;

        public Node(double[] point) {
            this.xPos = new Length(point[2], Length.ELength.M);
            this.yPos = new Length(point[3], Length.ELength.M);
            this.zPos = new Length(point[4], Length.ELength.M);
            this.drop = new Angle(point[3]/point[2], Angle.EAngle.RAD);
            this.drift = new Angle(point[4]/point[2], Angle.EAngle.RAD);
            this.timeMS = point[1]*1000;
            this.velocity = new Speed(Math.sqrt(point[5]*point[5]+point[6]*point[6]+point[7]*point[7]), Speed.ESpeed.MPS);
        }
    }

    @Override
    public String toString() {
        // distance, correction dist, correction ang, speed
        Enum<?>[] units = new Enum<?>[] {Length.ELength.YD, Length.ELength.IN, Angle.EAngle.MOA, Speed.ESpeed.FPS};

        StringBuilder sb = new StringBuilder();

        String spacer = " | ";
        String time = "Time [ms]";
        String distance =  "Distance [" + units[0] + "]";
        String dropL = "Drop [" + units[1] + "]";
        String dropA = "Drop [" + units[2] + "]";
        String driftL = "Drift [" + units[1] + "]";
        String driftA = "Drift [" + units[2] + "]";
        String speed = "Velocity [" + units[3] + "]";

        // time, distance, correction dist, correction ang, speed
        int[] spacing = new int[]{time.length(), distance.length(), dropL.length(), dropA.length(), driftL.length(), driftA.length(), speed.length()};

        sb.append(time).append(spacer).append(distance).append(spacer).append(dropL).append(spacer).append(dropA).append(spacer).append(driftL).append(spacer).append(driftA).append(spacer).append(speed);

        for (Node n : curve) {
            time = Math.round(n.timeMS * 100)/100.0 + "";
            distance =  n.xPos.get((Length.ELength) units[0], 0) + "";
            dropL = n.yPos.get((Length.ELength) units[1], 2) + "";
            dropA = n.drop.get((Angle.EAngle) units[2], 2) + "";
            driftL = n.zPos.get((Length.ELength) units[1], 2) + "";
            driftA = n.drift.get((Angle.EAngle) units[2], 2) + "";
            speed = n.velocity.get((Speed.ESpeed) units[3], 2) + "";

            sb.append("\n").append(time).repeat(" ", spacing[0] - time.length()).append(spacer).append(distance).repeat(" ", spacing[1]-distance.length()).append(spacer).append(dropL).repeat(" ", spacing[2]-dropL.length()).append(spacer).append(dropA).repeat(" ", spacing[3]-dropA.length()).append(spacer).append(driftL).repeat(" ", spacing[4]-driftL.length()).append(spacer).append(driftA).repeat(" ", spacing[5]-driftA.length()).append(spacer).append(speed).repeat(" ", spacing[5]-speed.length());
        }

        return sb.toString();
    }
}

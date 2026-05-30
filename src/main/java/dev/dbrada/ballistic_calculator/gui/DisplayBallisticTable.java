package dev.dbrada.ballistic_calculator.gui;

import dev.dbrada.ballistic_calculator.BallisticCurve;
import dev.dbrada.ballistic_calculator.Parameters;
import dev.dbrada.ballistic_calculator.UserSettings;
import dev.dbrada.ballistic_calculator.units.Angle;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
public class DisplayBallisticTable {
    private final Scene previous;
    private final BallisticCurve bc;
    private final Parameters param;

    public Scene getScene() {
        Pane root = new Pane();

        //back button
        Button back = new Button(UserSettings.getStr("goBack.button"));
        back.layoutXProperty().bind(root.widthProperty().multiply(0));
        back.layoutYProperty().bind(root.heightProperty().multiply(0));

        root.getChildren().add(back);

        back.setOnAction(
                (_) -> {
                    Stage stage = (Stage) back.getScene().getWindow();
                    Menu menu = new Menu();
                    menu.start(stage);
                }
        );
        //
        //grid
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        ScrollPane scrollParams = new ScrollPane(grid);
        scrollParams.layoutXProperty().bind(root.widthProperty().multiply(0.025));
        scrollParams.layoutYProperty().bind(back.heightProperty().add(10));
        scrollParams.setPrefSize(previous.getWidth()*0.95, previous.getHeight()-50);
        root.getChildren().add(scrollParams);
        //
        int len = 3;
        if (param.outputTime()) len++;
        if (param.outputVelocity()) len++;
        len += param.outputDeviationA().length*2;

        System.out.println(param.outputDeviationL());

        String[] header = getHeader(len);
        for (int i = 0; i < len; i++) {
            grid.add(new Label(header[i]), i, 0);
        }

        for (int i = 0; i < bc.getCurve().length; i++) {
            String[] node = getLine(bc.getCurve()[i], len);
            for (int j = 0; j < node.length; j++) {
                grid.add(new Label(node[j]), j, i+1);
            }
        }

        Scene scene = new Scene(root, previous.getWidth(), previous.getHeight());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/gui/" + UserSettings.colorPreset.getPrefix() + ".css")).toExternalForm());

        return scene;
    }

    private String[] getLine(BallisticCurve.Node node, int len) {
        String[] result = new String[len];

        int i = 0;
        if (param.outputTime()) result[i++] = Math.round(node.getTimeMS()) + "";
        result[i++] = node.getXPos().get(param.range(), 0) + "";
        result[i++] = node.getYPos().get(param.outputDeviationL(), 2) + "";
        for (Angle.EAngle a : param.outputDeviationA()) {
            result[i++] = node.getDrop().get(a, 2) + "";
        }
        result[i++] = node.getZPos().get(param.outputDeviationL(), 2) + "";
        for (Angle.EAngle a : param.outputDeviationA()) {
            result[i++] = node.getDrift().get(a, 2) + "";
        }
        if (param.outputVelocity()) result[i] = node.getVelocity().get(param.velocity().getUnit(), 0) + "";

        return result;
    }

    private String[] getHeader(int len) {
        String[] result = new String[len];

        int i = 0;
        if (param.outputTime()) result[i++] = UserSettings.getStr("time.table") + " [ms]";
        result[i++] = UserSettings.getStr("range.table") + " [" + param.range().getName() + "]";
        result[i++] = UserSettings.getStr("drop.table") + " [" + param.outputDeviationL().getName() + "]";
        for (Angle.EAngle a : param.outputDeviationA()) {
            result[i++] = UserSettings.getStr("drop.table") + " [" + a.getName() + "]";
        }
        result[i++] = UserSettings.getStr("drift.table") + " [" + param.outputDeviationL().getName() + "]";
        for (Angle.EAngle a : param.outputDeviationA()) {
            result[i++] = UserSettings.getStr("drift.table") + " [" + a.getName() + "]";
        }
        if (param.outputVelocity()) result[i] = UserSettings.getStr("velocity.table") + " [" + param.velocity().getUnit().getName() + "]";

        return result;
    }
}

package dev.dbrada.ballistic_calculator.gui;

import dev.dbrada.ballistic_calculator.BallisticCurve;
import dev.dbrada.ballistic_calculator.Parameters;
import dev.dbrada.ballistic_calculator.UserSettings;
import dev.dbrada.ballistic_calculator.units.Angle;
import javafx.animation.SequentialTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;

import java.util.Objects;
import java.util.Optional;

/**
 * Provides a gui display of a ballistic curve by table
 */
@AllArgsConstructor
public class DisplayBallisticTable {
    private final Scene previous;
    private final BallisticCurve bc;
    private final Parameters param;

    /**
     * Constructs the gui image
     * @return a {@code Scene} to be displayed
     */
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

        Button save = new Button(UserSettings.getStr("save.button"));
        save.layoutXProperty().bind(root.widthProperty().multiply(1).subtract(save.widthProperty()));
        save.layoutYProperty().bind(root.heightProperty().multiply(0));

        root.getChildren().add(save);

        save.setOnAction(
                (_) -> {
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle(UserSettings.getStr("fileName.title"));
                    dialog.setContentText(UserSettings.getStr("fileName.label"));

                    Optional<String> result = dialog.showAndWait();

                    Label status;
                    Pane rootPane = (Pane) save.getScene().getRoot();
                    if (result.isPresent() && Parameters.save(result.get(), param)) {
                        status = new Label(UserSettings.getStr("saveSuccessful.label"));
                        status.getStyleClass().add("success-notification");
                    } else {
                        status = new Label(UserSettings.getStr("saveUnsuccessful.label"));
                        status.getStyleClass().add("error-notification");
                    }

                    status.layoutXProperty().bind(rootPane.widthProperty().subtract(status.widthProperty()).divide(2));
                    status.layoutYProperty().bind(rootPane.heightProperty().subtract(status.heightProperty()).subtract(50));

                    rootPane.getChildren().add(status);

                    SequentialTransition sequence = SharedElements.getTransition(status, rootPane);
                    sequence.play();
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
        scrollParams.layoutYProperty().bind(save.heightProperty().add(10));
        scrollParams.setPrefSize(previous.getWidth()*0.95, previous.getHeight()-50);
        root.getChildren().add(scrollParams);
        //
        int len = 3;
        if (param.outputTime()) len++;
        if (param.outputVelocity()) len++;
        len += param.outputDeviationA().length*2;

        String[] header = getHeader(len);
        for (int i = 0; i < len; i++) {
            grid.add(new Label(header[i]), i, 0);
        }

        for (int i = 0; i < bc.curve().length; i++) {
            String[] node = getLine(bc.curve()[i], len);
            for (int j = 0; j < node.length; j++) {
                grid.add(new Label(node[j]), j, i+1);
            }
        }

        Scene scene = new Scene(root, previous.getWidth(), previous.getHeight());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/gui/" + UserSettings.colorPreset.getPrefix() + ".css")).toExternalForm());

        return scene;
    }

    /**
     * Makes a line of table
     * @param node the line of a table
     * @param len length of the line
     * @return a line of the table
     */
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

    /**
     * Makes a table header
     * @param len length of the header
     * @return a header for the table
     */
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

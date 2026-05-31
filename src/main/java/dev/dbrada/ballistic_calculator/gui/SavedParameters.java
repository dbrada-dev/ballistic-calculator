package dev.dbrada.ballistic_calculator.gui;

import dev.dbrada.ballistic_calculator.BallisticCurve;
import dev.dbrada.ballistic_calculator.Parameters;
import dev.dbrada.ballistic_calculator.Physics;
import dev.dbrada.ballistic_calculator.UserSettings;
import dev.dbrada.ballistic_calculator.units.*;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

/**
 * Handles user saved parameters
 */
@AllArgsConstructor
public class SavedParameters {
    private final Scene previous;

    /**
     * Constructs the final form
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
        //

        //parameters
        //grid
        GridPane params = new GridPane();
        params.setAlignment(Pos.CENTER_LEFT);
        params.setHgap(10);
        params.setVgap(10);
        ScrollPane scrollParams = new ScrollPane(params);
        scrollParams.layoutXProperty().bind(root.widthProperty().multiply(0.025));
        scrollParams.layoutYProperty().bind(back.heightProperty().add(10));
        scrollParams.setPrefSize(previous.getWidth() * 0.95, previous.getHeight() - 50);
        root.getChildren().add(scrollParams);
        //
        //

        Path[] savesPaths = Parameters.getSaves();
        String[] saves = new String[savesPaths.length];
        for (int i = 0; i < savesPaths.length; i++) {
            String tmp = savesPaths[i].subpath(savesPaths[i].getNameCount()-1, savesPaths[i].getNameCount()).toString();
            saves[i] = tmp.substring(0, tmp.length()-4);
        }

        for (int i = 0; i < saves.length; i++) {
            Label label = new Label(saves[i]);
            Button view = new Button(UserSettings.getStr("view.button"));
            Button delete = new Button(UserSettings.getStr("delete.button"));
            Button edit = new Button(UserSettings.getStr("edit.button"));

            int finalI = i;
            view.setOnAction((_) -> {
                try {
                    Parameters p = Parameters.load(saves[finalI]);
                    DisplayBallisticTable display = new DisplayBallisticTable(previous, new BallisticCurve(Physics.positionIntegration(new Physics(p))), p);
                    ((Stage) view.getScene().getWindow()).setScene(display.getScene());
                } catch (Exception _) {
                    Parameters.delete(saves[finalI]);
                    Scene nextScene = getScene();
                    ((Stage) view.getScene().getWindow()).setScene(nextScene);

                    Pane rootPane = (Pane) nextScene.getRoot();
                    Label notification = new Label(UserSettings.getStr("loadUnsuccessful.label"));
                    notification.getStyleClass().add("error-notification");

                    notification.layoutXProperty().bind(rootPane.widthProperty().subtract(notification.widthProperty()).divide(2));
                    notification.layoutYProperty().bind(rootPane.heightProperty().subtract(notification.heightProperty()).subtract(50));

                    rootPane.getChildren().add(notification);

                    SequentialTransition sequence = SharedElements.getTransition(notification, rootPane);
                    sequence.play();
                }
            });

            delete.setOnAction((_) -> {
                Parameters.delete(saves[finalI]);
                ((Stage) delete.getScene().getWindow()).setScene(getScene());
            });

            edit.setOnAction((_) -> {
                try {
                    Parameters p = Parameters.load(saves[finalI]);
                    ((Stage) edit.getScene().getWindow()).setScene(getEditable(p));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    Parameters.delete(saves[finalI]);
                    Scene nextScene = getScene();
                    ((Stage) view.getScene().getWindow()).setScene(nextScene);

                    Pane rootPane = (Pane) nextScene.getRoot();
                    Label notification = new Label(UserSettings.getStr("loadUnsuccessful.label"));
                    notification.getStyleClass().add("error-notification");

                    notification.layoutXProperty().bind(rootPane.widthProperty().subtract(notification.widthProperty()).divide(2));
                    notification.layoutYProperty().bind(rootPane.heightProperty().subtract(notification.heightProperty()).subtract(50));

                    rootPane.getChildren().add(notification);

                    SequentialTransition sequence = SharedElements.getTransition(notification, rootPane);
                    sequence.play();
                }
            });

            params.add(label, 0, i);
            params.add(view, 1, i);
            params.add(edit, 2, i);
            params.add(delete, 3, i);
        }

        Scene scene = new Scene(root, previous.getWidth(), previous.getHeight());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/gui/" + UserSettings.colorPreset.getPrefix() + ".css")).toExternalForm());

        return scene;
    }

    /**
     * Gets a regular calculation scene and fills it with user saved parameters
     * <li> partially Gemini generated
     * @param param saved parameters
     * @return @return a {@code Scene} to be displayed
     */
    @SuppressWarnings("unchecked")
    private Scene getEditable(Parameters param) {
        RegularCalculationForm form = new RegularCalculationForm(previous);
        Scene scene = form.getScene();
        Platform.runLater(() -> {
            GridPane grid = (GridPane) scene.lookup("#params");
            ((TextField) getNodeFromGrid(grid, 1, 1)).textProperty().set(param.diameter().getValue() + "");
            ((ChoiceBox<Length.ELength>) getNodeFromGrid(grid, 2, 1)).setValue(param.diameter().getUnit());

            ((TextField) getNodeFromGrid(grid, 1, 2)).textProperty().set(param.mass().getValue() + "");
            ((ChoiceBox<Mass.EMass>) getNodeFromGrid(grid, 2, 2)).setValue(param.mass().getUnit());

            ((TextField) getNodeFromGrid(grid, 1, 3)).textProperty().set(param.velocity().getValue() + "");
            ((ChoiceBox<Speed.ESpeed>) getNodeFromGrid(grid, 2, 3)).setValue(param.velocity().getUnit());

            ((TextField) getNodeFromGrid(grid, 1, 4)).textProperty().set(param.balCoef().getValue() + "");
            ((ChoiceBox<BallisticCoefficient.EBallisticCoefficient>) getNodeFromGrid(grid, 2, 4)).setValue(param.balCoef().getType());

            ((TextField) getNodeFromGrid(grid, 1, 5)).textProperty().set(param.zeroRange().getValue() + "");
            ((ChoiceBox<Length.ELength>) getNodeFromGrid(grid, 2, 5)).setValue(param.zeroRange().getUnit());

            ((TextField) getNodeFromGrid(grid, 1, 6)).textProperty().set(param.sightHeight().getValue() + "");
            ((ChoiceBox<Length.ELength>) getNodeFromGrid(grid, 2, 6)).setValue(param.sightHeight().getUnit());

            ((TextField) getNodeFromGrid(grid, 1, 7)).textProperty().set(param.twistRate().getValue() + "");
            ((ChoiceBox<Length.ELength>) getNodeFromGrid(grid, 2, 7)).setValue(param.twistRate().getUnit());

            ((TextField) getNodeFromGrid(grid, 1, 8)).textProperty().set(param.temperature().getValue() + "");
            ((ChoiceBox<Temperature.ETemperature>) getNodeFromGrid(grid, 2, 8)).setValue(param.temperature().getUnit());

            ((TextField) getNodeFromGrid(grid, 1, 9)).textProperty().set(param.humidity() + "");

            ((TextField) getNodeFromGrid(grid, 1, 10)).textProperty().set(param.windSpeed().getValue() + "");
            ((ChoiceBox<Speed.ESpeed>) getNodeFromGrid(grid, 2, 10)).setValue(param.windSpeed().getUnit());

            ((TextField) ((VBox) getNodeFromGrid(grid, 1, 11)).getChildren().getFirst()).textProperty().set(param.windAzimuth().getValue() + "");
            ((ChoiceBox<Angle.EAngle>) getNodeFromGrid(grid, 2, 11)).setValue(param.windAzimuth().getUnit());

            ((RadioButton) getNodeFromGrid(grid, 1, 12)).setSelected(true);
            ((TextField) getNodeFromGrid(grid, 1, 13)).textProperty().set(param.pressure().get((Pressure.EPressure) UserSettings.defaultUnits.get("pressure"), 2) + "");
            ((ChoiceBox<Pressure.EPressure>) getNodeFromGrid(grid, 2, 13)).setValue((Pressure.EPressure) UserSettings.defaultUnits.get("pressure"));

            ((TextField) ((VBox) getNodeFromGrid(grid, 1, 14)).getChildren().getFirst()).textProperty().set(param.shotAngle().getValue() + "");
            ((ChoiceBox<Angle.EAngle>) getNodeFromGrid(grid, 2, 14)).setValue(param.shotAngle().getUnit());

            ((TextField) getNodeFromGrid(grid, 1, 15)).textProperty().set(param.maxRange().getValue() + "");
            ((ChoiceBox<Length.ELength>) getNodeFromGrid(grid, 2, 15)).setValue(param.maxRange().getUnit());

            ((TextField) getNodeFromGrid(grid, 1, 16)).textProperty().set(param.rangeStep().getValue() + "");
            ((ChoiceBox<Length.ELength>) getNodeFromGrid(grid, 2, 16)).setValue(param.rangeStep().getUnit());

            ((ChoiceBox<Length.ELength>) getNodeFromGrid(grid, 1, 18)).setValue(param.range());

            ((ChoiceBox<Length.ELength>) getNodeFromGrid(grid, 1, 19)).setValue(param.outputDeviationL());

            for (Node node : grid.getChildren()) {
                Integer rowIndex = GridPane.getRowIndex(node);
                if (rowIndex != null && rowIndex == 20 && node instanceof CheckBox cb) {
                    boolean match = Arrays.stream(param.outputDeviationA()).anyMatch(a -> a.getName().equalsIgnoreCase(cb.getText()));
                    cb.setSelected(match);
                }
            }

            ((CheckBox) getNodeFromGrid(grid, 1, 21)).setSelected(param.outputTime());
            ((CheckBox) getNodeFromGrid(grid, 2, 21)).setSelected(param.outputVelocity());
        });
        return scene;
    }

    /**
     * Gets node at a grid coordinates
     * <li> Gemini generated
     * @param gridPane the grid
     * @return a found node
     */
    public Node getNodeFromGrid(GridPane gridPane, int column, int row) {
        for (Node child : gridPane.getChildren()) {
            Integer colIndex = GridPane.getColumnIndex(child);
            Integer rowIndex = GridPane.getRowIndex(child);

            int c = (colIndex == null) ? 0 : colIndex;
            int r = (rowIndex == null) ? 0 : rowIndex;

            if (c == column && r == row) {
                return child;
            }
        }
        return null;
    }
}

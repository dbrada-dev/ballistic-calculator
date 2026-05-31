package dev.dbrada.ballistic_calculator.gui;

import dev.dbrada.ballistic_calculator.UserSettings;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * Handles the main menu and initial start-up
 */
public class Menu extends Application {

    /**
     * Constructs the gui menu
     * @param stage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     */
    @Override
    public void start(Stage stage){
        Pane root = new Pane();

        //settings button
        ImageView settingsIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/gui/icons/"+UserSettings.colorPreset.getPrefix()+"-settings.png"))));
        settingsIcon.setFitWidth(32);
        settingsIcon.setFitHeight(32);
        settingsIcon.setPreserveRatio(true);

        Button settingsButton = new Button();
        settingsButton.setPrefSize(32, 32);
        settingsButton.setGraphic(settingsIcon);

        settingsButton.setOnAction(
                (_) -> {
                    Scene current = settingsButton.getScene();
                    Settings settingsWindow = new Settings(current);
                    stage.setScene(settingsWindow.getScene());
                }
        );

        settingsButton.layoutXProperty().bind(root.widthProperty().multiply(1).subtract(settingsButton.widthProperty()));
        settingsButton.layoutYProperty().bind(root.heightProperty().multiply(0));
        root.getChildren().add(settingsButton);
        //

        //the options
        Label optionsLabel = new Label(UserSettings.getStr("options.label"));
        optionsLabel.setStyle("-fx-font-size: 20px");

        Pane spacer = new Pane();
        spacer.setPrefSize(1, 10);
        //regular calculation
        Button normalCalculation = new Button(UserSettings.getStr("normalCalculation.button"));
        normalCalculation.setPrefSize(400, 40);

        normalCalculation.setOnAction(
                (_) -> {
                    Scene current = settingsButton.getScene();
                    RegularCalculationForm formWindow = new RegularCalculationForm(current);
                    stage.setScene(formWindow.getScene());
                }
        );
        //

        //simple calculation form
        Button simpleCalculation = new Button(UserSettings.getStr("simpleCalculation.button"));
        simpleCalculation.setPrefSize(400, 40);

        simpleCalculation.setOnAction(
                (_) -> {
                    Scene current = settingsButton.getScene();
                    SimpleCalculationForm formWindow = new SimpleCalculationForm(current);
                    stage.setScene(formWindow.getScene());
                }
        );
        //

        VBox options = new VBox(optionsLabel, spacer, normalCalculation, simpleCalculation);
        options.setAlignment(Pos.CENTER);

        options.layoutXProperty().bind(root.widthProperty().multiply(0.5).subtract(options.widthProperty().divide(2)));
        options.layoutYProperty().bind(root.heightProperty().multiply(0.5).subtract(options.heightProperty().divide(2)));
        root.getChildren().add(options);
        //



        Scene scene = new Scene(root, 1920/3.0, 1080*2/3.0);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/gui/" + UserSettings.colorPreset.getPrefix() + ".css")).toExternalForm());

        stage.setTitle("Ballistic Calculator");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}

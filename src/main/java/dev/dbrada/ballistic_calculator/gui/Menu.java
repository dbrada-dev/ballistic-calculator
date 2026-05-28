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

public class Menu extends Application {
    @Override
    public void start(Stage stage){
        Pane root = new Pane();

        //settings button
        ImageView settingsIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/gui/icons/"+UserSettings.colorPreset+"-settings.png"))));
        settingsIcon.setFitWidth(32);
        settingsIcon.setFitHeight(32);
        settingsIcon.setPreserveRatio(true);

        Button settings = new Button();
        settings.setPrefSize(32, 32);
        settings.setGraphic(settingsIcon);

        settings.setOnAction(
                (evt) -> {
                    //TODO
                }
        );

        settings.layoutXProperty().bind(root.widthProperty().multiply(1).subtract(settings.widthProperty()));
        settings.layoutYProperty().bind(root.heightProperty().multiply(0));
        root.getChildren().add(settings);

        //the options
            Label optionsLabel = new Label("Options");
            optionsLabel.setStyle("-fx-font-size: 20px");

            Pane spacer = new Pane();
            spacer.setPrefSize(1, 10);
            //regular calculation
            Button normalCalculation = new Button("Calculate ballistic curve");
            normalCalculation.setPrefSize(400, 40);

        VBox options = new VBox(optionsLabel, spacer, normalCalculation);
        options.setAlignment(Pos.CENTER);

        options.layoutXProperty().bind(root.widthProperty().multiply(0.5).subtract(options.widthProperty().divide(2)));
        options.layoutYProperty().bind(root.heightProperty().multiply(0.5).subtract(options.heightProperty().divide(2)));
        root.getChildren().add(options);



        Scene scene = new Scene(root, 640, 360);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/gui/" + UserSettings.colorPreset + ".css")).toExternalForm());

        stage.setTitle("Menu - Ballistic Calculator");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}

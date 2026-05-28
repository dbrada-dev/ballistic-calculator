package dev.dbrada.ballistic_calculator.gui;

import dev.dbrada.ballistic_calculator.UserSettings;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.Objects;

public class Settings {
    private final Scene previous;

    private final ChoiceBox<UserSettings.Colors> colorPreset;

    public Settings(Scene previous) {
        this.previous = previous;
        this.colorPreset = new ChoiceBox<>();
    }

    public Scene getScene() {
        Pane root = new Pane();

        //back button
        Button back = new Button("<- Go Back");
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

        //color preset
        colorPresetInit();

        colorPreset.layoutXProperty().bind(root.widthProperty().multiply(0.05));
        colorPreset.layoutYProperty().bind(back.heightProperty().add(10));
        root.getChildren().add(colorPreset);

        //save
        Button save = new Button("Save");
        save.layoutXProperty().bind(root.widthProperty().subtract(save.widthProperty()));
        save.layoutYProperty().bind(root.heightProperty().multiply(0));

        root.getChildren().add(save);

        save.setOnAction(
                (_) -> {
                    UserSettings.colorPreset = colorPreset.getValue();
                    UserSettings.save();
                    Stage stage = (Stage) save.getScene().getWindow();
                    stage.setScene(new Settings(save.getScene()).getScene());
                }
        );

        Scene scene = new Scene(root, previous.getWidth(), previous.getHeight());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/gui/" + UserSettings.colorPreset.getPrefix() + ".css")).toExternalForm());

        return scene;
    }

    private void colorPresetInit() {
        colorPreset.getItems().addAll(UserSettings.Colors.values());
        colorPreset.setValue(UserSettings.colorPreset);
        colorPreset.setPrefSize(240, 40);

        colorPreset.setConverter(new StringConverter<>() {
            @Override
            public String toString(UserSettings.Colors colors) {
                return colors.getName();
            }

            @Override
            public UserSettings.Colors fromString(String s) {
                return UserSettings.Colors.valueOf(s);
            }
        });
    }
}

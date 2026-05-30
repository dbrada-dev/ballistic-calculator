package dev.dbrada.ballistic_calculator.gui;

import dev.dbrada.ballistic_calculator.UserSettings;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.Objects;

/**
 * Handles user setting gui
 */
public class Settings {
    private final Scene previous;

    private final ChoiceBox<UserSettings.Languages> lang;
    private final ChoiceBox<UserSettings.Colors> colorPreset;

    public Settings(Scene previous) {
        this.previous = previous;
        this.lang = new ChoiceBox<>();
        this.colorPreset = new ChoiceBox<>();
    }

    /**
     * Constructs the gui display
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

        //settings
        VBox settings = new VBox();

        settings.layoutXProperty().bind(root.widthProperty().multiply(0.025));
        settings.layoutYProperty().bind(back.heightProperty().add(10));
        settings.setAlignment(Pos.CENTER_LEFT);
        root.getChildren().add(settings);
        //

        //language
        langInit();
        Label langLabel = new Label(UserSettings.getStr("language.label"));
        HBox langBox = new HBox(langLabel, lang);
        langBox.setAlignment(Pos.CENTER_LEFT);
        Pane spacer0 = new Pane();
        spacer0.setPrefSize(0,5);
        settings.getChildren().addAll(spacer0, langBox);
        //

        //color preset
        colorPresetInit();
        Label colorPresetLabel = new Label(UserSettings.getStr("scheme.label"));
        HBox colorPresetBox = new HBox(colorPresetLabel, colorPreset);
        colorPresetBox.setAlignment(Pos.CENTER_LEFT);
        Pane spacer1 = new Pane();
        spacer1.setPrefSize(0,5);
        settings.getChildren().addAll(spacer1, colorPresetBox);
        //

        //save
        Button save = new Button(UserSettings.getStr("save.button"));
        save.layoutXProperty().bind(root.widthProperty().subtract(save.widthProperty()));
        save.layoutYProperty().bind(root.heightProperty().multiply(0));

        root.getChildren().add(save);

        save.setOnAction(
                (_) -> {
                    UserSettings.colorPreset = colorPreset.getValue();
                    UserSettings.lang = lang.getValue();
                    UserSettings.save();
                    Stage stage = (Stage) save.getScene().getWindow();
                    stage.setScene(new Settings(save.getScene()).getScene());
                }
        );
        //

        Scene scene = new Scene(root, previous.getWidth(), previous.getHeight());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/gui/" + UserSettings.colorPreset.getPrefix() + ".css")).toExternalForm());

        return scene;
    }

    /**
     * Initializes language options
     */
    private void langInit() {
        lang.getItems().addAll(UserSettings.Languages.values());
        lang.setValue(UserSettings.lang);
        lang.setPrefSize(240, 40);

        lang.setConverter(new StringConverter<>() {
            @Override
            public String toString(UserSettings.Languages languages) {
                return languages.getName();
            }

            @Override
            public UserSettings.Languages fromString(String s) {
                for (UserSettings.Languages l :UserSettings.Languages.values()) {
                    if (l.getName().equalsIgnoreCase(s)) return l;
                }
                return UserSettings.Languages.EN_US;
            }
        });
    }

    /**
     * Initializes color preset options
     */
    private void colorPresetInit() {
        colorPreset.getItems().addAll(UserSettings.Colors.values());
        colorPreset.setValue(UserSettings.colorPreset);
        colorPreset.setPrefSize(240, 40);

        colorPreset.setConverter(new StringConverter<>() {
            @Override
            public String toString(UserSettings.Colors colors) {
                return UserSettings.getStr(colors.getName());
            }

            @Override
            public UserSettings.Colors fromString(String s) {
                for (UserSettings.Colors c : UserSettings.Colors.values()) {
                    if (c.getName().equalsIgnoreCase(s)) return c;
                }
                return UserSettings.Colors.DARK;
            }
        });
    }
}

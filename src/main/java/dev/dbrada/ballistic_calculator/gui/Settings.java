package dev.dbrada.ballistic_calculator.gui;

import dev.dbrada.ballistic_calculator.Constants;
import dev.dbrada.ballistic_calculator.UserSettings;
import dev.dbrada.ballistic_calculator.units.NamedUnit;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        //grid
        GridPane settings = new GridPane();
        settings.setAlignment(Pos.CENTER_LEFT);
        settings.setHgap(10);
        settings.setVgap(10);
        ScrollPane scrollParams = new ScrollPane(settings);
        scrollParams.layoutXProperty().bind(root.widthProperty().multiply(0.025));
        scrollParams.layoutYProperty().bind(back.heightProperty().add(10));
        scrollParams.setPrefSize(previous.getWidth()*0.95, previous.getHeight()-50);
        root.getChildren().add(scrollParams);
        //

        //settings
        //general
        //language
        langInit();
        Label langLabel = new Label(UserSettings.getStr("language.label"));
        settings.add(langLabel, 0, 1);
        settings.add(lang, 1, 1);
        //
        //color preset
        colorPresetInit();
        Label colorPresetLabel = new Label(UserSettings.getStr("scheme.label"));
        settings.add(colorPresetLabel, 0, 2);
        settings.add(colorPreset, 1, 2);
        //
        //
        //default units
        List<ChoiceBox<Enum<?>>> defaultUnitChoices = new ArrayList<>();
        List<Label> defaultUnitLabels = new ArrayList<>();
        List<String> defaultUnitNames = new ArrayList<>();

        defaultUnitInit(defaultUnitNames, defaultUnitLabels, defaultUnitChoices);

        for (int i = 0; i < defaultUnitLabels.size(); i++) {
            settings.add(defaultUnitLabels.get(i), 0, i+4);
            settings.add(defaultUnitChoices.get(i), 1, i+4);
        }
        //
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
                    UserSettings.updateLang();

                    for (int i = 0; i < defaultUnitNames.size(); i++) {
                        UserSettings.defaultUnits.put(defaultUnitNames.get(i), defaultUnitChoices.get(i).getValue());
                    }

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

    /**
     * Prepares default unit choice
     * @param defaultUnitNames Stores keys for saving
     * @param defaultUnitLabels Stores {@code Label}s for gui
     * @param defaultUnitChoices Stores the setup {@code ChoiceBox}es
     */
    private void defaultUnitInit(List<String> defaultUnitNames, List<Label> defaultUnitLabels, List<ChoiceBox<Enum<?>>> defaultUnitChoices) {
        for (Map.Entry<String, Enum<?>> e : UserSettings.defaultUnits.entrySet()) {
            defaultUnitNames.add(e.getKey());
            defaultUnitLabels.add(new Label(UserSettings.getStr(e.getKey() + ".label")));
            ChoiceBox<Enum<?>> ch = new ChoiceBox<>();
            ch.getItems().addAll(Constants.ALLOWED_UNITS.get(e.getKey()));
            ch.setValue(e.getValue());
            ch.setPrefSize(100, 30);
            ch.setConverter(new StringConverter<>() {
                @Override
                public String toString(Enum<?> object) {
                    return ((NamedUnit) object).getName();
                }

                @Override
                public Enum<?> fromString(String string) {
                    if (string == null || string.isBlank()) {
                        return UserSettings.defaultUnits.get(e.getKey());
                    }
                    for (Enum<?> item : ch.getItems()) {
                        NamedUnit nu = (NamedUnit) item;
                        if (nu.getName().equalsIgnoreCase(string)) {
                            return item;
                        }
                    }
                    return UserSettings.defaultUnits.get(e.getKey());
                }
            });
            defaultUnitChoices.add(ch);
        }
    }
}

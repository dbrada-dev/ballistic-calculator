package dev.dbrada.ballistic_calculator.gui;

import dev.dbrada.ballistic_calculator.Constants;
import dev.dbrada.ballistic_calculator.UserSettings;
import dev.dbrada.ballistic_calculator.units.Length;
import dev.dbrada.ballistic_calculator.units.Mass;
import dev.dbrada.ballistic_calculator.units.Speed;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.AllArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
public class RegularCalculationForm {
    private final Scene previous;

    @SuppressWarnings("unchecked")
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
        params.layoutXProperty().bind(root.widthProperty().multiply(0.025));
        params.layoutYProperty().bind(back.heightProperty().add(10));
        params.setAlignment(Pos.CENTER_LEFT);
        params.setHgap(10);
        params.setVgap(10);
        root.getChildren().add(params);
        //
        //projectile
        //diameter
        Object[] diameter = diameterInit();
        params.add((Label) diameter[0], 0, 0);
        params.add((HBox) diameter[1], 1, 0);
        params.add((ChoiceBox<Length.ELength>) diameter[2], 2, 0);
        //
        //mass
        Object[] mass = massInit();
        params.add((Label) mass[0], 0, 1);
        params.add((HBox) mass[1], 1 ,1);
        params.add((ChoiceBox<Mass.EMass>) mass[2], 2, 1);
        //
        //velocity
        Object[] velocity = velocityInit();
        params.add((Label) velocity[0], 0, 2);
        params.add((HBox) velocity[1], 1 ,2);
        params.add((ChoiceBox<Speed.ESpeed>) velocity[2], 2, 2);
        //
        //ballistic coefficient
            //TODO
        //
        //

        //rifle setup
        //zero range
        Object[] zeroRange = zeroRangeInit();
        params.add((Label) zeroRange[0], 0,4);
        params.add((HBox) zeroRange[1], 1, 4);
        params.add((ChoiceBox<Length.ELength>) zeroRange[2], 2, 4);
        //
        //

        //

        //calculate
        Button calculate = new Button(UserSettings.getStr("calculate.button"));
        calculate.layoutXProperty().bind(root.widthProperty().subtract(calculate.widthProperty()));
        calculate.layoutYProperty().bind(root.heightProperty().multiply(0));

        root.getChildren().add(calculate);

        calculate.setOnAction(
                (_) -> {

                }
        );
        //

        Scene scene = new Scene(root, previous.getWidth(), previous.getHeight());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/gui/" + UserSettings.colorPreset.getPrefix() + ".css")).toExternalForm());

        return scene;
    }

    private Object[] diameterInit() {
        Label diameterLabel = new Label(UserSettings.getStr("diameter.label"));
        Label diameterReq = new Label("*");

        diameterReq.setStyle("-fx-font-size: 14px; -fx-text-fill: #FF4040");
        TextField diameterText = new TextField();
        diameterText.setPrefSize(180, 40);
        HBox diameterTextBox = new HBox(diameterReq, diameterText);

        ChoiceBox<Length.ELength> diameterChoice = new ChoiceBox<>();
        diameterChoice.setPrefSize(100, 40);
        diameterChoice.getItems().addAll((Length.ELength[]) Constants.ALLOWED_UNITS.get("diameter"));
        diameterChoice.setValue((Length.ELength) UserSettings.defaultUnits.get("diameter"));
        diameterChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(Length.ELength eLength) {
                return eLength.getName();
            }

            @Override
            public Length.ELength fromString(String s) {
                for (Length.ELength e: (Length.ELength[]) Constants.ALLOWED_UNITS.get("diameter")) {
                    if (e.getName().equalsIgnoreCase("s")) return e;
                }
                return (Length.ELength) UserSettings.defaultUnits.get("diameter");
            }
        });

        Length min = new Length(1, Length.ELength.MM);
        Length max = new Length(30, Length.ELength.MM);
        diameterText.setPromptText(Math.ceil(min.get(diameterChoice.getValue(), 3)*100)/100.0 + "-" + Math.floor(max.get(diameterChoice.getValue(), 3)*100)/100.0);

        ObjectProperty<Double> diameterValue = new SimpleObjectProperty<>(null);

        diameterChoice.valueProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != oldValue) {
                        diameterText.setPromptText(Math.ceil(min.get(diameterChoice.getValue(), 3)*100)/100.0 + "-" + Math.floor(max.get(diameterChoice.getValue(), 3)*100)/100.0);
                    }
                }
        );

        diameterText.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty() || newText.matches("\\d+[.,]?\\d*")) {
                return change;
            }
            return null;
        }));

        diameterText.textProperty().addListener((observable, oldValue, newValue) -> {
            newValue = newValue.replace(',','.');
            try {
                diameterValue.set(Double.parseDouble(newValue));
            } catch (NumberFormatException _) {
                diameterValue.set(null);
            }
        });

        BooleanBinding isDiameterInvalid = Bindings.createBooleanBinding(() -> {
            Double val = diameterValue.get();
            if (val == null) return true;
            return val < min.get(diameterChoice.getValue()) || val > max.get(diameterChoice.getValue());
        }, diameterChoice.valueProperty(), diameterValue, diameterText.getProperties());

        diameterText.styleProperty().bind(Bindings.when(isDiameterInvalid).then("-fx-border-color: red;").otherwise(""));

        Object[] result = new Object[5];
        result[0] = diameterLabel;
        result[1] = diameterTextBox;
        result[2] = diameterChoice;
        result[3] = isDiameterInvalid;
        result[4] = diameterValue;

        return result;
    }

    private Object[] massInit() {
        Label massLabel = new Label(UserSettings.getStr("mass.label"));
        Label massReq = new Label("*");

        massReq.setStyle("-fx-font-size: 14px; -fx-text-fill: #FF4040");
        TextField massText = new TextField();
        massText.setPrefSize(180, 40);
        HBox massTextBox = new HBox(massReq, massText);

        ChoiceBox<Mass.EMass> massChoice = new ChoiceBox<>();
        massChoice.setPrefSize(100, 40);
        massChoice.getItems().addAll((Mass.EMass[]) Constants.ALLOWED_UNITS.get("mass"));
        massChoice.setValue((Mass.EMass) UserSettings.defaultUnits.get("mass"));
        massChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(Mass.EMass eMass) {
                return eMass.getName();
            }

            @Override
            public Mass.EMass fromString(String s) {
                for (Mass.EMass e: (Mass.EMass[]) Constants.ALLOWED_UNITS.get("mass")) {
                    if (e.getName().equalsIgnoreCase("s")) return e;
                }
                return (Mass.EMass) UserSettings.defaultUnits.get("mass");
            }
        });

        Mass min = new Mass(10, Mass.EMass.GR);
        Mass max = new Mass(1500, Mass.EMass.GR);
        massText.setPromptText(Math.ceil(min.get(massChoice.getValue(), 3)*100)/100.0 + "-" + Math.floor(max.get(massChoice.getValue(), 3)*100)/100.0);

        ObjectProperty<Double> massValue = new SimpleObjectProperty<>(null);

        massChoice.valueProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != oldValue) {
                        massText.setPromptText(Math.ceil(min.get(massChoice.getValue(), 3)*100)/100.0 + "-" + Math.floor(max.get(massChoice.getValue(), 3)*100)/100.0);
                    }
                }
        );

        massText.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty() || newText.matches("\\d+[.,]?\\d*")) {
                return change;
            }
            return null;
        }));

        massText.textProperty().addListener((observable, oldValue, newValue) -> {
            newValue = newValue.replace(',','.');
            try {
                massValue.set(Double.parseDouble(newValue));
            } catch (NumberFormatException _) {
                massValue.set(null);
            }
        });

        BooleanBinding isMassInvalid = Bindings.createBooleanBinding(() -> {
            Double val = massValue.get();
            if (val == null) return true;
            return val < min.get(massChoice.getValue()) || val > max.get(massChoice.getValue());
        }, massChoice.valueProperty(), massValue, massText.getProperties());

        massText.styleProperty().bind(Bindings.when(isMassInvalid).then("-fx-border-color: red;").otherwise(""));

        Object[] result = new Object[5];
        result[0] = massLabel;
        result[1] = massTextBox;
        result[2] = massChoice;
        result[3] = isMassInvalid;
        result[4] = massValue;

        return result;
    }

    private Object[] velocityInit() {
        Label velocityLabel = new Label(UserSettings.getStr("velocity.label"));
        Label velocityReq = new Label("*");

        velocityReq.setStyle("-fx-font-size: 14px; -fx-text-fill: #FF4040");
        TextField velocityText = new TextField();
        velocityText.setPrefSize(180, 40);
        HBox velocityTextBox = new HBox(velocityReq, velocityText);

        ChoiceBox<Speed.ESpeed> velocityChoice = new ChoiceBox<>();
        velocityChoice.setPrefSize(100, 40);
        velocityChoice.getItems().addAll((Speed.ESpeed[]) Constants.ALLOWED_UNITS.get("velocity"));
        velocityChoice.setValue((Speed.ESpeed) UserSettings.defaultUnits.get("velocity"));
        velocityChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(Speed.ESpeed eSpeed) {
                return eSpeed.getName();
            }

            @Override
            public Speed.ESpeed fromString(String s) {
                for (Speed.ESpeed e: (Speed.ESpeed[]) Constants.ALLOWED_UNITS.get("velocity")) {
                    if (e.getName().equalsIgnoreCase("s")) return e;
                }
                return (Speed.ESpeed) UserSettings.defaultUnits.get("velocity");
            }
        });

        Speed min = new Speed(25, Speed.ESpeed.MPS);
        Speed max = new Speed(1600, Speed.ESpeed.MPS);
        velocityText.setPromptText(Math.ceil(min.get(velocityChoice.getValue(), 3)*100)/100.0 + "-" + Math.floor(max.get(velocityChoice.getValue(), 3)*100)/100.0);

        ObjectProperty<Double> velocityValue = new SimpleObjectProperty<>(null);

        velocityChoice.valueProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != oldValue) {
                        velocityText.setPromptText(Math.ceil(min.get(velocityChoice.getValue(), 3)*100)/100.0 + "-" + Math.floor(max.get(velocityChoice.getValue(), 3)*100)/100.0);
                    }
                }
        );

        velocityText.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty() || newText.matches("\\d+[.,]?\\d*")) {
                return change;
            }
            return null;
        }));

        velocityText.textProperty().addListener((observable, oldValue, newValue) -> {
            newValue = newValue.replace(',','.');
            try {
                velocityValue.set(Double.parseDouble(newValue));
            } catch (NumberFormatException _) {
                velocityValue.set(null);
            }
        });

        BooleanBinding isVelocityInvalid = Bindings.createBooleanBinding(() -> {
            Double val = velocityValue.get();
            if (val == null) return true;
            return val < min.get(velocityChoice.getValue()) || val > max.get(velocityChoice.getValue());
        }, velocityChoice.valueProperty(), velocityValue, velocityText.getProperties());

        velocityText.styleProperty().bind(Bindings.when(isVelocityInvalid).then("-fx-border-color: red;").otherwise(""));

        Object[] result = new Object[5];
        result[0] = velocityLabel;
        result[1] = velocityTextBox;
        result[2] = velocityChoice;
        result[3] = isVelocityInvalid;
        result[4] = velocityValue;

        return result;
    }

    private Object[] zeroRangeInit() {
        Label zeroRangeLabel = new Label(UserSettings.getStr("zeroRange.label"));
        Label zeroRangeReq = new Label("*");

        zeroRangeReq.setStyle("-fx-font-size: 14px; -fx-text-fill: #FF4040");
        TextField zeroRangeText = new TextField();
        zeroRangeText.setPrefSize(180, 40);
        HBox zeroRangeTextBox = new HBox(zeroRangeReq, zeroRangeText);

        ChoiceBox<Length.ELength> zeroRangeChoice = new ChoiceBox<>();
        zeroRangeChoice.setPrefSize(100, 40);
        zeroRangeChoice.getItems().addAll((Length.ELength[]) Constants.ALLOWED_UNITS.get("zeroRange"));
        zeroRangeChoice.setValue((Length.ELength) UserSettings.defaultUnits.get("zeroRange"));
        zeroRangeChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(Length.ELength eLength) {
                return eLength.getName();
            }

            @Override
            public Length.ELength fromString(String s) {
                for (Length.ELength e: (Length.ELength[]) Constants.ALLOWED_UNITS.get("zeroRange")) {
                    if (e.getName().equalsIgnoreCase("s")) return e;
                }
                return (Length.ELength) UserSettings.defaultUnits.get("zeroRange");
            }
        });

        Length min = new Length(5, Length.ELength.M);
        Length max = new Length(500, Length.ELength.M);
        zeroRangeText.setPromptText(Math.ceil(min.get(zeroRangeChoice.getValue(), 3)*100)/100.0 + "-" + Math.floor(max.get(zeroRangeChoice.getValue(), 3)*100)/100.0);

        ObjectProperty<Double> zeroRangeValue = new SimpleObjectProperty<>(null);

        zeroRangeChoice.valueProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != oldValue) {
                        zeroRangeText.setPromptText(Math.ceil(min.get(zeroRangeChoice.getValue(), 3)*100)/100.0 + "-" + Math.floor(max.get(zeroRangeChoice.getValue(), 3)*100)/100.0);
                    }
                }
        );

        zeroRangeText.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty() || newText.matches("\\d+[.,]?\\d*")) {
                return change;
            }
            return null;
        }));

        zeroRangeText.textProperty().addListener((observable, oldValue, newValue) -> {
            newValue = newValue.replace(',','.');
            try {
                zeroRangeValue.set(Double.parseDouble(newValue));
            } catch (NumberFormatException _) {
                zeroRangeValue.set(null);
            }
        });

        BooleanBinding isZeroRangeInvalid = Bindings.createBooleanBinding(() -> {
            Double val = zeroRangeValue.get();
            if (val == null) return true;
            return val < min.get(zeroRangeChoice.getValue()) || val > max.get(zeroRangeChoice.getValue());
        }, zeroRangeChoice.valueProperty(), zeroRangeValue, zeroRangeText.getProperties());

        zeroRangeText.styleProperty().bind(Bindings.when(isZeroRangeInvalid).then("-fx-border-color: red;").otherwise(""));

        Object[] result = new Object[5];
        result[0] = zeroRangeLabel;
        result[1] = zeroRangeTextBox;
        result[2] = zeroRangeChoice;
        result[3] = isZeroRangeInvalid;
        result[4] = zeroRangeValue;

        return result;
    }
}

package dev.dbrada.ballistic_calculator.gui;

import dev.dbrada.ballistic_calculator.*;
import dev.dbrada.ballistic_calculator.units.*;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        params.setAlignment(Pos.CENTER_LEFT);
        params.setHgap(10);
        params.setVgap(10);
        ScrollPane scrollParams = new ScrollPane(params);
        scrollParams.layoutXProperty().bind(root.widthProperty().multiply(0.025));
        scrollParams.layoutYProperty().bind(back.heightProperty().add(10));
        scrollParams.setPrefSize(previous.getWidth()*0.95, previous.getHeight()-50);
        root.getChildren().add(scrollParams);
        //
        //input label
        Label inputs = new Label(UserSettings.getStr("inputs.label"));
        inputs.setStyle("-fx-font-size: 20px");
        params.add(inputs, 0, 0);
        //
        //projectile
        //diameter
        Object[] diameter = diameterInit();
        params.add((Label) diameter[0], 0, 1);
        params.add((TextField) diameter[1], 1, 1);
        params.add((ChoiceBox<Length.ELength>) diameter[2], 2, 1);
        //
        //mass
        Object[] mass = massInit();
        params.add((Label) mass[0], 0, 2);
        params.add((TextField) mass[1], 1 ,2);
        params.add((ChoiceBox<Mass.EMass>) mass[2], 2, 2);
        //
        //velocity
        Object[] velocity = velocityInit();
        params.add((Label) velocity[0], 0, 3);
        params.add((TextField) velocity[1], 1 ,3);
        params.add((ChoiceBox<Speed.ESpeed>) velocity[2], 2, 3);
        //
        //ballistic coefficient
        Object[] balCoef = balCoefInit();
        params.add((Label) balCoef[0], 0, 4);
        params.add((TextField) balCoef[1], 1 ,4);
        params.add((ChoiceBox<BallisticCoefficient.EBallisticCoefficient>) balCoef[2], 2, 4);
        //
        //
        //rifle setup
        //zero range
        Object[] zeroRange = zeroRangeInit();
        params.add((Label) zeroRange[0], 0,5);
        params.add((TextField) zeroRange[1], 1, 5);
        params.add((ChoiceBox<Length.ELength>) zeroRange[2], 2, 5);
        //
        //sight height
        Object[] sightHeight = sightHeightInit();
        params.add((Label) sightHeight[0], 0, 6);
        params.add((TextField) sightHeight[1], 1, 6);
        params.add((ChoiceBox<Length.ELength>) sightHeight[2], 2, 6);
        //
        //twist rate
        Object[] twistRate = twistRateInit();
        params.add((Label) twistRate[0], 0, 7);
        params.add((TextField) twistRate[1], 1, 7);
        params.add((ChoiceBox<Length.ELength>) twistRate[2], 2, 7);
        //
        //temperature
        Object[] temperature = temperatureInit();
        params.add((Label) temperature[0], 0, 8);
        params.add((TextField) temperature[1], 1, 8);
        params.add((ChoiceBox<Length.ELength>) temperature[2], 2, 8);
        //
        //humidity
        Object[] humidity = humidityInit();
        params.add((Label) humidity[0], 0, 9);
        params.add((TextField) humidity[1], 1, 9);
        params.add((Label) humidity[2], 2, 9);
        //
        //wind speed
        Object[] windSpeed = windSpeedInit();
        params.add((Label) windSpeed[0], 0, 10);
        params.add((TextField) windSpeed[1], 1, 10);
        params.add((ChoiceBox<Speed.ESpeed>) windSpeed[2], 2, 10);
        //
        //wind azimuth
        Object[] windAzimuth = windAzimuthInit();
        params.add((Label) windAzimuth[0], 0, 11);
        params.add(new VBox((TextField) windAzimuth[1], (Label) windAzimuth[5]), 1, 11);
        params.add((ChoiceBox<Angle.EAngle>) windAzimuth[2], 2, 11);
        //
        //altitude/pressure
        ToggleGroup group = new ToggleGroup();
        RadioButton altitudeOpt = new RadioButton(UserSettings.getStr("altitude.opt"));
        RadioButton pressureOpt = new RadioButton(UserSettings.getStr("pressure.opt"));
        altitudeOpt.setToggleGroup(group);
        pressureOpt.setToggleGroup(group);
        params.add(altitudeOpt, 0, 12);
        params.add(pressureOpt, 1, 12);

        Object[] altitude = altitudeInit();
        Object[] pressure = pressureInit();

        altitudeOpt.selectedProperty().addListener((_, _, isSelected) -> {
            if (isSelected) {
                params.getChildren().remove((Label) pressure[0]);
                params.getChildren().remove((TextField) pressure[1]);
                params.getChildren().remove((ChoiceBox<Pressure.EPressure>) pressure[2]);
                params.add((Label) altitude[0], 0, 13);
                params.add((TextField) altitude[1], 1, 13);
                params.add((ChoiceBox<Length.ELength>) altitude[2], 2, 13);
            }
        });
        pressureOpt.selectedProperty().addListener((_, _, isSelected) -> {
            if (isSelected) {
                params.getChildren().remove((Label) altitude[0]);
                params.getChildren().remove((TextField) altitude[1]);
                params.getChildren().remove((ChoiceBox<Length.ELength>) altitude[2]);
                params.add((Label) pressure[0], 0, 13);
                params.add((TextField) pressure[1], 1, 13);
                params.add((ChoiceBox<Pressure.EPressure>) pressure[2], 2, 13);
            }
        });
        altitudeOpt.setSelected(true);
        //
        //shot angle
        Object[] shotAngle = shotAngleInit();
        params.add((Label) shotAngle[0], 0, 14);
        params.add(new VBox((TextField) shotAngle[1], (Label) shotAngle[5]), 1, 14);
        params.add((ChoiceBox<Angle.EAngle>) shotAngle[2], 2, 14);
        //
        //max range
        Object[] maxRange = maxRangeInit();
        params.add((Label) maxRange[0], 0,15);
        params.add((TextField) maxRange[1], 1, 15);
        params.add((ChoiceBox<Length.ELength>) maxRange[2], 2, 15);
        //
        //range step
        Object[] rangeStep = rangeStepInit();
        params.add((Label) rangeStep[0], 0,16);
        params.add((TextField) rangeStep[1], 1, 16);
        params.add((ChoiceBox<Length.ELength>) rangeStep[2], 2, 16);
        //
        //
        //output label
        Label outputs = new Label(UserSettings.getStr("outputs.label"));
        outputs.setStyle("-fx-font-size: 20px");
        params.add(outputs, 0, 17);
        //
        //range units
        Label rangeUnitLabel = new Label(UserSettings.getStr("range.label"));
        ChoiceBox<Length.ELength> rangeUnitChoice = new ChoiceBox<>();
        rangeUnitChoice.setPrefSize(100, 40);
        rangeUnitChoice.getItems().addAll((Length.ELength[]) Constants.ALLOWED_UNITS.get("range"));
        rangeUnitChoice.setValue((Length.ELength) UserSettings.defaultUnits.get("range"));
        rangeUnitChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(Length.ELength eLength) {
                return eLength.getName();
            }

            @Override
            public Length.ELength fromString(String s) {
                for (Length.ELength e: (Length.ELength[]) Constants.ALLOWED_UNITS.get("range")) {
                    if (e.getName().equalsIgnoreCase(s)) return e;
                }
                return (Length.ELength) UserSettings.defaultUnits.get("range");
            }
        });
        params.add(rangeUnitLabel, 0, 18);
        params.add(rangeUnitChoice, 1, 18);
        //
        //deviation length
        Label deviationLLabel = new Label(UserSettings.getStr("deviationL.label"));
        ChoiceBox<Length.ELength> deviationLChoice = new ChoiceBox<>();
        deviationLChoice.setPrefSize(100, 40);
        deviationLChoice.getItems().addAll((Length.ELength[]) Constants.ALLOWED_UNITS.get("deviationL"));
        deviationLChoice.setValue((Length.ELength) UserSettings.defaultUnits.get("deviationL"));
        deviationLChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(Length.ELength eLength) {
                return eLength.getName();
            }

            @Override
            public Length.ELength fromString(String s) {
                for (Length.ELength e: (Length.ELength[]) Constants.ALLOWED_UNITS.get("deviationL")) {
                    if (e.getName().equalsIgnoreCase(s)) return e;
                }
                return (Length.ELength) UserSettings.defaultUnits.get("deviationL");
            }
        });
        params.add(deviationLLabel, 0, 19);
        params.add(deviationLChoice, 1, 19);
        //
        //deviation angle
        Label deviationALabel = new Label(UserSettings.getStr("deviationA.label"));
        params.add(deviationALabel, 0, 20);
        CheckBox[] deviationABoxes = new CheckBox[Constants.ALLOWED_UNITS.get("deviationA").length];
        for (int i = 0; i < deviationABoxes.length; i++) {
            deviationABoxes[i] = new CheckBox(((Angle.EAngle) Constants.ALLOWED_UNITS.get("deviationA")[i]).getName());
            if (((Angle.EAngle) Constants.ALLOWED_UNITS.get("deviationA")[i]).getName().equalsIgnoreCase(((Angle.EAngle) UserSettings.defaultUnits.get("deviationA")).getName())) {
                deviationABoxes[i].setSelected(true);
            }
            params.add(deviationABoxes[i], i+1, 20);
        }
        //
        //other params
        Label othersLabel = new Label(UserSettings.getStr("others.label"));
        CheckBox othersTime = new CheckBox(UserSettings.getStr("time.box"));
        CheckBox othersVelocity = new CheckBox(UserSettings.getStr("velocity.box"));
        params.add(othersLabel, 0, 21);
        params.add(othersTime, 1, 21);
        params.add(othersVelocity, 2, 21);
        //
        //

        //calculate
        Button calculate = new Button(UserSettings.getStr("calculate.button"));
        calculate.layoutXProperty().bind(root.widthProperty().subtract(calculate.widthProperty()));
        calculate.layoutYProperty().bind(root.heightProperty().multiply(0));

        root.getChildren().add(calculate);

        ObservableBooleanValue[] baseFields = {
                (ObservableBooleanValue) diameter[3],
                (ObservableBooleanValue) mass[3],
                (ObservableBooleanValue) velocity[3],
                (ObservableBooleanValue) balCoef[3],
                (ObservableBooleanValue) zeroRange[3],
                (ObservableBooleanValue) sightHeight[3],
                (ObservableBooleanValue) twistRate[3],
                (ObservableBooleanValue) temperature[3],
                (ObservableBooleanValue) humidity[3],
                (ObservableBooleanValue) windSpeed[3],
                (ObservableBooleanValue) windAzimuth[3],
                (ObservableBooleanValue) shotAngle[3],
                (ObservableBooleanValue) maxRange[3],
                (ObservableBooleanValue) rangeStep[3]
        };

        ObservableBooleanValue altitudeField = (ObservableBooleanValue) altitude[3];
        ObservableBooleanValue pressureField = (ObservableBooleanValue) pressure[3];

        List<Observable> dependencies = new ArrayList<>(Arrays.asList(baseFields));
        dependencies.addAll(Arrays.asList(altitudeField, pressureField, altitudeOpt.selectedProperty(), pressureOpt.selectedProperty()));

        BooleanBinding disableCalculate = Bindings.createBooleanBinding(() -> {
            for (ObservableBooleanValue field : baseFields) {
                if (field.get()) {
                    return true;
                }
            }
            if (altitudeOpt.isSelected()) {
                return altitudeField.get();
            } else if (pressureOpt.isSelected()) {
                return pressureField.get();
            }
            return true;
        }, dependencies.toArray(new Observable[0]));

        calculate.disableProperty().bind(disableCalculate);

        calculate.setOnAction(
                (_) -> {
                    Parameters parameters = null;
                    BallisticCurve bc = null;

                    try {
                        Pressure pressureOut;
                        if (altitudeOpt.isSelected()) {
                            pressureOut = Physics.calculatePressure(new Length(((ObjectProperty<Double>) altitude[4]).getValue(), ((ChoiceBox<Length.ELength>) altitude[2]).getValue()));
                        } else if (pressureOpt.isSelected()) {
                            pressureOut = new Pressure(((ObjectProperty<Double>) pressure[4]).getValue(), ((ChoiceBox<Pressure.EPressure>) pressure[2]).getValue());
                        } else {
                            pressureOut = new Pressure(Constants.SEA_LEVEL_PRESSURE, Pressure.EPressure.PA);
                        }
                        List<Angle.EAngle> devAList = new ArrayList<>();
                        for (CheckBox cb : deviationABoxes) {
                            if (cb.isSelected()) {
                                for (Angle.EAngle a : Angle.EAngle.values()) {
                                    if (a.getName().equalsIgnoreCase(cb.getText())) {
                                        devAList.add(a);
                                        break;
                                    }
                                }
                            }
                        }
                        Angle.EAngle[] devA = new Angle.EAngle[devAList.size()];
                        for (int i = 0; i < devA.length; i++) {
                            devA[i] = devAList.get(i);
                        }

                        parameters = new Parameters(
                                new Length(((ObjectProperty<Double>) diameter[4]).getValue(), ((ChoiceBox<Length.ELength>) diameter[2]).getValue()),
                                new Mass(((ObjectProperty<Double>) mass[4]).getValue(), ((ChoiceBox<Mass.EMass>) mass[2]).getValue()),
                                new Speed(((ObjectProperty<Double>) velocity[4]).getValue(), ((ChoiceBox<Speed.ESpeed>) velocity[2]).getValue()),
                                new BallisticCoefficient(((ObjectProperty<Double>) balCoef[4]).getValue(), ((ChoiceBox<BallisticCoefficient.EBallisticCoefficient>) balCoef[2]).getValue()),
                                new Length(((ObjectProperty<Double>) zeroRange[4]).getValue(), ((ChoiceBox<Length.ELength>) zeroRange[2]).getValue()),
                                new Length(((ObjectProperty<Double>) sightHeight[4]).getValue(), ((ChoiceBox<Length.ELength>) sightHeight[2]).getValue()),
                                new Length(((ObjectProperty<Double>) twistRate[4]).getValue(), ((ChoiceBox<Length.ELength>) twistRate[2]).getValue()),
                                new Temperature(((ObjectProperty<Double>) temperature[4]).getValue(), ((ChoiceBox<Temperature.ETemperature>) temperature[2]).getValue()),
                                ((ObjectProperty<Double>) humidity[4]).getValue(),
                                new Speed(((ObjectProperty<Double>) windSpeed[4]).getValue(), ((ChoiceBox<Speed.ESpeed>) windSpeed[2]).getValue()),
                                new Angle(((ObjectProperty<Double>) windAzimuth[4]).getValue(), ((ChoiceBox<Angle.EAngle>) windAzimuth[2]).getValue()),
                                pressureOut,
                                new Angle(((ObjectProperty<Double>) shotAngle[4]).getValue(), ((ChoiceBox<Angle.EAngle>) shotAngle[2]).getValue()),
                                new Length(((ObjectProperty<Double>) maxRange[4]).getValue(), ((ChoiceBox<Length.ELength>) maxRange[2]).getValue()),
                                new Length(((ObjectProperty<Double>) rangeStep[4]).getValue(), ((ChoiceBox<Length.ELength>) rangeStep[2]).getValue()),
                                rangeUnitChoice.getValue(),
                                deviationLChoice.getValue(),
                                devA, othersTime.isSelected(), othersVelocity.isSelected()
                        );

                        bc = new BallisticCurve(Physics.positionIntegration(new Physics(parameters)));
                    } catch (Exception e) {
                        back.fire();
                    }

                    Stage current = (Stage) calculate.getScene().getWindow();
                    DisplayBallisticTable display = new DisplayBallisticTable(previous, bc, parameters);
                    current.setScene(display.getScene());
                }
        );
        //

        Scene scene = new Scene(root, previous.getWidth(), previous.getHeight());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/gui/" + UserSettings.colorPreset.getPrefix() + ".css")).toExternalForm());

        return scene;
    }

    private Object[] diameterInit() {
        Label diameterLabel = new Label(UserSettings.getStr("diameter.label"));

        TextField diameterText = new TextField();
        diameterText.setPrefSize(180, 40);

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
                    if (e.getName().equalsIgnoreCase(s)) return e;
                }
                return (Length.ELength) UserSettings.defaultUnits.get("diameter");
            }
        });

        Length min = new Length(1, Length.ELength.MM);
        Length max = new Length(30, Length.ELength.MM);
        diameterText.setPromptText(Math.ceil(min.get(diameterChoice.getValue(), 3)*100)/100.0 + " - " + Math.floor(max.get(diameterChoice.getValue(), 3)*100)/100.0);

        ObjectProperty<Double> diameterValue = new SimpleObjectProperty<>(null);

        diameterChoice.valueProperty().addListener(
                (_, oldValue, newValue) -> {
                    if (newValue != oldValue) {
                        diameterText.setPromptText(Math.ceil(min.get(diameterChoice.getValue(), 3)*100)/100.0 + " - " + Math.floor(max.get(diameterChoice.getValue(), 3)*100)/100.0);
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

        diameterText.textProperty().addListener((_, _, newValue) -> {
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
        result[1] = diameterText;
        result[2] = diameterChoice;
        result[3] = isDiameterInvalid;
        result[4] = diameterValue;

        return result;
    }

    private Object[] massInit() {
        Label massLabel = new Label(UserSettings.getStr("mass.label"));

        TextField massText = new TextField();
        massText.setPrefSize(180, 40);

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
                    if (e.getName().equalsIgnoreCase(s)) return e;
                }
                return (Mass.EMass) UserSettings.defaultUnits.get("mass");
            }
        });

        Mass min = new Mass(10, Mass.EMass.GR);
        Mass max = new Mass(1500, Mass.EMass.GR);
        massText.setPromptText(Math.ceil(min.get(massChoice.getValue(), 3)*100)/100.0 + " - " + Math.floor(max.get(massChoice.getValue(), 3)*100)/100.0);

        ObjectProperty<Double> massValue = new SimpleObjectProperty<>(null);

        massChoice.valueProperty().addListener(
                (_, oldValue, newValue) -> {
                    if (newValue != oldValue) {
                        massText.setPromptText(Math.ceil(min.get(massChoice.getValue(), 3)*100)/100.0 + " - " + Math.floor(max.get(massChoice.getValue(), 3)*100)/100.0);
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

        massText.textProperty().addListener((_, _, newValue) -> {
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
        result[1] = massText;
        result[2] = massChoice;
        result[3] = isMassInvalid;
        result[4] = massValue;

        return result;
    }

    private Object[] velocityInit() {
        Label velocityLabel = new Label(UserSettings.getStr("velocity.label"));

        TextField velocityText = new TextField();
        velocityText.setPrefSize(180, 40);

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
                    if (e.getName().equalsIgnoreCase(s)) return e;
                }
                return (Speed.ESpeed) UserSettings.defaultUnits.get("velocity");
            }
        });

        Speed min = new Speed(25, Speed.ESpeed.MPS);
        Speed max = new Speed(1600, Speed.ESpeed.MPS);
        velocityText.setPromptText(Math.ceil(min.get(velocityChoice.getValue(), 3)*100)/100.0 + " - " + Math.floor(max.get(velocityChoice.getValue(), 3)*100)/100.0);

        ObjectProperty<Double> velocityValue = new SimpleObjectProperty<>(null);

        velocityChoice.valueProperty().addListener(
                (_, oldValue, newValue) -> {
                    if (newValue != oldValue) {
                        velocityText.setPromptText(Math.ceil(min.get(velocityChoice.getValue(), 3)*100)/100.0 + " - " + Math.floor(max.get(velocityChoice.getValue(), 3)*100)/100.0);
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

        velocityText.textProperty().addListener((_, _, newValue) -> {
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
        result[1] = velocityText;
        result[2] = velocityChoice;
        result[3] = isVelocityInvalid;
        result[4] = velocityValue;

        return result;
    }

    private Object[] balCoefInit() {
        Label balCoefLabel = new Label(UserSettings.getStr("balCoef.label"));

        TextField balCoefText = new TextField();
        balCoefText.setPrefSize(180, 40);

        ChoiceBox<BallisticCoefficient.EBallisticCoefficient> balCoefChoice = new ChoiceBox<>();
        balCoefChoice.setPrefSize(100, 40);
        balCoefChoice.getItems().addAll((BallisticCoefficient.EBallisticCoefficient[]) Constants.ALLOWED_UNITS.get("balCoef"));
        balCoefChoice.setValue((BallisticCoefficient.EBallisticCoefficient) UserSettings.defaultUnits.get("balCoef"));
        balCoefChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(BallisticCoefficient.EBallisticCoefficient eBallisticCoefficient) {
                return eBallisticCoefficient.getName();
            }

            @Override
            public BallisticCoefficient.EBallisticCoefficient fromString(String s) {
                for (BallisticCoefficient.EBallisticCoefficient e: (BallisticCoefficient.EBallisticCoefficient[]) Constants.ALLOWED_UNITS.get("balCoef")) {
                    if (e.getName().equalsIgnoreCase(s)) return e;
                }
                return (BallisticCoefficient.EBallisticCoefficient) UserSettings.defaultUnits.get("balCoef");
            }
        });

        double min = 0.001;
        double max = 1;
        balCoefText.setPromptText(min + " - " + max);

        ObjectProperty<Double> balCoefValue = new SimpleObjectProperty<>(null);

        balCoefChoice.valueProperty().addListener(
                (_, oldValue, newValue) -> {
                    if (newValue != oldValue) {
                        balCoefText.setPromptText(min + " - " + max);
                    }
                }
        );

        balCoefText.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty() || newText.matches("\\d+[.,]?\\d*")) {
                return change;
            }
            return null;
        }));

        balCoefText.textProperty().addListener((_, _, newValue) -> {
            newValue = newValue.replace(',','.');
            try {
                balCoefValue.set(Double.parseDouble(newValue));
            } catch (NumberFormatException _) {
                balCoefValue.set(null);
            }
        });

        BooleanBinding isBalCoefInvalid = Bindings.createBooleanBinding(() -> {
            Double val = balCoefValue.get();
            if (val == null) return true;
            return val < min || val > max;
        }, balCoefChoice.valueProperty(), balCoefValue, balCoefText.getProperties());

        balCoefText.styleProperty().bind(Bindings.when(isBalCoefInvalid).then("-fx-border-color: red;").otherwise(""));

        Object[] result = new Object[5];
        result[0] = balCoefLabel;
        result[1] = balCoefText;
        result[2] = balCoefChoice;
        result[3] = isBalCoefInvalid;
        result[4] = balCoefValue;

        return result;
    }

    private Object[] zeroRangeInit() {
        Label zeroRangeLabel = new Label(UserSettings.getStr("zeroRange.label"));

        TextField zeroRangeText = new TextField();
        zeroRangeText.setPrefSize(180, 40);

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
                    if (e.getName().equalsIgnoreCase(s)) return e;
                }
                return (Length.ELength) UserSettings.defaultUnits.get("zeroRange");
            }
        });

        Length min = new Length(5, Length.ELength.M);
        Length max = new Length(500, Length.ELength.M);
        zeroRangeText.setPromptText(Math.ceil(min.get(zeroRangeChoice.getValue(), 3)*100)/100.0 + " - " + Math.floor(max.get(zeroRangeChoice.getValue(), 3)*100)/100.0);

        ObjectProperty<Double> zeroRangeValue = new SimpleObjectProperty<>(null);

        zeroRangeChoice.valueProperty().addListener(
                (_, oldValue, newValue) -> {
                    if (newValue != oldValue) {
                        zeroRangeText.setPromptText(Math.ceil(min.get(zeroRangeChoice.getValue(), 3)*100)/100.0 + " - " + Math.floor(max.get(zeroRangeChoice.getValue(), 3)*100)/100.0);
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

        zeroRangeText.textProperty().addListener((_, _, newValue) -> {
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
        result[1] = zeroRangeText;
        result[2] = zeroRangeChoice;
        result[3] = isZeroRangeInvalid;
        result[4] = zeroRangeValue;

        return result;
    }

    private Object[] sightHeightInit() {
        Label sightHeightLabel = new Label(UserSettings.getStr("sightHeight.label"));

        TextField sightHeightText = new TextField();
        sightHeightText.setPrefSize(180, 40);

        ChoiceBox<Length.ELength> sightHeightChoice = new ChoiceBox<>();
        sightHeightChoice.setPrefSize(100, 40);
        sightHeightChoice.getItems().addAll((Length.ELength[]) Constants.ALLOWED_UNITS.get("sightHeight"));
        sightHeightChoice.setValue((Length.ELength) UserSettings.defaultUnits.get("sightHeight"));
        sightHeightChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(Length.ELength eLength) {
                return eLength.getName();
            }

            @Override
            public Length.ELength fromString(String s) {
                for (Length.ELength e: (Length.ELength[]) Constants.ALLOWED_UNITS.get("sightHeight")) {
                    if (e.getName().equalsIgnoreCase(s)) return e;
                }
                return (Length.ELength) UserSettings.defaultUnits.get("sightHeight");
            }
        });

        Length min = new Length(0, Length.ELength.CM);
        Length max = new Length(10, Length.ELength.CM);
        sightHeightText.setPromptText(Math.ceil(min.get(sightHeightChoice.getValue(), 3)*100)/100.0 + " - " + Math.floor(max.get(sightHeightChoice.getValue(), 3)*100)/100.0);

        ObjectProperty<Double> sightHeightValue = new SimpleObjectProperty<>(null);

        sightHeightChoice.valueProperty().addListener(
                (_, oldValue, newValue) -> {
                    if (newValue != oldValue) {
                        sightHeightText.setPromptText(Math.ceil(min.get(sightHeightChoice.getValue(), 3)*100)/100.0 + " - " + Math.floor(max.get(sightHeightChoice.getValue(), 3)*100)/100.0);
                    }
                }
        );

        sightHeightText.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty() || newText.matches("\\d+[.,]?\\d*")) {
                return change;
            }
            return null;
        }));

        sightHeightText.textProperty().addListener((_, _, newValue) -> {
            newValue = newValue.replace(',','.');
            try {
                sightHeightValue.set(Double.parseDouble(newValue));
            } catch (NumberFormatException _) {
                sightHeightValue.set(null);
            }
        });

        BooleanBinding isSightHeightInvalid = Bindings.createBooleanBinding(() -> {
            Double val = sightHeightValue.get();
            if (val == null) return true;
            return val < min.get(sightHeightChoice.getValue()) || val > max.get(sightHeightChoice.getValue());
        }, sightHeightChoice.valueProperty(), sightHeightValue, sightHeightText.getProperties());

        sightHeightText.styleProperty().bind(Bindings.when(isSightHeightInvalid).then("-fx-border-color: red;").otherwise(""));

        Object[] result = new Object[5];
        result[0] = sightHeightLabel;
        result[1] = sightHeightText;
        result[2] = sightHeightChoice;
        result[3] = isSightHeightInvalid;
        result[4] = sightHeightValue;

        return result;
    }

    private Object[] twistRateInit() {
        Label twistRateLabel = new Label(UserSettings.getStr("twistRate.label"));

        TextField twistRateText = new TextField();
        twistRateText.setPrefSize(180, 40);

        ChoiceBox<Length.ELength> twistRateChoice = new ChoiceBox<>();
        twistRateChoice.setPrefSize(100, 40);
        twistRateChoice.getItems().addAll((Length.ELength[]) Constants.ALLOWED_UNITS.get("twistRate"));
        twistRateChoice.setValue((Length.ELength) UserSettings.defaultUnits.get("twistRate"));
        twistRateChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(Length.ELength eLength) {
                return eLength.getName();
            }

            @Override
            public Length.ELength fromString(String s) {
                for (Length.ELength e: (Length.ELength[]) Constants.ALLOWED_UNITS.get("twistRate")) {
                    if (e.getName().equalsIgnoreCase(s)) return e;
                }
                return (Length.ELength) UserSettings.defaultUnits.get("twistRate");
            }
        });

        Length min = new Length(-20, Length.ELength.IN);
        Length max = new Length(20, Length.ELength.IN);
        twistRateText.setPromptText(Math.ceil(min.get(twistRateChoice.getValue(), 3)*100)/100.0 + " - " + Math.floor(max.get(twistRateChoice.getValue(), 3)*100)/100.0);

        ObjectProperty<Double> twistRateValue = new SimpleObjectProperty<>(null);

        twistRateChoice.valueProperty().addListener(
                (_, oldValue, newValue) -> {
                    if (newValue != oldValue) {
                        twistRateText.setPromptText(Math.ceil(min.get(twistRateChoice.getValue(), 3)*100)/100.0 + " - " + Math.floor(max.get(twistRateChoice.getValue(), 3)*100)/100.0);
                    }
                }
        );

        twistRateText.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty() || newText.matches("-?\\d*[.,]?\\d*")) {
                return change;
            }
            return null;
        }));

        twistRateText.textProperty().addListener((_, _, newValue) -> {
            newValue = newValue.replace(',','.');
            try {
                twistRateValue.set(Double.parseDouble(newValue));
            } catch (NumberFormatException _) {
                twistRateValue.set(null);
            }
        });

        BooleanBinding isTwistRateInvalid = Bindings.createBooleanBinding(() -> {
            Double val = twistRateValue.get();
            if (val == null) return true;
            return val < min.get(twistRateChoice.getValue()) || val > max.get(twistRateChoice.getValue());
        }, twistRateChoice.valueProperty(), twistRateValue, twistRateText.getProperties());

        twistRateText.styleProperty().bind(Bindings.when(isTwistRateInvalid).then("-fx-border-color: red;").otherwise(""));

        Object[] result = new Object[5];
        result[0] = twistRateLabel;
        result[1] = twistRateText;
        result[2] = twistRateChoice;
        result[3] = isTwistRateInvalid;
        result[4] = twistRateValue;

        return result;
    }

    private Object[] temperatureInit() {
        Label temperatureLabel = new Label(UserSettings.getStr("temperature.label"));

        TextField temperatureText = new TextField();
        temperatureText.setPrefSize(180, 40);

        ChoiceBox<Temperature.ETemperature> temperatureChoice = new ChoiceBox<>();
        temperatureChoice.setPrefSize(100, 40);
        temperatureChoice.getItems().addAll((Temperature.ETemperature[]) Constants.ALLOWED_UNITS.get("temperature"));
        temperatureChoice.setValue((Temperature.ETemperature) UserSettings.defaultUnits.get("temperature"));
        temperatureChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(Temperature.ETemperature eTemperature) {
                return eTemperature.getName();
            }

            @Override
            public Temperature.ETemperature fromString(String s) {
                for (Temperature.ETemperature e: (Temperature.ETemperature[]) Constants.ALLOWED_UNITS.get("temperature")) {
                    if (e.getName().equalsIgnoreCase(s)) return e;
                }
                return (Temperature.ETemperature) UserSettings.defaultUnits.get("temperature");
            }
        });

        Temperature min = new Temperature(-80, Temperature.ETemperature.C);
        Temperature max = new Temperature(100, Temperature.ETemperature.C);
        temperatureText.setPromptText(Math.ceil(min.get(temperatureChoice.getValue(), 3)*100)/100.0 + " - " + Math.floor(max.get(temperatureChoice.getValue(), 3)*100)/100.0);

        ObjectProperty<Double> temperatureValue = new SimpleObjectProperty<>(null);

        temperatureChoice.valueProperty().addListener(
                (_, oldValue, newValue) -> {
                    if (newValue != oldValue) {
                        temperatureText.setPromptText(Math.ceil(min.get(temperatureChoice.getValue(), 3)*100)/100.0 + " - " + Math.floor(max.get(temperatureChoice.getValue(), 3)*100)/100.0);
                    }
                }
        );

        temperatureText.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty() || newText.matches("-?\\d*[.,]?\\d*")) {
                return change;
            }
            return null;
        }));

        temperatureText.textProperty().addListener((_, _, newValue) -> {
            newValue = newValue.replace(',','.');
            try {
                temperatureValue.set(Double.parseDouble(newValue));
            } catch (NumberFormatException _) {
                temperatureValue.set(null);
            }
        });

        BooleanBinding isTemperatureInvalid = Bindings.createBooleanBinding(() -> {
            Double val = temperatureValue.get();
            if (val == null) return true;
            return val < min.get(temperatureChoice.getValue()) || val > max.get(temperatureChoice.getValue());
        }, temperatureChoice.valueProperty(), temperatureValue, temperatureText.getProperties());

        temperatureText.styleProperty().bind(Bindings.when(isTemperatureInvalid).then("-fx-border-color: red;").otherwise(""));

        Object[] result = new Object[5];
        result[0] = temperatureLabel;
        result[1] = temperatureText;
        result[2] = temperatureChoice;
        result[3] = isTemperatureInvalid;
        result[4] = temperatureValue;

        return result;
    }

    private Object[] humidityInit() {
        Label humidityLabel = new Label(UserSettings.getStr("humidity.label"));

        TextField humidityText = new TextField();
        humidityText.setPrefSize(180, 40);

        Label percent = new Label("%");

        double min = 0;
        double max = 100;
        humidityText.setPromptText(min + " - " + max);

        ObjectProperty<Double> humidityValue = new SimpleObjectProperty<>(null);

        humidityText.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty() || newText.matches("\\d+[.,]?\\d*")) {
                return change;
            }
            return null;
        }));

        humidityText.textProperty().addListener((_, _, newValue) -> {
            newValue = newValue.replace(',','.');
            try {
                humidityValue.set(Double.parseDouble(newValue));
            } catch (NumberFormatException _) {
                humidityValue.set(null);
            }
        });

        BooleanBinding isHumidityInvalid = Bindings.createBooleanBinding(() -> {
            Double val = humidityValue.get();
            if (val == null) return true;
            return val < min || val > max;
        }, humidityValue, humidityText.getProperties());

        humidityText.styleProperty().bind(Bindings.when(isHumidityInvalid).then("-fx-border-color: red;").otherwise(""));

        Object[] result = new Object[5];
        result[0] = humidityLabel;
        result[1] = humidityText;
        result[2] = percent;
        result[3] = isHumidityInvalid;
        result[4] = humidityValue;

        return result;
    }

    private Object[] windSpeedInit() {
        Label windSpeedLabel = new Label(UserSettings.getStr("windSpeed.label"));

        TextField windSpeedText = new TextField();
        windSpeedText.setPrefSize(180, 40);

        ChoiceBox<Speed.ESpeed> windSpeedChoice = new ChoiceBox<>();
        windSpeedChoice.setPrefSize(100, 40);
        windSpeedChoice.getItems().addAll((Speed.ESpeed[]) Constants.ALLOWED_UNITS.get("windSpeed"));
        windSpeedChoice.setValue((Speed.ESpeed) UserSettings.defaultUnits.get("windSpeed"));
        windSpeedChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(Speed.ESpeed eSpeed) {
                return eSpeed.getName();
            }

            @Override
            public Speed.ESpeed fromString(String s) {
                for (Speed.ESpeed e: (Speed.ESpeed[]) Constants.ALLOWED_UNITS.get("windSpeed")) {
                    if (e.getName().equalsIgnoreCase(s)) return e;
                }
                return (Speed.ESpeed) UserSettings.defaultUnits.get("windSpeed");
            }
        });

        Speed min = new Speed(0, Speed.ESpeed.KMPH);
        Speed max = new Speed(150, Speed.ESpeed.KMPH);
        windSpeedText.setPromptText(Math.ceil(min.get(windSpeedChoice.getValue(), 3)*100)/100.0 + " - " + Math.floor(max.get(windSpeedChoice.getValue(), 3)*100)/100.0);

        ObjectProperty<Double> windSpeedValue = new SimpleObjectProperty<>(null);

        windSpeedChoice.valueProperty().addListener(
                (_, oldValue, newValue) -> {
                    if (newValue != oldValue) {
                        windSpeedText.setPromptText(Math.ceil(min.get(windSpeedChoice.getValue(), 3)*100)/100.0 + " - " + Math.floor(max.get(windSpeedChoice.getValue(), 3)*100)/100.0);
                    }
                }
        );

        windSpeedText.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty() || newText.matches("\\d+[.,]?\\d*")) {
                return change;
            }
            return null;
        }));

        windSpeedText.textProperty().addListener((_, _, newValue) -> {
            newValue = newValue.replace(',','.');
            try {
                windSpeedValue.set(Double.parseDouble(newValue));
            } catch (NumberFormatException _) {
                windSpeedValue.set(null);
            }
        });

        BooleanBinding isWindSpeedInvalid = Bindings.createBooleanBinding(() -> {
            Double val = windSpeedValue.get();
            if (val == null) return true;
            return val < min.get(windSpeedChoice.getValue()) || val > max.get(windSpeedChoice.getValue());
        }, windSpeedChoice.valueProperty(), windSpeedValue, windSpeedText.getProperties());

        windSpeedText.styleProperty().bind(Bindings.when(isWindSpeedInvalid).then("-fx-border-color: red;").otherwise(""));

        Object[] result = new Object[5];
        result[0] = windSpeedLabel;
        result[1] = windSpeedText;
        result[2] = windSpeedChoice;
        result[3] = isWindSpeedInvalid;
        result[4] = windSpeedValue;

        return result;
    }

    private Object[] windAzimuthInit() {
        Label windAzimuthLabel = new Label(UserSettings.getStr("windAzimuth.label"));

        TextField windAzimuthText = new TextField();
        windAzimuthText.setPrefSize(180, 40);

        ChoiceBox<Angle.EAngle> windAzimuthChoice = new ChoiceBox<>();
        windAzimuthChoice.setPrefSize(100, 40);
        windAzimuthChoice.getItems().addAll((Angle.EAngle[]) Constants.ALLOWED_UNITS.get("windAzimuth"));
        windAzimuthChoice.setValue((Angle.EAngle) UserSettings.defaultUnits.get("windAzimuth"));
        windAzimuthChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(Angle.EAngle eAngle) {
                return eAngle.getName();
            }

            @Override
            public Angle.EAngle fromString(String s) {
                for (Angle.EAngle e: (Angle.EAngle[]) Constants.ALLOWED_UNITS.get("windAzimuth")) {
                    if (e.getName().equalsIgnoreCase(s)) return e;
                }
                return (Angle.EAngle) UserSettings.defaultUnits.get("windAzimuth");
            }
        });

        Angle min = new Angle(-180, Angle.EAngle.DEG);
        Angle max = new Angle(180, Angle.EAngle.DEG);
        windAzimuthText.setPromptText(Math.ceil(min.get(windAzimuthChoice.getValue(), 3)*100)/100.0 + " - " + Math.floor(max.get(windAzimuthChoice.getValue(), 3)*100)/100.0);

        ObjectProperty<Double> windAzimuthValue = new SimpleObjectProperty<>(null);

        windAzimuthChoice.valueProperty().addListener(
                (_, oldValue, newValue) -> {
                    if (newValue != oldValue) {
                        windAzimuthText.setPromptText(Math.ceil(min.get(windAzimuthChoice.getValue(), 3)*100)/100.0 + " - " + Math.floor(max.get(windAzimuthChoice.getValue(), 3)*100)/100.0);
                    }
                }
        );

        windAzimuthText.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty() || newText.matches("-?\\d*[.,]?\\d*")) {
                return change;
            }
            return null;
        }));

        windAzimuthText.textProperty().addListener((_, _, newValue) -> {
            newValue = newValue.replace(',','.');
            try {
                windAzimuthValue.set(Double.parseDouble(newValue));
            } catch (NumberFormatException _) {
                windAzimuthValue.set(null);
            }
        });

        BooleanBinding isWindAzimuthInvalid = Bindings.createBooleanBinding(() -> {
            Double val = windAzimuthValue.get();
            if (val == null) return true;
            return val < min.get(windAzimuthChoice.getValue()) || val > max.get(windAzimuthChoice.getValue());
        }, windAzimuthChoice.valueProperty(), windAzimuthValue, windAzimuthText.getProperties());

        windAzimuthText.styleProperty().bind(Bindings.when(isWindAzimuthInvalid).then("-fx-border-color: red;").otherwise(""));

        Label azimuth = new Label(UserSettings.getStr("windAzimuth.desc"));
        azimuth.setStyle("-fx-font-size: 10px");

        Object[] result = new Object[6];
        result[0] = windAzimuthLabel;
        result[1] = windAzimuthText;
        result[2] = windAzimuthChoice;
        result[3] = isWindAzimuthInvalid;
        result[4] = windAzimuthValue;
        result[5] = azimuth;

        return result;
    }

    private Object[] altitudeInit() {
        Label altitudeLabel = new Label(UserSettings.getStr("altitude.label"));

        TextField altitudeText = new TextField();
        altitudeText.setPrefSize(180, 40);

        ChoiceBox<Length.ELength> altitudeChoice = new ChoiceBox<>();
        altitudeChoice.setPrefSize(100, 40);
        altitudeChoice.getItems().addAll((Length.ELength[]) Constants.ALLOWED_UNITS.get("altitude"));
        altitudeChoice.setValue((Length.ELength) UserSettings.defaultUnits.get("altitude"));
        altitudeChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(Length.ELength eLength) {
                return eLength.getName();
            }

            @Override
            public Length.ELength fromString(String s) {
                for (Length.ELength e: (Length.ELength[]) Constants.ALLOWED_UNITS.get("altitude")) {
                    if (e.getName().equalsIgnoreCase(s)) return e;
                }
                return (Length.ELength) UserSettings.defaultUnits.get("altitude");
            }
        });

        Length min = new Length(0, Length.ELength.M);
        Length max = new Length(8000, Length.ELength.M);
        altitudeText.setPromptText(Math.ceil(min.get(altitudeChoice.getValue(), 3)*100)/100.0 + " - " + Math.floor(max.get(altitudeChoice.getValue(), 3)*100)/100.0);

        ObjectProperty<Double> altitudeValue = new SimpleObjectProperty<>(null);

        altitudeChoice.valueProperty().addListener(
                (_, oldValue, newValue) -> {
                    if (newValue != oldValue) {
                        altitudeText.setPromptText(Math.ceil(min.get(altitudeChoice.getValue(), 3)*100)/100.0 + " - " + Math.floor(max.get(altitudeChoice.getValue(), 3)*100)/100.0);
                    }
                }
        );

        altitudeText.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty() || newText.matches("\\d+[.,]?\\d*")) {
                return change;
            }
            return null;
        }));

        altitudeText.textProperty().addListener((_, _, newValue) -> {
            newValue = newValue.replace(',','.');
            try {
                altitudeValue.set(Double.parseDouble(newValue));
            } catch (NumberFormatException _) {
                altitudeValue.set(null);
            }
        });

        BooleanBinding isAltitudeInvalid = Bindings.createBooleanBinding(() -> {
            Double val = altitudeValue.get();
            if (val == null) return true;
            return val < min.get(altitudeChoice.getValue()) || val > max.get(altitudeChoice.getValue());
        }, altitudeChoice.valueProperty(), altitudeValue, altitudeText.getProperties());

        altitudeText.styleProperty().bind(Bindings.when(isAltitudeInvalid).then("-fx-border-color: red;").otherwise(""));

        Object[] result = new Object[5];
        result[0] = altitudeLabel;
        result[1] = altitudeText;
        result[2] = altitudeChoice;
        result[3] = isAltitudeInvalid;
        result[4] = altitudeValue;

        return result;
    }

    private Object[] pressureInit() {
        Label pressureLabel = new Label(UserSettings.getStr("pressure.label"));

        TextField pressureText = new TextField();
        pressureText.setPrefSize(180, 40);

        ChoiceBox<Pressure.EPressure> pressureChoice = new ChoiceBox<>();
        pressureChoice.setPrefSize(100, 40);
        pressureChoice.getItems().addAll((Pressure.EPressure[]) Constants.ALLOWED_UNITS.get("pressure"));
        pressureChoice.setValue((Pressure.EPressure) UserSettings.defaultUnits.get("pressure"));
        pressureChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(Pressure.EPressure ePressure) {
                return ePressure.getName();
            }

            @Override
            public Pressure.EPressure fromString(String s) {
                for (Pressure.EPressure e: (Pressure.EPressure[]) Constants.ALLOWED_UNITS.get("pressure")) {
                    if (e.getName().equalsIgnoreCase(s)) return e;
                }
                return (Pressure.EPressure) UserSettings.defaultUnits.get("pressure");
            }
        });

        Pressure min = new Pressure(300, Pressure.EPressure.HPA);
        Pressure max = new Pressure(1300, Pressure.EPressure.HPA);
        pressureText.setPromptText(Math.ceil(min.get(pressureChoice.getValue(), 3)*100)/100.0 + " - " + Math.floor(max.get(pressureChoice.getValue(), 3)*100)/100.0);

        ObjectProperty<Double> pressureValue = new SimpleObjectProperty<>(null);

        pressureChoice.valueProperty().addListener(
                (_, oldValue, newValue) -> {
                    if (newValue != oldValue) {
                        pressureText.setPromptText(Math.ceil(min.get(pressureChoice.getValue(), 3)*100)/100.0 + " - " + Math.floor(max.get(pressureChoice.getValue(), 3)*100)/100.0);
                    }
                }
        );

        pressureText.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty() || newText.matches("\\d+[.,]?\\d*")) {
                return change;
            }
            return null;
        }));

        pressureText.textProperty().addListener((_, _, newValue) -> {
            newValue = newValue.replace(',','.');
            try {
                pressureValue.set(Double.parseDouble(newValue));
            } catch (NumberFormatException _) {
                pressureValue.set(null);
            }
        });

        BooleanBinding isPressureInvalid = Bindings.createBooleanBinding(() -> {
            Double val = pressureValue.get();
            if (val == null) return true;
            return val < min.get(pressureChoice.getValue()) || val > max.get(pressureChoice.getValue());
        }, pressureChoice.valueProperty(), pressureValue, pressureText.getProperties());

        pressureText.styleProperty().bind(Bindings.when(isPressureInvalid).then("-fx-border-color: red;").otherwise(""));

        Object[] result = new Object[5];
        result[0] = pressureLabel;
        result[1] = pressureText;
        result[2] = pressureChoice;
        result[3] = isPressureInvalid;
        result[4] = pressureValue;

        return result;
    }

    private Object[] shotAngleInit() {
        Label shotAngleLabel = new Label(UserSettings.getStr("shotAngle.label"));

        TextField shotAngleText = new TextField();
        shotAngleText.setPrefSize(180, 40);

        ChoiceBox<Angle.EAngle> shotAngleChoice = new ChoiceBox<>();
        shotAngleChoice.setPrefSize(100, 40);
        shotAngleChoice.getItems().addAll((Angle.EAngle[]) Constants.ALLOWED_UNITS.get("shotAngle"));
        shotAngleChoice.setValue((Angle.EAngle) UserSettings.defaultUnits.get("shotAngle"));
        shotAngleChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(Angle.EAngle eAngle) {
                return eAngle.getName();
            }

            @Override
            public Angle.EAngle fromString(String s) {
                for (Angle.EAngle e: (Angle.EAngle[]) Constants.ALLOWED_UNITS.get("shotAngle")) {
                    if (e.getName().equalsIgnoreCase(s)) return e;
                }
                return (Angle.EAngle) UserSettings.defaultUnits.get("shotAngle");
            }
        });

        Angle min = new Angle(-80, Angle.EAngle.DEG);
        Angle max = new Angle(80, Angle.EAngle.DEG);
        shotAngleText.setPromptText(Math.ceil(min.get(shotAngleChoice.getValue(), 3)*100)/100.0 + " - " + Math.floor(max.get(shotAngleChoice.getValue(), 3)*100)/100.0);

        ObjectProperty<Double> shotAngleValue = new SimpleObjectProperty<>(null);

        shotAngleChoice.valueProperty().addListener(
                (_, oldValue, newValue) -> {
                    if (newValue != oldValue) {
                        shotAngleText.setPromptText(Math.ceil(min.get(shotAngleChoice.getValue(), 3)*100)/100.0 + " - " + Math.floor(max.get(shotAngleChoice.getValue(), 3)*100)/100.0);
                    }
                }
        );

        shotAngleText.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty() || newText.matches("-?\\d*[.,]?\\d*")) {
                return change;
            }
            return null;
        }));

        shotAngleText.textProperty().addListener((_, _, newValue) -> {
            newValue = newValue.replace(',','.');
            try {
                shotAngleValue.set(Double.parseDouble(newValue));
            } catch (NumberFormatException _) {
                shotAngleValue.set(null);
            }
        });

        BooleanBinding isShotAngleInvalid = Bindings.createBooleanBinding(() -> {
            Double val = shotAngleValue.get();
            if (val == null) return true;
            return val < min.get(shotAngleChoice.getValue()) || val > max.get(shotAngleChoice.getValue());
        }, shotAngleChoice.valueProperty(), shotAngleValue, shotAngleText.getProperties());

        shotAngleText.styleProperty().bind(Bindings.when(isShotAngleInvalid).then("-fx-border-color: red;").otherwise(""));

        Label azimuth = new Label(UserSettings.getStr("shotAngle.desc"));
        azimuth.setStyle("-fx-font-size: 10px");

        Object[] result = new Object[6];
        result[0] = shotAngleLabel;
        result[1] = shotAngleText;
        result[2] = shotAngleChoice;
        result[3] = isShotAngleInvalid;
        result[4] = shotAngleValue;
        result[5] = azimuth;

        return result;
    }

    private Object[] maxRangeInit() {
        Label maxRangeLabel = new Label(UserSettings.getStr("maxRange.label"));

        TextField maxRangeText = new TextField();
        maxRangeText.setPrefSize(180, 40);

        ChoiceBox<Length.ELength> maxRangeChoice = new ChoiceBox<>();
        maxRangeChoice.setPrefSize(100, 40);
        maxRangeChoice.getItems().addAll((Length.ELength[]) Constants.ALLOWED_UNITS.get("maxRange"));
        maxRangeChoice.setValue((Length.ELength) UserSettings.defaultUnits.get("maxRange"));
        maxRangeChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(Length.ELength eLength) {
                return eLength.getName();
            }

            @Override
            public Length.ELength fromString(String s) {
                for (Length.ELength e: (Length.ELength[]) Constants.ALLOWED_UNITS.get("maxRange")) {
                    if (e.getName().equalsIgnoreCase(s)) return e;
                }
                return (Length.ELength) UserSettings.defaultUnits.get("maxRange");
            }
        });

        Length min = new Length(10, Length.ELength.M);
        Length max = new Length(3000, Length.ELength.M);
        maxRangeText.setPromptText(Math.ceil(min.get(maxRangeChoice.getValue(), 3)*100)/100.0 + " - " + Math.floor(max.get(maxRangeChoice.getValue(), 3)*100)/100.0);

        ObjectProperty<Double> maxRangeValue = new SimpleObjectProperty<>(null);

        maxRangeChoice.valueProperty().addListener(
                (_, oldValue, newValue) -> {
                    if (newValue != oldValue) {
                        maxRangeText.setPromptText(Math.ceil(min.get(maxRangeChoice.getValue(), 3)*100)/100.0 + " - " + Math.floor(max.get(maxRangeChoice.getValue(), 3)*100)/100.0);
                    }
                }
        );

        maxRangeText.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty() || newText.matches("\\d+[.,]?\\d*")) {
                return change;
            }
            return null;
        }));

        maxRangeText.textProperty().addListener((_, _, newValue) -> {
            newValue = newValue.replace(',','.');
            try {
                maxRangeValue.set(Double.parseDouble(newValue));
            } catch (NumberFormatException _) {
                maxRangeValue.set(null);
            }
        });

        BooleanBinding isMaxRangeInvalid = Bindings.createBooleanBinding(() -> {
            Double val = maxRangeValue.get();
            if (val == null) return true;
            return val < min.get(maxRangeChoice.getValue()) || val > max.get(maxRangeChoice.getValue());
        }, maxRangeChoice.valueProperty(), maxRangeValue, maxRangeText.getProperties());

        maxRangeText.styleProperty().bind(Bindings.when(isMaxRangeInvalid).then("-fx-border-color: red;").otherwise(""));

        Object[] result = new Object[5];
        result[0] = maxRangeLabel;
        result[1] = maxRangeText;
        result[2] = maxRangeChoice;
        result[3] = isMaxRangeInvalid;
        result[4] = maxRangeValue;

        return result;
    }

    private Object[] rangeStepInit() {
        Label rangeStepLabel = new Label(UserSettings.getStr("rangeStep.label"));

        TextField rangeStepText = new TextField();
        rangeStepText.setPrefSize(180, 40);

        ChoiceBox<Length.ELength> rangeStepChoice = new ChoiceBox<>();
        rangeStepChoice.setPrefSize(100, 40);
        rangeStepChoice.getItems().addAll((Length.ELength[]) Constants.ALLOWED_UNITS.get("rangeStep"));
        rangeStepChoice.setValue((Length.ELength) UserSettings.defaultUnits.get("rangeStep"));
        rangeStepChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(Length.ELength eLength) {
                return eLength.getName();
            }

            @Override
            public Length.ELength fromString(String s) {
                for (Length.ELength e: (Length.ELength[]) Constants.ALLOWED_UNITS.get("rangeStep")) {
                    if (e.getName().equalsIgnoreCase(s)) return e;
                }
                return (Length.ELength) UserSettings.defaultUnits.get("rangeStep");
            }
        });

        Length min = new Length(5, Length.ELength.M);
        Length max = new Length(500, Length.ELength.M);
        rangeStepText.setPromptText(Math.ceil(min.get(rangeStepChoice.getValue(), 3)*100)/100.0 + " - " + Math.floor(max.get(rangeStepChoice.getValue(), 3)*100)/100.0);

        ObjectProperty<Double> rangeStepValue = new SimpleObjectProperty<>(null);

        rangeStepChoice.valueProperty().addListener(
                (_, oldValue, newValue) -> {
                    if (newValue != oldValue) {
                        rangeStepText.setPromptText(Math.ceil(min.get(rangeStepChoice.getValue(), 3)*100)/100.0 + " - " + Math.floor(max.get(rangeStepChoice.getValue(), 3)*100)/100.0);
                    }
                }
        );

        rangeStepText.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty() || newText.matches("\\d+[.,]?\\d*")) {
                return change;
            }
            return null;
        }));

        rangeStepText.textProperty().addListener((_, _, newValue) -> {
            newValue = newValue.replace(',','.');
            try {
                rangeStepValue.set(Double.parseDouble(newValue));
            } catch (NumberFormatException _) {
                rangeStepValue.set(null);
            }
        });

        BooleanBinding isRangeStepInvalid = Bindings.createBooleanBinding(() -> {
            Double val = rangeStepValue.get();
            if (val == null) return true;
            return val < min.get(rangeStepChoice.getValue()) || val > max.get(rangeStepChoice.getValue());
        }, rangeStepChoice.valueProperty(), rangeStepValue, rangeStepText.getProperties());

        rangeStepText.styleProperty().bind(Bindings.when(isRangeStepInvalid).then("-fx-border-color: red;").otherwise(""));

        Object[] result = new Object[5];
        result[0] = rangeStepLabel;
        result[1] = rangeStepText;
        result[2] = rangeStepChoice;
        result[3] = isRangeStepInvalid;
        result[4] = rangeStepValue;

        return result;
    }
}

package dev.dbrada.ballistic_calculator.gui;

import dev.dbrada.ballistic_calculator.*;
import dev.dbrada.ballistic_calculator.units.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public class SimpleCalculationForm {
    private final Scene previous;

    /**
     * Constructs the final form
     *
     * @return a {@code Scene} to be displayed
     */
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
        scrollParams.setPrefSize(previous.getWidth() * 0.95, previous.getHeight() - 50);
        root.getChildren().add(scrollParams);
        //
        //input label
        Label inputs = new Label(UserSettings.getStr("inputs.label"));
        inputs.setStyle("-fx-font-size: 20px");
        params.add(inputs, 0, 0);
        //
        //projectile
        //diameter
        Object[] diameter = FormElements.diameterInit();
        params.add((Label) diameter[0], 0, 1);
        params.add((TextField) diameter[1], 1, 1);
        params.add((ChoiceBox<Length.ELength>) diameter[2], 2, 1);
        //
        //mass
        Object[] mass = FormElements.massInit();
        params.add((Label) mass[0], 0, 2);
        params.add((TextField) mass[1], 1, 2);
        params.add((ChoiceBox<Mass.EMass>) mass[2], 2, 2);
        //
        //velocity
        Object[] velocity = FormElements.velocityInit();
        params.add((Label) velocity[0], 0, 3);
        params.add((TextField) velocity[1], 1, 3);
        params.add((ChoiceBox<Speed.ESpeed>) velocity[2], 2, 3);
        //
        //ballistic coefficient
        Object[] balCoef = FormElements.balCoefInit();
        params.add((Label) balCoef[0], 0, 4);
        params.add((TextField) balCoef[1], 1, 4);
        params.add((ChoiceBox<BallisticCoefficient.EBallisticCoefficient>) balCoef[2], 2, 4);
        //
        //
        //rifle setup
        //zero range
        Object[] zeroRange = FormElements.zeroRangeInit();
        params.add((Label) zeroRange[0], 0, 5);
        params.add((TextField) zeroRange[1], 1, 5);
        params.add((ChoiceBox<Length.ELength>) zeroRange[2], 2, 5);
        //
        //sight height
        Object[] sightHeight = FormElements.sightHeightInit();
        params.add((Label) sightHeight[0], 0, 6);
        params.add((TextField) sightHeight[1], 1, 6);
        params.add((ChoiceBox<Length.ELength>) sightHeight[2], 2, 6);
        //
        //temperature
        Object[] temperature = FormElements.temperatureInit();
        params.add((Label) temperature[0], 0, 7);
        params.add((TextField) temperature[1], 1, 7);
        params.add((ChoiceBox<Length.ELength>) temperature[2], 2, 7);
        //
        //wind speed
        Object[] windSpeed = FormElements.windSpeedInit();
        params.add((Label) windSpeed[0], 0, 8);
        params.add((TextField) windSpeed[1], 1, 8);
        params.add((ChoiceBox<Speed.ESpeed>) windSpeed[2], 2, 8);
        //
        //altitude
        Object[] altitude = FormElements.altitudeInit();
        params.add((Label) altitude[0], 0, 9);
        params.add((TextField) altitude[1], 1, 9);
        params.add((ChoiceBox<Length.ELength>) altitude[2], 2, 9);
        //
        //max range
        Object[] maxRange = FormElements.maxRangeInit();
        params.add((Label) maxRange[0], 0, 10);
        params.add((TextField) maxRange[1], 1, 10);
        params.add((ChoiceBox<Length.ELength>) maxRange[2], 2, 10);
        //
        //range step
        Object[] rangeStep = FormElements.rangeStepInit();
        params.add((Label) rangeStep[0], 0, 12);
        params.add((TextField) rangeStep[1], 1, 12);
        params.add((ChoiceBox<Length.ELength>) rangeStep[2], 2, 12);
        //
        //
        //output label
        Label outputs = new Label(UserSettings.getStr("outputs.label"));
        outputs.setStyle("-fx-font-size: 20px");
        params.add(outputs, 0, 13);
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
                for (Length.ELength e : (Length.ELength[]) Constants.ALLOWED_UNITS.get("range")) {
                    if (e.getName().equalsIgnoreCase(s)) return e;
                }
                return (Length.ELength) UserSettings.defaultUnits.get("range");
            }
        });
        params.add(rangeUnitLabel, 0, 14);
        params.add(rangeUnitChoice, 1, 14);
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
                for (Length.ELength e : (Length.ELength[]) Constants.ALLOWED_UNITS.get("deviationL")) {
                    if (e.getName().equalsIgnoreCase(s)) return e;
                }
                return (Length.ELength) UserSettings.defaultUnits.get("deviationL");
            }
        });
        params.add(deviationLLabel, 0, 15);
        params.add(deviationLChoice, 1, 15);
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
            params.add(deviationABoxes[i], i + 1, 20);
        }
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
                (ObservableBooleanValue) temperature[3],
                (ObservableBooleanValue) windSpeed[3],
                (ObservableBooleanValue) maxRange[3],
                (ObservableBooleanValue) rangeStep[3],
                (ObservableBooleanValue) altitude[3]
        };

        BooleanBinding disableCalculate = Bindings.createBooleanBinding(() -> {
            for (ObservableBooleanValue field : baseFields) {
                if (field.get()) {
                    return true;
                }
            }
            return false;
        }, baseFields);

        calculate.disableProperty().bind(disableCalculate);

        calculate.setOnAction(
                (_) -> {
                    Parameters parameters = null;
                    BallisticCurve bc = null;

                    try {
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
                                new Length(0, Length.ELength.IN),
                                new Temperature(((ObjectProperty<Double>) temperature[4]).getValue(), ((ChoiceBox<Temperature.ETemperature>) temperature[2]).getValue()),
                                0,
                                new Speed(((ObjectProperty<Double>) windSpeed[4]).getValue(), ((ChoiceBox<Speed.ESpeed>) windSpeed[2]).getValue()),
                                new Angle(-90, Angle.EAngle.DEG),
                                Physics.calculatePressure(new Length(((ObjectProperty<Double>) altitude[4]).getValue(), ((ChoiceBox<Length.ELength>) altitude[2]).getValue())),
                                new Angle(0, Angle.EAngle.DEG),
                                new Length(((ObjectProperty<Double>) maxRange[4]).getValue(), ((ChoiceBox<Length.ELength>) maxRange[2]).getValue()),
                                new Length(((ObjectProperty<Double>) rangeStep[4]).getValue(), ((ChoiceBox<Length.ELength>) rangeStep[2]).getValue()),
                                rangeUnitChoice.getValue(),
                                deviationLChoice.getValue(),
                                devA, false, false
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
}

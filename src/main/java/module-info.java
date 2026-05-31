module ballistic.calculator {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires static lombok;
    requires com.google.gson;

    exports dev.dbrada.ballistic_calculator.gui;
    exports dev.dbrada.ballistic_calculator;
    opens dev.dbrada.ballistic_calculator.units to com.google.gson;
}
package dev.dbrada.ballistic_calculator;

import dev.dbrada.ballistic_calculator.gui.Menu;
import javafx.application.Application;

public class Main {
    static void main() {
        UserSettings.init();
        Application.launch(Menu.class);
    }
}

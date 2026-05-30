package dev.dbrada.ballistic_calculator;

import dev.dbrada.ballistic_calculator.gui.Menu;
import javafx.application.Application;

/**
 * Handles start of the app
 */
public class Main {
    /**
     * Starts the app
     */
    static void main() {
        UserSettings.init();
        Application.launch(Menu.class);
    }
}

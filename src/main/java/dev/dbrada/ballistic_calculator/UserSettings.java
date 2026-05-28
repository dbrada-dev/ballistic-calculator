package dev.dbrada.ballistic_calculator;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Properties;

public abstract class UserSettings {
    public static Colors colorPreset = Colors.DARK;

    public static void init() {
        File file = new File(Path.of(getConfigPath().toString(), "config").toString());
        if (!file.exists()) return;

        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(file)) {
            props.load(in);
            colorPreset = Colors.valueOf(props.getProperty("colorPreset").toUpperCase());
        } catch (IOException | NumberFormatException e) {
            System.err.println("No load done\n" + e.getMessage());
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void save() {
        Properties props = new Properties();
        props.setProperty("colorPreset", colorPreset.prefix);

        Path path = getConfigPath();
        new File(path.toUri()).mkdir();

        try(FileOutputStream out = new FileOutputStream(Path.of(path.toString(), "config").toString())) {
            props.store(out, "config");
        } catch (IOException e) {
            System.err.println("No save done\n" + e.getMessage());
        }
    }

    private static Path getConfigPath() {
        String os = System.getProperty("os.name").toLowerCase();
        String app = "ballistic-calculator/";
        String home = System.getProperty("user.home");
        return switch (os) {
            case String s when s.contains("win")  -> Path.of(home, "AppData", "Roaming", app);
            case String s when s.contains("mac")  -> Path.of(home, "Library", "Application Support", app);
            case String s when s.contains("nix") || s.contains("nux") || s.contains("aix") -> Path.of(home, ".config", app);
            default -> {
                try {
                    yield Path.of(new File(UserSettings.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent(), app);
                } catch (URISyntaxException e) {
                    yield Path.of(new File(".").getAbsolutePath());
                }
            }
        };
    }

    @Getter
    @AllArgsConstructor
    public enum Colors {
        DARK("dark", "Dark"),
        LIGHT("light", "Light");

        private final String prefix;
        private final String name;
    }
}

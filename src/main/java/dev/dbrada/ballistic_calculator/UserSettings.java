package dev.dbrada.ballistic_calculator;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.*;

public abstract class UserSettings {
    public static Colors colorPreset = Colors.DARK;
    public static Languages lang = Languages.EN_US;
    public static Map<String, Enum<?>> defaultUnits = defaultUnitsInit();

    private static ResourceBundle bundle = ResourceBundle.getBundle("langs/lang", lang.locale);

    public static String getStr(String key) {
        return bundle.getString(key);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void init() {
        File file = new File(Path.of(getConfigPath().toString(), "config").toString());
        if (!file.exists()) return;

        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(file)) {
            props.load(in);
            Map<String, Enum<?>> temp = new HashMap<>();
            for (String s : defaultUnits.keySet()) {
                temp.put(s, (Enum<?>) defaultUnits.get(s).getClass().getMethod("valueOf", String.class).invoke(null, props.getProperty(s)));
            }
            for (String s : defaultUnits.keySet()) {
                boolean notValid = true;
                for (Enum<?> e : Constants.ALLOWED_UNITS.get(s)) {
                    if (e.equals(temp.get(s))) {
                        notValid = false;
                        break;
                    }
                }
                if (notValid) throw new IllegalStateException("Default unit is not in allowed");
            }
            colorPreset = Colors.valueOf(props.getProperty("colorPreset"));
            lang = Languages.valueOf(props.getProperty("lang"));
            defaultUnits = temp;
            bundle = ResourceBundle.getBundle("langs/lang", lang.locale);
        } catch (IOException | NumberFormatException | NoSuchMethodException | IllegalAccessException |
                 InvocationTargetException | IllegalStateException e) {
            System.err.println("No load done\n" + e.getMessage());
            file.delete();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void save() {
        Properties props = new Properties();
        props.setProperty("colorPreset", colorPreset.toString());
        props.setProperty("lang", lang.toString());
        for (Map.Entry<String, Enum<?>> e : defaultUnits.entrySet()) {
            props.setProperty(e.getKey(), e.getValue().toString());
        }

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

    private static Map<String, Enum<?>> defaultUnitsInit() {
        HashMap<String, Enum<?>> result = new HashMap<>();
        for (Map.Entry<String, Enum<?>[]> e : Constants.ALLOWED_UNITS.entrySet()) {
            result.put(e.getKey(), e.getValue()[0]);
        }
        return result;
    }

    @Getter
    @AllArgsConstructor
    public enum Colors {
        DARK("dark", "scheme.dark"),
        LIGHT("light", "scheme.light");

        private final String prefix;
        private final String name;
    }

    @Getter
    @AllArgsConstructor
    public enum Languages {
        EN_US("English - US", Locale.US),
        EN_UK("English - UK", Locale.UK);

        private final String name;
        private final Locale locale;
    }
}

package dev.dbrada.ballistic_calculator;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import dev.dbrada.ballistic_calculator.units.*;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Handles parameters for calculation
 * @param diameter projectile diameter
 * @param mass projectile mass
 * @param velocity projectile muzzle velocity
 * @param balCoef projectile ballistic coefficient
 * @param zeroRange rifle zero range
 * @param sightHeight rifle sight overbore
 * @param twistRate rifle muzzle twist rate
 * @param temperature env temperature
 * @param humidity env humidity
 * @param windSpeed env wind speed
 * @param windAzimuth env speed rotation
 * @param pressure env pressure
 * @param shotAngle rotation of muzzle
 * @param maxRange max calculation range
 * @param rangeStep steps to max range
 * @param range output range unit
 * @param outputDeviationL output deviation unit
 * @param outputDeviationA output deviation units
 * @param outputTime output time of flight?
 * @param outputVelocity output velocity?
 */
public record Parameters(Length diameter, Mass mass, Speed velocity, BallisticCoefficient balCoef, Length zeroRange,
                         Length sightHeight, Length twistRate, Temperature temperature, double humidity,
                         Speed windSpeed, Angle windAzimuth, Pressure pressure, Angle shotAngle, Length maxRange,
                         Length rangeStep, Length.ELength range, Length.ELength outputDeviationL, Angle.EAngle[] outputDeviationA,
                         boolean outputTime, boolean outputVelocity) {

    /**
     * Saves parameters
     * @param fileName file name
     * @param p saves parameters
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean save(String fileName, Parameters p) {
        Path path = getSavePath();
        new File(path.toUri()).mkdir();

        try (FileOutputStream out = new FileOutputStream(path + "/" + fileName + ".bcs");
             OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {

            Gson gson = new Gson();
            gson.toJson(p, writer);

        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * Loads parameters from file
     * @param fileName file with save
     * @return parameters
     */
    public static Parameters load(String fileName) {
        try(FileInputStream in = new FileInputStream(getSavePath() + "/" + fileName + ".bcs")) {
            Gson gson = new Gson();
            return gson.fromJson(new JsonReader(new InputStreamReader(in)), TypeToken.get(Parameters.class));
        } catch (IOException e) {
            throw new IllegalStateException("Invalid file\n" + e.getMessage());
        }
    }

    /**
     * Deletes a file
     * @param fileName deleted file
     */
    public static void delete(String fileName) {
        try {
            Files.delete(Path.of(getSavePath().toString(), fileName+".bcs"));
        } catch (IOException _) {}
    }

    /**
     * Restrives all files in save folder with suffix .bcs
     * @return paths of saves
     */
    public static Path[] getSaves() {
        try(Stream<Path> files = Files.list(getSavePath())) {
            List<Path> saves = new ArrayList<>();
            for (Path p : files.toList()) {
                if (p.toString().endsWith(".bcs")) saves.add(p);
            }
            return saves.toArray(new Path[0]);
        } catch (IOException e) {
            return new Path[0];
        }
    }

    /**
     * @return path to system default saves folder
     */
    public static Path getSavePath() {
        String os = System.getProperty("os.name").toLowerCase();
        String app = "ballistic-calculator";
        String home = System.getProperty("user.home");
        return switch (os) {
            case String s when s.contains("win")  -> Path.of(home, "AppData", "Roaming", app, "saves");
            case String s when s.contains("mac")  -> Path.of(home, "Library", "Application Support", app, "saves");
            case String s when s.contains("nix") || s.contains("nux") || s.contains("aix") -> Path.of(home, ".local", "share" , app);
            default -> {
                try {
                    yield Path.of(new File(UserSettings.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent(), app, "saves");
                } catch (URISyntaxException e) {
                    yield Path.of(new File(".").getAbsolutePath(), "saves");
                }
            }
        };
    }
}

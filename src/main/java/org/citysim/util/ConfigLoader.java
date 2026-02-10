package org.citysim.util;

import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final Properties props = new Properties();

    static {
        try (InputStream file = ConfigLoader.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (file == null) {
                throw new RuntimeException("config.properties file not found in resources!");
            }
            props.load(file);
            validate();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load config.properties", ex);
        }
    }

    /**
     * Gets positive integer configuration value
     * @param key - configuration key
     * @return parsed positive integer
     * @throws RuntimeException if missing, invalid, or negative
     */
    public static int getInt(String key) {
        String value = getRequired(key);
        int parsed;
        try {
            parsed = Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new RuntimeException("Invalid int value for key " + key + ": " + value);
        }
        if (parsed <= 0) {
            throw new RuntimeException("Value for key " + key + " must be positive");
        }
        return parsed;
    }

    /**
     * Gets positive double configuration value
     * @param key - configuration key
     * @return parsed positive double
     * @throws RuntimeException if missing, invalid, or negative
     */
    public static double getDouble(String key) {
        String value = getRequired(key);
        double parsed;
        try {
            parsed = Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            throw new RuntimeException("Invalid double value for key " + key + ": " + value);
        }
        if (parsed <= 0) {
            throw new RuntimeException("Value for key " + key + " must be positive");
        }
        return parsed;
    }

    /**
     * Fetches a required raw configuration value
     * @param key - configuration key
     * @return not empty value
     * @throws RuntimeException if missing or empty
     */
    private static String getRequired(String key) {
        String value = props.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Missing required config property: " + key);
        }
        value = value.trim();
        if (value.isEmpty()) {
            throw new RuntimeException("Config property " + key + " is empty");
        }
        return value;
    }

    /**
     * Validates all required configuration keys exist
     * @throws RuntimeException if any key is missing
     */
    private static void validate() {
        String[] keys = {
                "simulation.duration",
                "simulation.timeout",
                "thread.pool.size",
                "traffic.int.sec",
                "street.int.sec",
                "bike.int.sec",
                "air.int.sec",
                "traffic.fixed.green",
                "traffic.adapt.min",
                "traffic.adapt.max",
                "traffic.vehicle.threshold",
                "traffic.scale.factor",
                "bike.rent.probability",
                "bike.min.capacity",
                "bike.max.capacity",
                "bike.min.bikes",
                "bike.max.bikes",
                "bike.min.chargers",
                "bike.max.chargers",
                "bike.min.charge",
                "bike.max.charge",
                "air.quality.threshold",
                "air.pm.base",
                "air.pm.range",
                "air.max.history",
                "street.dark.start",
                "street.dark.end"
        };

        for (String key : keys) {
            if (!props.containsKey(key)) {
                throw new RuntimeException("Config property missing: " + key);
            }
        }
    }
}
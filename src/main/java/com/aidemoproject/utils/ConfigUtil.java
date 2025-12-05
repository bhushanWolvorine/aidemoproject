package com.aidemoproject.utils;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigUtil {

    private static final Properties properties = new Properties();

    static {
        loadProperties();
    }

    private ConfigUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    private static void loadProperties() {
        try (InputStream input = ConfigUtil.class.getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (input == null) {
                System.out.println("config.properties not found → using defaults");
                properties.setProperty("websocket.url", "ws://localhost:8765");
            } else {
                properties.load(input);
                System.out.println("config.properties loaded successfully");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    public static String getWebSocketUrl() {
        return properties.getProperty("websocket.url", "ws://localhost:8765");
    }


    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}
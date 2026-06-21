package com.apiAuto.base;

import java.io.InputStream;
import java.util.Properties;

public class ConfigProperties {
    private static final Properties props = new Properties();

    static {
        // 1. Загружаем файл config.properties
        try (InputStream in = ConfigProperties.class.getClassLoader().getResourceAsStream("config.properties")) {
            props.load(in);
        } catch (Exception e) {
            throw new RuntimeException("config.properties not found", e);
        }

        // 2. Переопределяем значения, если они переданы из Jenkins
        overrideFromSystemProperties();
    }

    /**
     * Если в JVM передан параметр -Dbase.uri=... — он заменяет значение из файла
     */
    private static void overrideFromSystemProperties() {
        String[] keys = {"base.uri", "base.path", "user.email", "user.password"};
        for (String key : keys) {
            String value = System.getProperty(key);
            if (value != null && !value.isEmpty()) {
                props.setProperty(key, value);
            }
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}
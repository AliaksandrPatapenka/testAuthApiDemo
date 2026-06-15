package com.apiAuto.base;

import java.io.InputStream;
import java.util.Properties;

public class ConfigProperties {
    private static final Properties props = new Properties();


    static {
        try (InputStream in = ConfigProperties.class.getClassLoader().getResourceAsStream("config.properties")) {
            props.load(in);
        } catch (Exception e) {
            throw new RuntimeException("config.properties not found", e);
        }
    } //Загружает конфигурацию из файла config.properties в папке resources.

    /**
     * Возвращает значение key ключ из .properties файла
     * Значение или null, если ключ не найден
     */
    public static String get(String key) {
        return props.getProperty(key);
    }
}
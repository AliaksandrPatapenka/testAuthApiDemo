package com.apiAuto.base.properties.config;

import java.io.InputStream;
import java.util.Properties;

/**
 * Централизованный загрузчик конфигурации для тестового фреймворка.
 * <p>
 * <b>Приоритет получения значений (от высшего к низшему):</b>
 * <ol>
 *   <li>Системные свойства JVM (передаются через <code>-D</code> в Jenkins или IDEA)</li>
 *   <li>Файл <code>local.properties</code> в <code>src/test/resources</code></li>
 * </ol>
 * <p>
 * <b>Пример использования:</b>
 * <pre>
 * String baseUri = ConfigProperties.get("base.uri");
 * String userEmail = ConfigProperties.get("user.email");
 * </pre>
 * <p>
 * <b>Для локального запуска:</b> создай <code>local.properties</code> с нужными ключами.
 * <br>
 * <b>Для Jenkins:</b> передавай параметры через <code>-Dbase.uri=...</code>.
 */
public final class ConfigProperties {
    private static final Properties props = new Properties();

    static {
        // 1. Загрузка файла local.properties
        try (InputStream in = ConfigProperties.class.getClassLoader().getResourceAsStream("local.properties")) {
            props.load(in);
        } catch (Exception e) {
            throw new RuntimeException("local.properties not found in classpath", e);
        }
        // 2. Переопределение системными свойствами
        overrideFromSystemProperties();
    }

    /**
     * Переопределяет значения из файла системными свойствами JVM.
     * <p>
     * Если передан параметр <code>-Dbase.uri=https://dev.api.com</code>,
     * то значение ключа <code>base.uri</code> будет заменено на <code>https://dev.api.com</code>.
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

    /**
     * Возвращает значение конфигурации по ключу.
     *
     * @param key ключ в формате <code>base.uri</code> или <code>user.email</code>
     * @return значение параметра или <code>null</code>, если ключ не найден
     */
    public static String get(String key) {
        return props.getProperty(key);
    }
}
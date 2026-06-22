package com.apiAuto.helpers.testHelper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Хранилище данных между тестами через JSON-файл.
 * <p>
 * Данные сохраняются в {@code target/data.json} и доступны между разными тестами в рамках одной сборки.
 * <p>
 * <b>Пример использования:</b>
 * <pre>
 * // Сохранить данные
 * JsonFileStorage.put("userId", 123);
 * JsonFileStorage.put("userEmail", "test@mail.com");
 *
 * // Получить данные
 * int userId = JsonFileStorage.getInt("userId");
 * String email = JsonFileStorage.getString("userEmail");
 *
 * // Очистить после всех тестов
 * JsonFileStorage.clear();
 * </pre>
 */
public final class JsonFileStorage {
    private static final String FILE = "target/data.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Сохраняет значение по ключу в JSON-файл.
     *
     * @param key   ключ для сохранения
     * @param value значение (может быть любого типа: String, Integer, Map, List и т.д.)
     * @throws RuntimeException если не удалось сохранить файл
     */
    public static void put(String key, Object value) {
        try {
            Map<String, Object> data = loadAll();
            data.put(key, value);
            Files.writeString(Path.of(FILE), JsonContext.toJson(data));
        } catch (Exception error) {
            throw new RuntimeException("Не удалось сохранить данные в JSON файл", error);
        }
    }

    /**
     * Возвращает значение по ключу.
     *
     * @param key ключ
     * @return значение или {@code null}, если ключ не найден
     */
    public static Object get(String key) {
        return loadAll().get(key);
    }

    /**
     * Возвращает значение по ключу как {@code int}.
     *
     * @param key ключ
     * @return значение, приведённое к {@code int}
     * @throws ClassCastException если значение не является числом
     */
    public static int getInt(String key) {
        return (int) get(key);
    }

    /**
     * Возвращает значение по ключу как {@code String}.
     *
     * @param key ключ
     * @return значение, приведённое к {@code String}
     * @throws ClassCastException если значение не является строкой
     */
    public static String getString(String key) {
        return (String) get(key);
    }

    /**
     * Загружает все данные из JSON-файла.
     *
     * @return {@code Map} с данными или пустая {@code Map}, если файла нет
     */
    private static Map<String, Object> loadAll() {
        try {
            String json = Files.readString(Path.of(FILE));
            return mapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            return new HashMap<>();
        }
    }

    /**
     * Удаляет файл с сохранёнными данными.
     *
     * @throws RuntimeException если не удалось удалить файл
     */
    public static void clear() {
        try {
            Files.deleteIfExists(Path.of(FILE));
        } catch (IOException e) {
            throw new RuntimeException("Не удалось очистить файл", e);
        }
    }
}
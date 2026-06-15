package com.apiAuto.helpers.testHelper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Хранилище данных между тестами через JSON-файл.
 * Извлечение данных в тесте: JsonContext.put("key_in_file", response.path("key_in_json"));
 * Получение данных в тестеЖ
 */
public class JsonContext {
    private static final String FILE = "target/data.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }//Метод toJson() превращает Java-объект в JSON-строку для отправки в запросе.

    public static void put(String key, Object value) {
        try {
            Map<String, Object> data = loadAll(); //Загружаем всё, что уже сохранено в файле
            data.put(key, value); //Добавляем новую пару (ключ → значение)
            Files.writeString(Path.of(FILE), mapper.writeValueAsString(data)); //Превращаем Map в JSON-строку и записываем в файл
        } catch (Exception error) {
            throw new RuntimeException("Не удалось сохранить данные в JSON файл", error);
        }
    }

    public static Object get(String key) {
        return loadAll().get(key);
    }

    public static int getInt(String key) {
        return (int) get(key); //(int) — приводим Object к int
    }

    public static String getString(String key) {
        return (String) get(key); //(String) — приводим Object к String
    }

    private static Map<String, Object> loadAll() {
        try {
            String json = Files.readString(Path.of(FILE)); // Читаем весь файл в одну строку
            return mapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            return new HashMap<>(); // Если файла нет или он пустой — возвращаем пустую Map
        }
    }

    public static void clear() {
        try {
            Files.deleteIfExists(Path.of(FILE));
        } catch (IOException e) {
            throw new RuntimeException("Не удалось очистить файл", e);
        }
    }
}

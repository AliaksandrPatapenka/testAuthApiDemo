package com.apiAuto.base.properties.config;

import java.io.InputStream;
import java.util.Properties;

public final class UserData {
    private static final Properties props = new Properties();

    static {
        try (InputStream in = UserData.class.getResourceAsStream("/local.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (Exception e) {
            // ignore
        }
    } // Загружает local.properties для локального запуска. В Jenkins переопределяется через -D.


    /**
     * Валидные тестовые пользователи<br>
     * Email и password прописаны в Jenkins /manage/credentials/store/system/domain
     */
    public static final String USER_EMAIL = System.getProperty("user.email", props.getProperty("user.email"));
    public static final String USER_PASSWORD = System.getProperty("user.password", props.getProperty("user.password"));


    /**
     *Невалидные тестовые данные пользователя
     */
    public static final String NON_EXISTENT_EMAIL = System.getProperty("user.nonExistentEmail", "nonExistentEmail@test.ru");
    public static final String INVALID_FORMAT_EMAIL = System.getProperty("user.invalidFormatEmail", "@@@");
    public static final String INVALID_PASSWORD = System.getProperty("user.invalidPassword", "invalidPassword");


    /**
     *Тестовые данные
     */
    public static final String IMAGE_URI = System.getProperty("image.uri", "https://test.com");
    public static final String EMAIL_TEMPLATE = System.getProperty("email.template", "Test_%s@example.com");
    public static final String USER_NAME= System.getProperty("user.name", "Test_%s");
}

package com.apiAuto.helpers.testHelper;

import com.apiAuto.base.ConfigProperties;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestDataGenerator {
    public static String timeIndex() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
    } //Генерация текущих даты и времени

    public static String randomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    } //Генератор случайной строки. Цифры, латиница верхний и нижний регистры. 6 знаков

    public static String generatorEmail(String timeIndex) {
        String template = ConfigProperties.get("email.template");
        return String.format(template, timeIndex);
    } //Генератор Email. Формат Test_yyyy-MM-dd_HH-mm-ss@example.com. Передаем текущее время из timeIndex и подставляем в user.email из config.properties

    public static String generatorName(String timeIndex) {
        String template = ConfigProperties.get("user.name");
        return String.format(template, timeIndex);
    } //Генератор name пользователя. Формат Test_yyyy-MM-dd_HH-mm-ss. Передаем текущее время из timeIndex и подставляем в user.email из config.properties


}







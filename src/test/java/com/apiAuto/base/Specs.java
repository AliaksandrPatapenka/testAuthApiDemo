package com.apiAuto.base;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.RestAssured.basePath;
import static io.restassured.RestAssured.baseURI;

/**
 * Настройки запросов и ответов для RestAssured.
 * Содержит базовый URL, заголовки, фильтр Allure и логирование
 */
public class Specs {
    static {
        baseURI = "https://api.escuelajs.co";
        RestAssured.basePath = "/api/v1";
    }

    public static final AllureRestAssured allureFilter = new AllureRestAssured();

    public static RequestSpecification requestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(baseURI)
                .setBasePath(basePath)
                .setContentType("application/json")
                .log(LogDetail.ALL)
                .addFilter(allureFilter)
                .setConfig(RestAssured.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", Integer.parseInt(ConfigProperties.get("http.connection.timeout")))
                        .setParam("http.socket.timeout", Integer.parseInt(ConfigProperties.get("http.socket.timeout")))))
                .build();
    }

    public static ResponseSpecification responseSpec() {
        return new ResponseSpecBuilder()
                .log(LogDetail.ALL)
                .build();
    }

}

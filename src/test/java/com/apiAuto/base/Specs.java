package com.apiAuto.base;

import com.apiAuto.base.properties.config.TestProperties;
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
        baseURI = TestProperties.BASE_URI;
        RestAssured.basePath = TestProperties.BASE_PATH;
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
                        .setParam("http.connection.timeout", TestProperties.HTTP_CONNECTION_TIMEOUT)
                        .setParam("http.socket.timeout", TestProperties.HTTP_SOCKET_TIMEOUT)))
                .build();
    }

    public static ResponseSpecification responseSpec() {
        return new ResponseSpecBuilder()
                .log(LogDetail.ALL)
                .build();
    }

}

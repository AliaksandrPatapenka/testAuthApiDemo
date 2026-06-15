package com.apiAuto.test.auth;

import com.apiAuto.base.ConfigProperties;
import com.apiAuto.helpers.authHelper.UserLoginHelper;
import com.apiAuto.models.auth.UserLogin;
import com.apiAuto.helpers.testHelper.JsonContext;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static com.apiAuto.base.Specs.requestSpec;
import static com.apiAuto.base.Specs.responseSpec;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class PostLoginTest {

    /**
     * ==================== ПОЗИТИВНЫЕ ТЕСТЫ ====================
     *
     */
    @Nested
    @DisplayName("POST /login. PositiveTests")
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    public class PositiveTests {
        @Test
        @Order(1)
        @DisplayName("Case1: Авторизация пользователя")
        void userLogin() {
            UserLogin user = new UserLogin();
            user.setEmail(ConfigProperties.get("user.email"));
            user.setPassword(ConfigProperties.get("user.password"));
            String requestBody = JsonContext.toJson(user);

            Response response = given(requestSpec())
                    .body(requestBody).when()
                    .post(ConfigProperties.get("endpoint.login"))
                    .then().spec(responseSpec()).statusCode(201)
                    .body(matchesJsonSchemaInClasspath("schemas/userAuthSchema/userAuthSchema.json"))
                    .extract().response();

            JsonContext.put("access_token", response.path("access_token"));
            JsonContext.put("refresh_token", response.path("refresh_token"));
        }

    }

    /**
     * ==================== НЕГАТИВНЫЕ ТЕСТЫ ====================
     */
    @Nested
    @DisplayName("POST /login. NegativeTests")
    @Order(2)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    public class NegativeTests {
        @Test
        @Order(1)
        @DisplayName("Case1: Неверный логин пользователя")
        void incorrectEmail() {
            String userEmailLogin = ConfigProperties.get("user.invalidEmail");
            String userPasswordLogin = ConfigProperties.get("user.password");

            UserLoginHelper.userLogin(userEmailLogin, userPasswordLogin)
                    .then().spec(responseSpec())
                    .statusCode(401)
                    .body(matchesJsonSchemaInClasspath("schemas/errorSchema/401errorSchema.json"));
        }

        @Test
        @Order(2)
        @DisplayName("Case2: Неверный пароль пользователя")
        void incorrectPassword() {
            String userEmailLogin = ConfigProperties.get("user.email");
            String userPasswordLogin = ConfigProperties.get("user.invalidPassword");

            UserLoginHelper.userLogin(userEmailLogin, userPasswordLogin)
                    .then().spec(responseSpec())
                    .statusCode(401)
                    .body(matchesJsonSchemaInClasspath("schemas/errorSchema/401errorSchema.json"));

        }

    }
}

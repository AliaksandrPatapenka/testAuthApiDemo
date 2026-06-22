package com.apiAuto.test.auth;

import com.apiAuto.base.properties.config.UserData;
import com.apiAuto.base.properties.patch.AuthPatch;
import com.apiAuto.helpers.authHelper.UserLoginHelper;
import com.apiAuto.models.auth.UserLogin;
import com.apiAuto.helpers.testHelper.JsonContext;
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
            user.setEmail(UserData.USER_EMAIL);
            user.setPassword(UserData.USER_PASSWORD);
            String requestBody = JsonContext.toJson(user);

            given(requestSpec())
                    .body(requestBody).when()
                    .post(AuthPatch.ENDPOINT_LOGIN)
                    .then().spec(responseSpec()).statusCode(201)
                    .body(matchesJsonSchemaInClasspath("schemas/userAuthSchema/userAuthSchema.json"));
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
            String userEmailLogin = UserData.NON_EXISTENT_EMAIL;
            String userPasswordLogin = UserData.USER_PASSWORD;

            UserLoginHelper.userLogin(userEmailLogin, userPasswordLogin)
                    .then().spec(responseSpec())
                    .statusCode(401)
                    .body(matchesJsonSchemaInClasspath("schemas/errorSchema/401errorSchema.json"));
        }

        @Test
        @Order(2)
        @DisplayName("Case2: Неверный пароль пользователя")
        void incorrectPassword() {
            String userEmailLogin = UserData.USER_EMAIL;
            String userPasswordLogin = UserData.INVALID_PASSWORD;

            UserLoginHelper.userLogin(userEmailLogin, userPasswordLogin)
                    .then().spec(responseSpec())
                    .statusCode(401)
                    .body(matchesJsonSchemaInClasspath("schemas/errorSchema/401errorSchema.json"));

        }

    }
}

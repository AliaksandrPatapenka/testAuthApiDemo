package com.apiAuto.test.auth;

import com.apiAuto.base.ConfigProperties;
import com.apiAuto.helpers.authHelper.UserLoginHelper;
import com.apiAuto.helpers.testHelper.JsonContext;
import com.apiAuto.models.auth.UserRefreshToken;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static com.apiAuto.base.Specs.requestSpec;
import static com.apiAuto.base.Specs.responseSpec;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.core.IsNot.not;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class PostRefreshTokenTest {
    /**
     * ==================== ПОЗИТИВНЫЕ ТЕСТЫ ====================
     *
     */
    @Nested
    @DisplayName("POST /refresh-token. PositiveTests")
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    public class PositiveTests {
        @Test
        @DisplayName("Case1: Обновление токена")
        void userRefreshToken() {
            String userEmailLogin = ConfigProperties.get("user.email");
            String userPasswordLogin = ConfigProperties.get("user.password");

            Response loginResponse = UserLoginHelper.userLogin(userEmailLogin, userPasswordLogin);
            String userRefToken = loginResponse.jsonPath().getString("refresh_token");

            UserRefreshToken jsonRequest = new UserRefreshToken();
            jsonRequest.setRefreshToken(userRefToken);
            String requestBody = JsonContext.toJson(jsonRequest);


            given(requestSpec())
                    .body(requestBody)
                    .when()
                    .post(ConfigProperties.get("endpoint.refreshToken"))
                    .then().spec(responseSpec())
                    .statusCode(201)
                    .body("access_token", not(emptyString()))
                    .body("refresh_token", not(emptyString()))
                    .body(matchesJsonSchemaInClasspath("schemas/userAuthSchema/userAuthSchema.json")); //Переиспользуем схему authUserSchema.json, она идентична
        }
    }

    /**
     * ==================== НЕГАТИВНЫЕ ТЕСТЫ ====================
     */
    @Nested
    @DisplayName("POST /refresh-token. NegativeTests")
    @Order(2)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    public class NegativeTests {
        @Test
        @DisplayName("Case4: Ошибка обновления токена(пустое тело запроса)")
        void incorrectRefreshToken() {
            given(requestSpec()).body("{}")
                    .when()
                    .post(ConfigProperties.get("endpoint.refreshToken"))
                    .then().spec(responseSpec()).statusCode(400)
                    .body(matchesJsonSchemaInClasspath("schemas/errorSchema/400errorSchema.json"));
        }
    }
}

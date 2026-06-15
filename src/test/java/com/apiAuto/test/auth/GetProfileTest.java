package com.apiAuto.test.auth;

import com.apiAuto.base.ConfigProperties;
import com.apiAuto.helpers.authHelper.UserLoginHelper;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static com.apiAuto.base.Specs.requestSpec;
import static com.apiAuto.base.Specs.responseSpec;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class GetProfileTest {

    /**
     * ==================== ПОЗИТИВНЫЕ ТЕСТЫ ====================
     *
     */
    @Nested
    @DisplayName("GET /Profile. PositiveTests")
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    public class PositiveTests {
        @Test
        @DisplayName("Case1: Переход в профиль пользователя")
        void userProfile() {
            String userEmailLogin = ConfigProperties.get("user.email");
            String userPasswordLogin = ConfigProperties.get("user.password");

            Response loginResponse = UserLoginHelper.userLogin(userEmailLogin, userPasswordLogin);
            String tokenAuth = loginResponse.jsonPath().getString("access_token");

            given(requestSpec())
                    .header("Authorization", "Bearer " + tokenAuth)
                    .when()
                    .get(ConfigProperties.get("endpoint.profile"))
                    .then()
                    .spec(responseSpec())
                    .statusCode(200)
                    .body("email", equalTo(userEmailLogin))
                    .body(matchesJsonSchemaInClasspath("schemas/userAuthSchema/userProfileSchema.json"));

        }

    }

    /**
     * ==================== НЕГАТИВНЫЕ ТЕСТЫ ====================
     */
    @Nested
    @DisplayName("GET /Profile. NegativeTests")
    @Order(2)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    public class NegativeTests {
        @Test
        @DisplayName("Case1: Ответ 401 при переходе в профиль пользователя")
        void incorrectProfile() {
            given(requestSpec())
                    .when()
                    .get(ConfigProperties.get("endpoint.profile"))
                    .then()
                    .spec(responseSpec())
                    .statusCode(401)
                    .body(matchesJsonSchemaInClasspath("schemas/errorSchema/401errorSchema.json"));

        }
    }
}

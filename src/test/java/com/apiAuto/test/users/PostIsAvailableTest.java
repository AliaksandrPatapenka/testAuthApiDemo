package com.apiAuto.test.users;

import com.apiAuto.base.properties.config.UserData;
import com.apiAuto.base.properties.patch.UsersPatch;
import com.apiAuto.helpers.testHelper.JsonContext;
import com.apiAuto.models.users.CheckEmail;
import org.junit.jupiter.api.*;

import static com.apiAuto.base.Specs.requestSpec;
import static com.apiAuto.base.Specs.responseSpec;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class PostIsAvailableTest {

    /**
     * ==================== ПОЗИТИВНЫЕ ТЕСТЫ ====================
     */

    @Nested
    @DisplayName("POST /is-available. PositiveTests")
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class PositiveTests {
        @Test
        @DisplayName("Case 5.1: Проверка  доступности email (Пользователь существует)")
        void checkEmailExist() {
            String userEmail = UserData.USER_EMAIL;

            CheckEmail checkEmail = new CheckEmail();
            checkEmail.setEmail(userEmail);
            String requestBody = JsonContext.toJson(checkEmail);

            given(requestSpec())
                    .body(requestBody)
                    .when()
                    .post(UsersPatch.ENDPOINT_IS_AVAILABLE)
                    .then()
                    .spec(responseSpec())
                    .statusCode(201)
                    .body("isAvailable", equalTo(false))
                    .body(matchesJsonSchemaInClasspath("schemas/userCrudSchema/checkEmail.json"));

        }

        @Test
        @DisplayName("Case 5.2: Проверка  доступности email (Пользователь не существует)")
        void checkEmailNoExist() {
            CheckEmail checkEmail = new CheckEmail();
            checkEmail.setEmail(UserData.NON_EXISTENT_EMAIL);
            String requestBody = JsonContext.toJson(checkEmail);

            given(requestSpec())
                    .body(requestBody)
                    .when()
                    .post(UsersPatch.ENDPOINT_IS_AVAILABLE)
                    .then()
                    .spec(responseSpec())
                    .statusCode(201)
                    .body("isAvailable", equalTo(false))
                    .body(matchesJsonSchemaInClasspath("schemas/userCrudSchema/checkEmail.json"));

        }
    }

    /**
     * ==================== НЕГАТИВНЫЕ ТЕСТЫ ====================
     */

    @Nested
    @DisplayName("POST /is-available. NegativeTests")
    @Order(2)
    class NegativeTests {
        @Test
        @DisplayName("Case 5.1: Проверка  доступности email при email = null")
        void checkEmailNull() {
            CheckEmail checkEmail = new CheckEmail();
            checkEmail.setEmail(null);
            String requestBody = JsonContext.toJson(checkEmail);

            given(requestSpec())
                    .body(requestBody)
                    .when()
                    .post(UsersPatch.ENDPOINT_IS_AVAILABLE)
                    .then()
                    .spec(responseSpec())
                    .statusCode(400)
                    .body(matchesJsonSchemaInClasspath("schemas/errorSchema/400errorSchema.json"));
        }
    }
}

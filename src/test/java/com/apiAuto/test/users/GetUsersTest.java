package com.apiAuto.test.users;

import com.apiAuto.base.ConfigProperties;
import com.apiAuto.helpers.userHelper.UserCreateTemplate;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static com.apiAuto.base.Specs.requestSpec;
import static com.apiAuto.base.Specs.responseSpec;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

public class GetListTest {

    /**
     * ==================== ПОЗИТИВНЫЕ ТЕСТЫ ====================
     */

    @Nested
    @DisplayName("GET /list. PositiveTests")
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class PositiveTests {
        @Test
        @DisplayName("Case1: Получение списка пользователей")
        void userList() {
            Response createResponse = UserCreateTemplate.userCreateTemplate();
            int userId = createResponse.jsonPath().getInt("id");
            String userEmail = createResponse.jsonPath().getString("email");

            given(requestSpec())
                    .when()
                    .get(ConfigProperties.get("endpoint.users"))
                    .then()
                    .spec(responseSpec())
                    .statusCode(200)
                    .body("find { it.id == " + userId + " }.id", equalTo(userId))
                    .body("find { it.id == " + userId + " }.email", equalTo(userEmail))
                    .body(matchesJsonSchemaInClasspath("schemas/userCrudSchema/userListSchema.json"));
        }
    }


    /**
     * ==================== НЕГАТИВНЫЕ ТЕСТЫ ====================
     */

    @Nested
    @DisplayName("UserCrudTest. NegativeTests")
    @Order(2)
    class NegativeTests {
        @Test
        @DisplayName("Case2: Получение данных по несуществующему пользователю")
        void UserInvalid() {
            given(requestSpec())
                    .when()
                    .get(ConfigProperties.get("endpoint.users") + 0)
                    .then()
                    .spec(responseSpec())
                    .statusCode(400)
                    .body(matchesJsonSchemaInClasspath("schemas/errorSchema/400notFoundSchema.json"));
        }
    }
}

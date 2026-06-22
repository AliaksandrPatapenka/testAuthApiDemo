package com.apiAuto.test.users;


import com.apiAuto.base.properties.patch.UsersPatch;
import com.apiAuto.helpers.userHelper.UserCreateTemplate;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static com.apiAuto.base.Specs.requestSpec;
import static com.apiAuto.base.Specs.responseSpec;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class GetUsersTest {

    /**
     * ==================== ПОЗИТИВНЫЕ ТЕСТЫ ====================
     */

    @Nested
    @DisplayName("GET /users. PositiveTests")
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class PositiveTests {
        @Test
        @DisplayName("Case2.1: Получение списка пользователей")
        void userList() {
            Response createResponse = UserCreateTemplate.userCreateTemplate();
            int userId = createResponse.jsonPath().getInt("id");
            String userEmail = createResponse.jsonPath().getString("email");

            given(requestSpec())
                    .when()
                    .get(UsersPatch.ENDPOINT_USERS)
                    .then()
                    .spec(responseSpec())
                    .statusCode(200)
                    .body("find { it.id == " + userId + " }.id", equalTo(userId))
                    .body("find { it.id == " + userId + " }.email", equalTo(userEmail))
                    .body(matchesJsonSchemaInClasspath("schemas/userCrudSchema/userListSchema.json"));
        }
    }
}

package com.apiAuto.test.users;

import com.apiAuto.base.properties.config.UserData;
import com.apiAuto.base.properties.patch.UsersPatch;
import com.apiAuto.helpers.testHelper.JsonContext;
import com.apiAuto.helpers.testHelper.TestDataGenerator;
import com.apiAuto.helpers.userHelper.UserCreateTemplate;
import com.apiAuto.helpers.userHelper.UserJsonTemplate;
import com.apiAuto.models.users.UserUpdate;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.Map;

import static com.apiAuto.base.Specs.requestSpec;
import static com.apiAuto.base.Specs.responseSpec;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class PutUsersIdTest {

    /**
     * ==================== ПОЗИТИВНЫЕ ТЕСТЫ ====================
     */

    @Nested
    @DisplayName("PUT /users{id}. PositiveTests")
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class PositiveTests {
        @Test
        @DisplayName("Case4.1: Редактирование пользователя")
        void userUpdate() {
            Response createResponse = UserCreateTemplate.userCreateTemplate();
            int userId = createResponse.jsonPath().getInt("id");

            String userName = TestDataGenerator.generatorName(TestDataGenerator.timeIndex());
            String userEmail = TestDataGenerator.generatorEmail(TestDataGenerator.timeIndex());
            String userRole = "admin";
            String userAvatar = UserData.IMAGE_URI;

            UserUpdate jsonRequest = new UserUpdate();
            jsonRequest.setName(userName);
            jsonRequest.setEmail(userEmail);
            jsonRequest.setPassword(TestDataGenerator.randomPassword());
            jsonRequest.setAvatar(userAvatar);
            jsonRequest.setRole(userRole);
            String requestBody = JsonContext.toJson(jsonRequest);

            given(requestSpec())
                    .body(requestBody)
                    .when()
                    .put(UsersPatch.ENDPOINT_USERS + userId)
                    .then()
                    .spec(responseSpec())
                    .statusCode(200)
                    .body("name", equalTo(userName))
                    .body("email", equalTo(userEmail))
                    .body("role", equalTo(userRole))
                    .body("avatar", equalTo(userAvatar))
                    .body(matchesJsonSchemaInClasspath("schemas/userCrudSchema/userUpdateSchema.json"));
        }
    }

    /**
     * ==================== НЕГАТИВНЫЕ ТЕСТЫ ====================
     */
    @Nested
    @DisplayName("PUT /users{id}. NegativeTests")
    @Order(2)
    class NegativeTests {
        @Test
        @DisplayName("Case4.1: Редактирование  пользователя при введенном некорректном Email")
        void userUpdateInvalid() {
            Response createResponse = UserCreateTemplate.userCreateTemplate();
            int userId = createResponse.jsonPath().getInt("id");

            Map<String, Object> jsonRequest = UserJsonTemplate.userJsonTemplate();
            jsonRequest.put("email", UserData.INVALID_FORMAT_EMAIL);
            jsonRequest.remove("password");
            String requestBody = JsonContext.toJson(jsonRequest);

            given(requestSpec())
                    .body(requestBody)
                    .when()
                    .put(UsersPatch.ENDPOINT_USERS + userId)
                    .then()
                    .spec(responseSpec())
                    .statusCode(400)
                    .body(matchesJsonSchemaInClasspath("schemas/errorSchema/400errorSchema.json"));
        }
    }
}

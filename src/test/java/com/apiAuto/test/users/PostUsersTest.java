package com.apiAuto.test.users;

import com.apiAuto.base.properties.config.UserData;
import com.apiAuto.base.properties.patch.UsersPatch;
import com.apiAuto.helpers.testHelper.JsonContext;
import com.apiAuto.helpers.testHelper.TestDataGenerator;
import com.apiAuto.helpers.userHelper.UserJsonTemplate;
import com.apiAuto.models.users.UserCreate;
import org.junit.jupiter.api.*;
import java.util.Map;
import static com.apiAuto.base.Specs.requestSpec;
import static com.apiAuto.base.Specs.responseSpec;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class PostUsersTest {

    /**
     * ==================== ПОЗИТИВНЫЕ ТЕСТЫ ====================
     */

    @Nested
    @DisplayName("POST /users. PositiveTests")
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class PositiveTests {

        @Test
        @DisplayName("Case 1.1: Создание пользователя")
        void userCreate() {
            String timeIndex = TestDataGenerator.timeIndex();
            UserCreate userCreate = new UserCreate();
            userCreate.setName(TestDataGenerator.generatorName(timeIndex));
            userCreate.setEmail(TestDataGenerator.generatorEmail(timeIndex));
            userCreate.setPassword(TestDataGenerator.randomPassword());
            userCreate.setAvatar(UserData.IMAGE_URI);

            given(requestSpec())
                    .body(userCreate)
                    .when()
                    .post(UsersPatch.ENDPOINT_USERS)
                    .then()
                    .spec(responseSpec())
                    .statusCode(201)
                    .body("name", equalTo(userCreate.getName()))
                    .body("email", equalTo(userCreate.getEmail()))
                    .body("avatar", equalTo(userCreate.getAvatar()))
                    .body(matchesJsonSchemaInClasspath("schemas/userCrudSchema/userCreateSchema.json"));
        }


    }


    /**
     * ==================== НЕГАТИВНЫЕ ТЕСТЫ ====================
     */

    @Nested
    @DisplayName("POST /users. NegativeTests")
    @Order(2)
    class NegativeTests {

        @Test
        @Order(1)
        @DisplayName("Case1.1: Создание пользователя при отсутствии в запросе ключа email")
        void userCreateInvalid() {
            Map<String, Object> jsonRequest = UserJsonTemplate.userJsonTemplate();
            jsonRequest.remove("email");

            String requestBody = JsonContext.toJson(jsonRequest);

            given(requestSpec())
                    .body(requestBody)
                    .when()
                    .post(UsersPatch.ENDPOINT_USERS)
                    .then()
                    .spec(responseSpec())
                    .statusCode(400)
                    .body(matchesJsonSchemaInClasspath("schemas/errorSchema/400errorSchema.json"));
        }
    }
}

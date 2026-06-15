package com.apiAuto.test.users;

import com.apiAuto.base.ConfigProperties;
import com.apiAuto.helpers.testHelper.JsonContext;
import com.apiAuto.helpers.testHelper.TestDataGenerator;
import com.apiAuto.helpers.userHelper.UserCreateTemplate;
import com.apiAuto.helpers.userHelper.UserJsonTemplate;
import com.apiAuto.models.users.CheckEmail;
import com.apiAuto.models.users.UserCreate;
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
public class PostCreateTest {

    /**
     * ==================== ПОЗИТИВНЫЕ ТЕСТЫ ====================
     */

    @Nested
    @DisplayName("POST /users. PositiveTests")
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class PositiveTests {

        @Test
        @DisplayName("Case1: Создание пользователя")
        void userCreate() {
            String timeIndex = TestDataGenerator.timeIndex();
            UserCreate userCreate = new UserCreate();
            userCreate.setName(TestDataGenerator.generatorName(timeIndex));
            userCreate.setEmail(TestDataGenerator.generatorEmail(timeIndex));
            userCreate.setPassword(TestDataGenerator.randomPassword());
            userCreate.setAvatar(ConfigProperties.get("image.uri"));

            given(requestSpec())
                    .body(userCreate)
                    .when()
                    .post(ConfigProperties.get("endpoint.users"))
                    .then()
                    .spec(responseSpec())
                    .statusCode(201)
                    .body("name", equalTo(userCreate.getName()))
                    .body("email", equalTo(userCreate.getEmail()))
                    .body("avatar", equalTo(userCreate.getAvatar()))
                    .body(matchesJsonSchemaInClasspath("schemas/userCrudSchema/userCreateSchema.json"));
        }

        @Test
        @Order(2)
        @DisplayName("Case2: Получение списка пользователей")
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

        @Test
        @Order(3)
        @DisplayName("Case3: Получение данных по пользователю")
        void userSingle() {
            Response createResponse = UserCreateTemplate.userCreateTemplate();
            int userId = createResponse.jsonPath().getInt("id");
            String userEmail = createResponse.jsonPath().getString("email");

            given(requestSpec())
                    .when()
                    .get(ConfigProperties.get("endpoint.users") + userId)
                    .then()
                    .spec(responseSpec())
                    .statusCode(200)
                    .body("email", equalTo(userEmail))
                    .body(matchesJsonSchemaInClasspath("schemas/userCrudSchema/userSingleSchema.json"));
        }

        @Test
        @Order(4)
        @DisplayName("Case4: Редактирование пользователя")
        void userUpdate() {
            Response createResponse = UserCreateTemplate.userCreateTemplate();
            int userId = createResponse.jsonPath().getInt("id");

            String userName = TestDataGenerator.generatorName(TestDataGenerator.timeIndex());
            String userEmail = TestDataGenerator.generatorEmail(TestDataGenerator.timeIndex());
            String userRole = "admin";
            String userAvatar = ConfigProperties.get("image.uri");

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
                    .put(ConfigProperties.get("endpoint.users") + userId)
                    .then()
                    .spec(responseSpec())
                    .statusCode(200)
                    .body("name", equalTo(userName))
                    .body("email", equalTo(userEmail))
                    .body("role", equalTo(userRole))
                    .body("avatar", equalTo(userAvatar))
                    .body(matchesJsonSchemaInClasspath("schemas/userCrudSchema/userUpdateSchema.json"));
        }

        @Test
        @Order(5)
        @DisplayName("Case5: Проверка  доступности email (Пользователь существует)")
        void checkEmailExist() {
            String userEmail = ConfigProperties.get("user.email");

            CheckEmail checkEmail = new CheckEmail();
            checkEmail.setEmail(userEmail);
            String requestBody = JsonContext.toJson(checkEmail);

            given(requestSpec())
                    .body(requestBody)
                    .when()
                    .post(ConfigProperties.get("endpoint.is_available"))
                    .then()
                    .spec(responseSpec())
                    .statusCode(201)
                    .body("isAvailable", equalTo(false))
                    .body(matchesJsonSchemaInClasspath("schemas/userCrudSchema/checkEmail.json"));

        }

        @Test
        @Order(6)
        @DisplayName("Case5: Проверка  доступности email (Пользователь не существует)")
        void checkEmailNoExist() {
            CheckEmail checkEmail = new CheckEmail();
            checkEmail.setEmail(ConfigProperties.get("user.nonExistentEmail"));
            String requestBody = JsonContext.toJson(checkEmail);

            given(requestSpec())
                    .body(requestBody)
                    .when()
                    .post(ConfigProperties.get("endpoint.is_available"))
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
    @DisplayName("POST /users. NegativeTests")
    @Order(2)
    class NegativeTests {

        @Test
        @Order(1)
        @DisplayName("Case1: Создание пользователя при отсутствии в запросе ключа email")
        void userCreateInvalid() {
            Map<String, Object> jsonRequest = UserJsonTemplate.userJsonTemplate();
            jsonRequest.remove("email");

            String requestBody = JsonContext.toJson(jsonRequest);

            given(requestSpec())
                    .body(requestBody)
                    .when()
                    .post(ConfigProperties.get("endpoint.users"))
                    .then()
                    .spec(responseSpec())
                    .statusCode(400)
                    .body(matchesJsonSchemaInClasspath("schemas/errorSchema/400errorSchema.json"));
        }

        @Test
        @Order(2)
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

        @Test
        @Order(3)
        @DisplayName("Case3: Редактирование  пользователя при введенном некорректном Email")
        void userUpdateInvalid() {
            Response createResponse = UserCreateTemplate.userCreateTemplate();
            int userId = createResponse.jsonPath().getInt("id");

            Map<String, Object> jsonRequest = UserJsonTemplate.userJsonTemplate();
            jsonRequest.put("email", ConfigProperties.get("user.invalidFormatEmail"));
            jsonRequest.remove("password");
            String requestBody = JsonContext.toJson(jsonRequest);

            given(requestSpec())
                    .body(requestBody)
                    .when()
                    .put(ConfigProperties.get("endpoint.users") + userId)
                    .then()
                    .spec(responseSpec())
                    .statusCode(400)
                    .body(matchesJsonSchemaInClasspath("schemas/errorSchema/400errorSchema.json"));
        }

        @Test
        @Order(4)
        @DisplayName("Case5: Проверка  доступности email при email = null")
        void checkEmailNull() {
            CheckEmail checkEmail = new CheckEmail();
            checkEmail.setEmail(null);
            String requestBody = JsonContext.toJson(checkEmail);

            given(requestSpec())
                    .body(requestBody)
                    .when()
                    .post(ConfigProperties.get("endpoint.is_available"))
                    .then()
                    .spec(responseSpec())
                    .statusCode(400)
                    .body(matchesJsonSchemaInClasspath("schemas/errorSchema/400errorSchema.json"));
        }
    }
}

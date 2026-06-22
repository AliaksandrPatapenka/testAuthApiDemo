package com.apiAuto.helpers.authHelper;

import com.apiAuto.base.properties.patch.AuthPatch;
import com.apiAuto.helpers.testHelper.JsonContext;
import com.apiAuto.models.auth.UserLogin;
import io.restassured.response.Response;
import static com.apiAuto.base.Specs.requestSpec;
import static io.restassured.RestAssured.given;

public class UserLoginHelper {
        public static Response userLogin(String userEmail, String userPassword) {
            UserLogin user = new UserLogin();
            user.setEmail(userEmail);
            user.setPassword(userPassword);
            String requestBody = JsonContext.toJson(user);

            return given(requestSpec())
                    .body(requestBody)
                    .when()
                    .post(AuthPatch.ENDPOINT_LOGIN)
                    .then()
                    .extract().response();
        }
    }

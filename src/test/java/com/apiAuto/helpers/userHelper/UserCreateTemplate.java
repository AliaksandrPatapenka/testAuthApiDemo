package com.apiAuto.helpers.userHelper;

import com.apiAuto.base.ConfigProperties;
import com.apiAuto.helpers.testHelper.JsonContext;
import io.restassured.response.Response;

import java.util.Map;

import static com.apiAuto.base.Specs.requestSpec;
import static com.apiAuto.base.Specs.responseSpec;
import static io.restassured.RestAssured.given;

public class UserCreateTemplate {

    public static Response userCreateTemplate() {
        Map<String, Object> jsonRequest = UserJsonTemplate.userJsonTemplate();
        String requestBody = JsonContext.toJson(jsonRequest);

        return given(requestSpec())
                .body(requestBody)
                .when()
                .post(ConfigProperties.get("endpoint.users"))
                .then()
                .spec(responseSpec())
                .extract().response();
    }


}

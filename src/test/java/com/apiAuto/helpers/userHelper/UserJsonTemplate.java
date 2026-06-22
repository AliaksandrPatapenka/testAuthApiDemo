package com.apiAuto.helpers.userHelper;

import com.apiAuto.base.properties.config.UserData;
import com.apiAuto.helpers.testHelper.TestDataGenerator;

import java.util.HashMap;
import java.util.Map;

public class UserJsonTemplate {
    public static Map<String, Object> userJsonTemplate() {
        String timeIndex = TestDataGenerator.timeIndex();

        Map<String, Object> userJsonTemplate = new HashMap<>();
        userJsonTemplate.put("name", TestDataGenerator.generatorName(timeIndex));
        userJsonTemplate.put("email", TestDataGenerator.generatorEmail(timeIndex));
        userJsonTemplate.put("password", TestDataGenerator.randomPassword());
        userJsonTemplate.put("avatar", UserData.IMAGE_URI);

        return userJsonTemplate;
    }
}

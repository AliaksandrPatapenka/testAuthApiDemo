package com.apiAuto.base.properties.patch;


/**
 *Patch Users CRUD
 */
public final class UsersPatch {
    public static final String ENDPOINT_USERS = System.getProperty("endpoint.users", "/users/");
    public static final String ENDPOINT_IS_AVAILABLE= System.getProperty("endpoint.is_available", "/users/is-available/");
}

package com.apiAuto.base.properties.patch;


/**
 * Patch Auth JWT
 */
public final class AuthPatch {
    public static final String ENDPOINT_LOGIN = System.getProperty("endpoint.login", "/auth/login/");
    public static final String ENDPOINT_PROFILE = System.getProperty("endpoint.profile", "/auth/profile/");
    public static final String ENDPOINT_REFRESH_TOKEN = System.getProperty("endpoint.refreshToken", "/auth/refresh-token/");
}

package com.apiAuto.base.properties.config;

public final class TestProperties {
    public static final String BASE_URI = System.getProperty("base.url", "https://api.escuelajs.co");
    public static final String BASE_PATH = System.getProperty("base.path", "/api/v1");


    public static final int HTTP_CONNECTION_TIMEOUT = Integer.parseInt(System.getProperty("http.connection.timeout", "10000"));
    public static final int HTTP_SOCKET_TIMEOUT = Integer.parseInt(System.getProperty("http.socket.timeout", "10000"));
}

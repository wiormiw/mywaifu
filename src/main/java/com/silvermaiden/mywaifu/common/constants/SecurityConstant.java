package com.silvermaiden.mywaifu.common.constants;

import java.util.Set;

public class SecurityConstant {
    //JWT
    public static final long ACCESS_EXPIRATION_TIME = 900_000; // 15 Minutes
    public static final long REFRESH_EXPIRATION_TIME = 604_800_000; // 7 Days
    public static final String AUTH_HEADER_PREFIX = "Bearer ";
    public static final String ISSUER = "SilverMaiden";
    public static final String AUDIENCE = "MyWaifuClient";
    public static final String INVALID_TOKEN_MESSAGE = "Invalid authentication token!";

    //Application Options
    public static final String ACCESS_DENIED_MESSAGE = "You need to log in to access the API!";
    public static final String FORBIDDEN_MESSAGE = "You do not have permission to access this API!";
    public static final String[] PUBLIC_URLS = {
            "/api/auth/**", // Add new URLs after coma (,)
    };
    public static final String[] ALLOWED_ORIGINS = {
            "http://localhost:3000", // Add new origins after coma (,)
    };

    //Authorization
    public static final Set<String> DEFAULT_ROLE = Set.of("ROLE_USER");
}

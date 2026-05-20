package md.cassa.be2048.constants;

import java.util.Set;

public interface Security {
    String AUTH_COOKIE_NAME = "SessionID";

    Set<String> PUBLIC_ENDPOINTS = Set.of(
            "/",
            "/users/login",
            "/users/register",
            "/index.html",
            "/login.html",
            "/register.html",
            "/styles.css",
            "/scripts/",
            "/favicon.ico"
    );
}

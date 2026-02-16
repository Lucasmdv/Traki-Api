package com.traki.trakiapi.config;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor
public final class AppEnvConfig {

    public static void loadEnv() {
        Dotenv dotenv = Dotenv.load();
        System.setProperty("DB_URL", Objects.requireNonNull(dotenv.get("DB_URL")));
        System.setProperty("DB_USERNAME", Objects.requireNonNull(dotenv.get("DB_USER")));
        System.setProperty("DB_PASSWORD", Objects.requireNonNull(dotenv.get("DB_PASSWORD")));
        System.setProperty("JWT.SECRET", Objects.requireNonNull(dotenv.get("JWT_SECRET")));
        System.setProperty("JWT.EXPIRATION_MS", Objects.requireNonNull(dotenv.get("JWT_EXPIRATION")));
    }
}

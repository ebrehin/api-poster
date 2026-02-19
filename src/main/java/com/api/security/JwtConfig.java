package com.api.security;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Locale;
import java.util.Properties;

public final class JwtConfig {
    private final String secret;
    private final Duration expiration;

    private JwtConfig(String secret, Duration expiration) {
        this.secret = secret;
        this.expiration = expiration;
    }

    public String secret() {
        return secret;
    }

    public Duration expiration() {
        return expiration;
    }

    public static JwtConfig load() {
        Properties props = new Properties();
        try (InputStream in = JwtConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de charger application.properties", e);
        }

        String secret = readConfig("security.jwt.secret", props);
        String expirationRaw = readConfig("security.jwt.expiration", props);

        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("security.jwt.secret est obligatoire");
        }
        if (expirationRaw == null || expirationRaw.isBlank()) {
            throw new IllegalStateException("security.jwt.expiration est obligatoire");
        }

        Duration expiration = parseDuration(expirationRaw);
        return new JwtConfig(secret, expiration);
    }

    private static String readConfig(String key, Properties props) {
        String envKey = key.toUpperCase(Locale.ROOT).replace('.', '_');
        String fromEnv = System.getenv(envKey);
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv;
        }
        String fromProp = System.getProperty(key);
        if (fromProp != null && !fromProp.isBlank()) {
            return fromProp;
        }
        String fromFile = props.getProperty(key);
        return (fromFile != null && !fromFile.isBlank()) ? fromFile : null;
    }

    private static Duration parseDuration(String raw) {
        String value = raw.trim().toLowerCase(Locale.ROOT);
        long number;
        try {
            int i = 0;
            while (i < value.length() && Character.isDigit(value.charAt(i))) {
                i++;
            }
            if (i == 0) {
                throw new IllegalArgumentException("Pas de valeur numérique");
            }
            number = Long.parseLong(value.substring(0, i));
            String unit = value.substring(i).trim();
            return switch (unit) {
                case "s", "sec", "secs", "second", "seconds" -> Duration.ofSeconds(number);
                case "m", "min", "mins", "minute", "minutes" -> Duration.ofMinutes(number);
                case "h", "hr", "hrs", "hour", "hours" -> Duration.ofHours(number);
                case "d", "day", "days" -> Duration.ofDays(number);
                case "" -> Duration.ofSeconds(number);
                default -> throw new IllegalArgumentException("Unité inconnue: " + unit);
            };
        } catch (RuntimeException e) {
            throw new IllegalStateException("Format invalide pour security.jwt.expiration: " + raw, e);
        }
    }
}

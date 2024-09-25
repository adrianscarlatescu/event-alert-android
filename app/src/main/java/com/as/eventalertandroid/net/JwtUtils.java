package com.as.eventalertandroid.net;

import com.auth0.android.jwt.JWT;

import java.util.Date;
import java.util.Objects;

public class JwtUtils {

    public static boolean isExpired(String token) {
        JWT tokenJwt = new JWT(token);
        long offset = 60_000;
        long now = new Date().getTime();
        long expTime = Objects.requireNonNull(tokenJwt.getExpiresAt()).getTime();
        return expTime - offset < now;
    }

}

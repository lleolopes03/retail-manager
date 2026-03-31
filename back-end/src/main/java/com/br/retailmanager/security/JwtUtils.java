package com.br.retailmanager.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class JwtUtils {

    private static String SECRET_KEY = "minhaChaveSuperSecretaDeSeguranca1234567890";
    private static final long EXPIRE_MINUTES = 30;
    private static final long REFRESH_EXPIRE_MINUTES = 1440;

    public static void setSecretKey(String secretKey) {
        SECRET_KEY = secretKey;
    }

    public static Key generateKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    public static String createToken(String username, String role, Long userId) {
        Date issuedAt = new Date();
        Date limit = toExpireDate(issuedAt);

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .claim("userId", userId)
                .setIssuedAt(issuedAt)
                .setExpiration(limit)
                .signWith(generateKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public static String createRefreshToken(String username) {
        Date issuedAt = new Date();
        LocalDateTime end = issuedAt.toInstant().atZone(ZoneId.systemDefault())
                .toLocalDateTime().plusMinutes(REFRESH_EXPIRE_MINUTES);
        Date limit = Date.from(end.atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(issuedAt)
                .setExpiration(limit)
                .signWith(generateKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public static String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(generateKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public static Long getUserIdFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(generateKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userId", Long.class);
    }

    public static boolean isTokenValid(String token) {
        if (token == null || token.isBlank()) return false;
        try {
            Jwts.parserBuilder()
                    .setSigningKey(generateKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    private static Date toExpireDate(Date start) {
        LocalDateTime end = start.toInstant().atZone(ZoneId.systemDefault())
                .toLocalDateTime().plusMinutes(EXPIRE_MINUTES);
        return Date.from(end.atZone(ZoneId.systemDefault()).toInstant());
    }
}

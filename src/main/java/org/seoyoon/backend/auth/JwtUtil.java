package org.seoyoon.backend.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.seoyoon.backend.admin_user.RoleType;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
public class JwtUtil {
    private final String SECRET_KEY = "seoyoonSecretKey0608-I-need-long-password-12345"; // application.properties 로 뺄 수 있음
    private final long EXPIRATION_TIME =  24 * 1000 * 60 * 60; // 24시간
//    private final long EXPIRATION_TIME =   1000 * 60 * 1; // 1분
    public static final String BEARER_PREFIX = "Bearer ";

    public String generateToken(String username, Long storeId, String name, RoleType role) {
        return BEARER_PREFIX + Jwts.builder()
                .setSubject(username)
                .claim("storeId", storeId)
                .claim("name", name)
                .claim("role", role)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token, String username) {
        System.out.println("extract username from token"+ extractUsername(token));
        return username.equals(extractUsername(token)) &&
                !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }

    public Long extractStoreId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("storeId", Long.class);
    }
}
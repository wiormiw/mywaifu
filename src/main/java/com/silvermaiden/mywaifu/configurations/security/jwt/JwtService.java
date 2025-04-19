package com.silvermaiden.mywaifu.configurations.security.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.silvermaiden.mywaifu.common.constants.SecurityConstant.ISSUER;
import static com.silvermaiden.mywaifu.common.constants.SecurityConstant.AUDIENCE;
import static com.silvermaiden.mywaifu.common.constants.SecurityConstant.ACCESS_EXPIRATION_TIME;
import static com.silvermaiden.mywaifu.common.constants.SecurityConstant.REFRESH_EXPIRATION_TIME;
import static com.silvermaiden.mywaifu.common.utilities.DateUtil.currentTimeMillis;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public record TokenResponse(String token, long expiresAt) {}

    public TokenResponse generateAccessToken(String username, Set<String> roles) {
        long expiresAt = currentTimeMillis() + ACCESS_EXPIRATION_TIME;
        String token = Jwts.builder()
                .issuer(ISSUER)
                .audience().add(AUDIENCE).and()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(expiresAt))
                .signWith(getSigningKey())
                .compact();
        return new TokenResponse(token, expiresAt);
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .issuer(ISSUER)
                .audience().add(AUDIENCE).and()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(currentTimeMillis() + REFRESH_EXPIRATION_TIME))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("roles", List.class);
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}

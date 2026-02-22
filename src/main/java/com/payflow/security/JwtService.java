package com.payflow.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private final String SECRET_KEY = "ffghjyuhiloghfatr456789808ytdzfghjkhgczdgxfhf57778980";

    public String generateAccessToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .claim("type","ACCESS")
                .setIssuedAt(new Date())
                .setExpiration(
                       new Date(System.currentTimeMillis() + 1000 * 60 * 60) // 1 hour
                        //new Date(System.currentTimeMillis() + 1000 * 5) //5 seconds
                )
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    //refresh token
    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .claim("type","REFRESH")
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7) // 7 days
                )
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }
    public String extractUsername(String token) {
        try {
            return extractAllClaims(token).getSubject();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token expired");
        } catch (JwtException e) {
            throw new RuntimeException("Invalid token");
        }
    }

    public String extractTokenType(String token)
    {
        return extractAllClaims(token).get("type",String.class);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(
                SECRET_KEY.getBytes(StandardCharsets.UTF_8)
        );
    }
}


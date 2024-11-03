package com.grooveconductor.application.auth;

import com.grooveconductor.domain.user.UserDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static io.jsonwebtoken.Claims.*;
import static io.jsonwebtoken.Jwts.builder;
import static io.jsonwebtoken.io.Decoders.BASE64;
import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;

@Service
public class JwtTokenService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public String getEmailFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        try {
            Claims claims = getAllClaimsFromToken(token);

            return claimsResolver.apply(claims);
        } catch (MalformedJwtException e) {
            throw new AuthenticationException("Invalid JWT token");
        }
    }

    public boolean validateToken(String token, UserDTO user) {
        String email = getEmailFromToken(token);

        return (email.equals(user.email()) && !isTokenExpired(token));
    }

    public String generateToken(UserDTO user) {
        Map<String, Object> claims = new HashMap<>();

        return doGenerateToken(claims, user.email());
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);

        return expiration.before(new Date());
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        int hourInMillis = 1000 * 60 * 60;
        long currentTimeMillis = System.currentTimeMillis();

        return builder()
                .claims()
                .add(claims)
                .add(SUBJECT, subject)
                .add(ISSUED_AT, currentTimeMillis)
                .add(EXPIRATION, currentTimeMillis + hourInMillis)
                .and()
                .signWith(getSigningKey())
                .compact();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = BASE64.decode(jwtSecret);

        return hmacShaKeyFor(keyBytes);
    }


}

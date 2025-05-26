package com.grooveconductor.infrastructure.auth;

import com.grooveconductor.domain.auth.AuthenticationTokenException;
import com.grooveconductor.domain.auth.TokenRepository;
import com.grooveconductor.domain.user.UserDTO;

import java.net.URI;
import java.security.PublicKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;

import static io.jsonwebtoken.Claims.*;
import static io.jsonwebtoken.Jwts.builder;
import static io.jsonwebtoken.io.Decoders.BASE64;
import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;

@Repository
public class JwtTokenRepository implements TokenRepository {

    private static final String GOOGLE_JWK_URL = "https://www.googleapis.com/oauth2/v3/certs";

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    public String getSubjectFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    @Override
    public String getSubjectFromGoogleToken(String token) {
        return getClaimFromGoogleToken(token, Claims::getSubject);
    }

    @Override
    public String generateToken(UserDTO user) {
        Map<String, Object> claims = new HashMap<>();

        return doGenerateToken(claims, user.email());
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        try {
            Claims claims = getAllClaimsFromToken(token);

            return claimsResolver.apply(claims);
        } catch (MalformedJwtException ignored) {
            throw new AuthenticationTokenException("Malformed provided externalToken");
        }
    }

    private <T> T getClaimFromGoogleToken(String token, Function<Claims, T> claimsResolver) {
        try {
            Claims claims = getAllClaimsFromGoogleToken(token);

            return claimsResolver.apply(claims);
        } catch (Exception ignored) {
            throw new AuthenticationTokenException("Malformed provided unsecure externalToken");
        }
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Claims getAllClaimsFromGoogleToken(String token) throws Exception {
        Map<String, Object> header = getHeader(token);
        String kid = (String) header.getOrDefault("kid", null);
        JWKSet jwkSet = JWKSet.load(new URI(GOOGLE_JWK_URL).toURL());
        JWK jwk = jwkSet.getKeyByKeyId(kid);

        if (jwk == null)
            throw new IllegalArgumentException("Public key not found for kid: " + kid);
        PublicKey publicKey = jwk.toRSAKey().toPublicKey();

        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
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

    public Map<String, Object> getHeader(String token) throws JsonProcessingException {
        String[] parts = token.split("\\.");
        if (parts.length != 3)
            throw new IllegalArgumentException("Invalid JWT externalToken");
        ObjectMapper mapper = new ObjectMapper();
        Base64.Decoder base64Decoder = Base64.getUrlDecoder();
        String headerJson = new String(base64Decoder.decode(parts[0]));

        return mapper.readValue(headerJson, Map.class);
    }

}

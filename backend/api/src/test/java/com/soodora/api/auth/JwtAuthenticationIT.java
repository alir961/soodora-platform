package com.soodora.api.auth;

import java.util.Base64;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Invalid tokens must be rejected like a missing token, never crash the
 * request.
 */
@SpringBootTest
@AutoConfigureMockMvc
class JwtAuthenticationIT {

    @Autowired
    private MockMvc mockMvc;

    @Value("${jwt.secret}")
    private String jwtSecret;

    private String token(byte[] key, Date expiration) {
        return Jwts.builder()
                .subject("artist@soodora.test")
                .issuedAt(new Date(expiration.getTime() - 3_600_000))
                .expiration(expiration)
                .signWith(Keys.hmacShaKeyFor(key))
                .compact();
    }

    @Test
    void givenGarbageToken_whenGetProfile_thenForbidden() throws Exception {
        mockMvc.perform(get("/profile").header("Authorization", "Bearer not-a-jwt"))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenTokenSignedWithAnotherKey_whenGetProfile_thenForbidden() throws Exception {
        String forged = token(new byte[64], new Date(System.currentTimeMillis() + 3_600_000));

        mockMvc.perform(get("/profile").header("Authorization", "Bearer " + forged))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenExpiredToken_whenGetProfile_thenForbidden() throws Exception {
        String expired = token(Base64.getDecoder().decode(jwtSecret), new Date(System.currentTimeMillis() - 60_000));

        mockMvc.perform(get("/profile").header("Authorization", "Bearer " + expired))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenExpiredToken_whenGetPublicEndpoint_thenStillServed() throws Exception {
        String expired = token(Base64.getDecoder().decode(jwtSecret), new Date(System.currentTimeMillis() - 60_000));

        mockMvc.perform(get("/status").header("Authorization", "Bearer " + expired))
                .andExpect(status().isOk());
    }
}

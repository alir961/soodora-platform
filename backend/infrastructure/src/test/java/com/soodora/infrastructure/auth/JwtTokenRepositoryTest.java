package com.soodora.infrastructure.auth;

import com.soodora.domain.user.User;

import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtTokenRepositoryTest {

    private static final String SECRET = Base64.getEncoder().encodeToString(new byte[64]);
    private static final String EMAIL = "artist@soodora.test";

    private JwtTokenRepository repository;
    private User user;

    @BeforeEach
    void setUp() {
        repository = new JwtTokenRepository();
        ReflectionTestUtils.setField(repository, "jwtSecret", SECRET);
        user = mock(User.class, RETURNS_DEEP_STUBS);
        when(user.getEmail().value()).thenReturn(EMAIL);
    }

    private Claims parse(String token) {
        SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET));
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    @Test
    void tokenCarriesTheUserEmailAsSubject() {
        assertEquals(EMAIL, repository.getSubjectFromToken(repository.generateToken(user)));
    }

    @Test
    void tokenExpiresAfterOneHour() {
        Claims claims = parse(repository.generateToken(user));
        Instant expected = Instant.now().plus(Duration.ofHours(1));

        assertTrue(Duration.between(claims.getExpiration().toInstant(), expected).abs().toMinutes() < 2,
                "expiration should be ~1 hour from now but was " + claims.getExpiration());
        assertTrue(Duration.between(claims.getIssuedAt().toInstant(), Instant.now()).abs().toMinutes() < 2,
                "issued-at should be now but was " + claims.getIssuedAt());
    }
}

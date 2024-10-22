package com.grooveconductor.infrastructure.oauth;

import com.grooveconductor.infrastructure.InfrastructureTestConfiguration;
import com.grooveconductor.infrastructure.user.UserEntity;
import com.grooveconductor.infrastructure.user.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.grooveconductor.domain.oauth.OAuthProvider.GOOGLE;
import static com.grooveconductor.infrastructure.oauth.OAuthTokenEntityFactory.buildDefaultOAuthTokenEntity;
import static com.grooveconductor.infrastructure.user.UserEntityFactory.buildDefaultUserEntity;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = InfrastructureTestConfiguration.class)
class OAuthTokenRepositoryTest {

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private OAuthTokenJpaRepository oauthTokenRepository;

    private UserEntity userEntity;
    private OAuthTokenEntity oauthTokenEntity;

    @BeforeEach
    void setUp() {
        userEntity = buildDefaultUserEntity();
        userEntity = userRepository.save(userEntity);
        oauthTokenEntity = buildDefaultOAuthTokenEntity(userEntity);
        oauthTokenRepository.save(oauthTokenEntity);
    }

    @Test
    void createAuthorization_Success() {
        // GIVEN
        OAuthTokenEntity newOAuthToken = buildDefaultOAuthTokenEntity(userEntity);

        newOAuthToken.setRefreshToken("new-token");

        // WHEN
        OAuthTokenEntity savedOAuthToken = oauthTokenRepository.save(newOAuthToken);

        // THEN
        assertNotNull(savedOAuthToken.getId());
        assertEquals(GOOGLE, savedOAuthToken.getProvider());
    }

    @Test
    void findByProviderAndUserId_Success() {
        // WHEN
        OAuthTokenEntity entity = oauthTokenRepository.findByProviderAndUserId(GOOGLE, userEntity.getId())
            .orElse(null);

        // THEN
        assertNotNull(entity);
        assertEquals(GOOGLE, entity.getProvider());
        assertEquals(userEntity.getId(), entity.getUser().getId());
        assertEquals("refresh-token", entity.getRefreshToken());
    }

}
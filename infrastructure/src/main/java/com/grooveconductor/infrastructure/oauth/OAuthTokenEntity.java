package com.grooveconductor.infrastructure.oauth;

import com.grooveconductor.domain.oauth.OAuthProvider;
import com.grooveconductor.infrastructure.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

import static java.util.UUID.randomUUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "oauth_tokens")
public class OAuthTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id = randomUUID();

    private OAuthProvider provider;

    private String refreshToken;

    @JoinColumn
    @OneToOne
    private UserEntity user;

}

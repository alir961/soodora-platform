package com.grooveconductor.service.oauth;

import com.grooveconductor.domain.oauth.RefreshableAuthorization;
import org.springframework.web.client.HttpClientErrorException;

import java.net.URI;

public interface OAuthFacade {

    URI getOAuthAuthorizationFlowURI(String state);

    RefreshableAuthorization exchangeOAuthToken(String code) throws HttpClientErrorException;

    RefreshableAuthorization refreshOAuthToken(String refreshToken) throws HttpClientErrorException;

}

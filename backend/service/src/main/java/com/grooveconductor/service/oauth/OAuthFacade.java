package com.grooveconductor.service.oauth;

import com.grooveconductor.domain.oauth.RefreshableAuthorization;

import java.net.URI;

import org.springframework.web.client.HttpClientErrorException;

public interface OAuthFacade {

    URI getOAuthAuthorizationFlowURI(String state);

    RefreshableAuthorization exchangeOAuthToken(String code) throws HttpClientErrorException;

    RefreshableAuthorization refreshOAuthToken(String refreshToken) throws HttpClientErrorException;

}

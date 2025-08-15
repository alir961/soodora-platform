package com.soodora.service.oauth;

import com.soodora.domain.oauth.RefreshableAuthorization;

import java.net.URI;

import org.springframework.web.client.HttpClientErrorException;

public interface OAuthFacade {

    URI getOAuthAuthorizationFlowURI(String state);

    RefreshableAuthorization exchangeOAuthToken(String code) throws HttpClientErrorException;

    RefreshableAuthorization refreshOAuthToken(String refreshToken) throws HttpClientErrorException;

}

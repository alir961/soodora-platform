package com.grooveconductor.domain.oauth;

public interface AuthorizationMapper {

    String registerAuthorization(RefreshableAuthorization authorization);

    RefreshableAuthorization retrieveAuthorization(String external);

}

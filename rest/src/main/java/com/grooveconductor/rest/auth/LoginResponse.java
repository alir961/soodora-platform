package com.grooveconductor.rest.auth;

import com.grooveconductor.rest.common.JsonResponse;

public record LoginResponse(String token) implements JsonResponse {

}

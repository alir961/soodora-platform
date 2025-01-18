package com.grooveconductor.api.auth;

import com.grooveconductor.api.common.JsonResponse;

public record LoginResponse(String token) implements JsonResponse {

}

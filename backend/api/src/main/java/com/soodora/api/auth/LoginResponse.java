package com.soodora.api.auth;

import com.soodora.api.common.JsonResponse;

public record LoginResponse(String token) implements JsonResponse {

}

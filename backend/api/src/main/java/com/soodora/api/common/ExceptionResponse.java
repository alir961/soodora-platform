package com.soodora.api.common;

public record ExceptionResponse(String reason) implements JsonResponse {
}

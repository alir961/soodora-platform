package com.grooveconductor.rest.common;

public record ExceptionResponse(String reason) implements JsonResponse {
}

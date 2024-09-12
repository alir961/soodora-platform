package com.grooveconductor.rest.response;

public record ExceptionResponse(String reason) implements JsonResponse {
}

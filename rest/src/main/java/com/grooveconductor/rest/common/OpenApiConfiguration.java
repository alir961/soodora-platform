package com.grooveconductor.rest.common;

import io.swagger.v3.oas.annotations.security.SecurityScheme;

import static io.swagger.v3.oas.annotations.enums.SecuritySchemeIn.HEADER;
import static io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP;

// Only used for Swagger UI generation
@SuppressWarnings("unused")
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth",
        scheme = "bearer",
        type = HTTP,
        bearerFormat = "JWT",
        in = HEADER
)
public class OpenApiConfiguration {
}

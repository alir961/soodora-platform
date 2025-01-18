package com.grooveconductor.api.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

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
@OpenAPIDefinition(
    info = @Info(
        title = "GrooveConductor API",
        description = "Epitech 2026 EIP Project"
    ),
    servers = @Server(
        url ="http://localhost:8080",
        description = "Localhost development server"
    )
)
public class OpenApiConfiguration {
}

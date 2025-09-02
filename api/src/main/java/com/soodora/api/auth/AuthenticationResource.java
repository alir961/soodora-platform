package com.soodora.api.auth;

import com.soodora.api.common.ExceptionResponse;
import com.soodora.api.common.JsonResponse;
import com.soodora.service.auth.LoginRequest;
import com.soodora.service.auth.RegisterRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

public interface AuthenticationResource {

    @Operation(summary = "Register a user account", responses = {
            @ApiResponse(responseCode = "201", description = "User registered"),
            @ApiResponse(responseCode = "400", description = "Invalid register request", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
    })
    ResponseEntity<JsonResponse> registerUser(@RequestBody RegisterRequest request);

    @Operation(summary = "Login to user account", responses = {
            @ApiResponse(responseCode = "200", description = "Login success", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid login request", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    ResponseEntity<JsonResponse> loginUser(@RequestBody LoginRequest request);

}

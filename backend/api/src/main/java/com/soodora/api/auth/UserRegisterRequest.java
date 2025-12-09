package com.soodora.api.auth;

import com.soodora.service.auth.UserRegisterCommand;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.common.Request;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRegisterRequest(
        @Schema(description = "Username of the account", example = "Username", defaultValue = "Username") @NotBlank String username,
        @Schema(description = "Email of the account", example = "user@example.com", defaultValue = "user@example.com") @Email String email,
        @Schema(description = "Password of the account", example = "passwordPassword#!9", defaultValue = "passwordPassword#!9") @NotBlank String password,
        @Schema(description = "Firstname of the account", example = "John", defaultValue = "John") @NotBlank String firstname,
        @Schema(description = "Lastname of account", example = "Doe", defaultValue = "Doe") @NotBlank String lastname,
        @Schema(description = "External OAuth 2.0 token") String external)
        implements
            Request<UserRegisterCommand> {

    @Override
    public UserRegisterCommand toCommand() throws InvalidRequestException {
        return new UserRegisterCommand(
                username.trim(),
                email.trim(),
                password,
                firstname.trim(),
                lastname.trim(),
                external);
    }

}

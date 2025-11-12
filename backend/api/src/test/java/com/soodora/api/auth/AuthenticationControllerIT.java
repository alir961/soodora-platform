package com.soodora.api.auth;

import com.soodora.service.auth.AuthenticationException;
import com.soodora.service.auth.AuthenticationService;
import com.soodora.service.user.UnknownUserException;
import com.soodora.service.user.UserUnityException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.soodora.api.JsonStringify.asJsonString;
import static com.soodora.test.TestConstants.VALID_PASSWORD;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationService authenticationService;

    @Test
    void givenValidRequest_whenRegisterUser_thenOk() throws Exception {
        // GIVEN
        UserRegisterRequest request = new UserRegisterRequest(
                "username",
                "user@example.com",
                VALID_PASSWORD,
                "John",
                "Doe",
                "external");

        doNothing().when(authenticationService).registerUser(any());

        // WHEN & THEN
        mockMvc.perform(post("/auth/register")
                .contentType(APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void givenUsedUsernameOrEmail_whenRegisterUser_thenBadRequest() throws Exception {
        // GIVEN
        UserRegisterRequest request = new UserRegisterRequest(
                "username",
                "user@example.com",
                "password",
                "John",
                "Doe",
                "external");

        doThrow(UserUnityException.class).when(authenticationService).registerUser(any());

        // WHEN & THEN
        mockMvc.perform(post("/auth/register")
                .contentType(APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenValidSecret_whenValidUser_thenCreated() throws Exception {
        // GIVEN
        String validSecret = "validSecret";

        doNothing().when(authenticationService).confirmUserRegistration(validSecret);

        // WHEN & THEN
        mockMvc.perform(post("/auth/confirm")
                .param("secret", validSecret))
                .andExpect(status().isCreated());
    }

    @Test
    void givenInvalidSecret_whenValidUser_thenCreated() throws Exception {
        // GIVEN
        String invalidSecret = "invalidSecret";

        doThrow(AuthenticationException.class).when(authenticationService).confirmUserRegistration(invalidSecret);

        // WHEN & THEN
        mockMvc.perform(post("/auth/confirm")
                .param("secret", invalidSecret))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenValidCredentials_whenLoginUser_thenOk() throws Exception {
        // GIVEN
        LoginRequest request = new LoginRequest(
                "user@example.com",
                "password");

        when(authenticationService.loginUser(any())).thenReturn("externalToken");

        // WHEN & THEN
        mockMvc.perform(post("/auth/login")
                .contentType(APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("externalToken"));
    }

    @Test
    void givenInvalidCredentials_whenLoginUser_thenBadRequest() throws Exception {
        // GIVEN
        LoginRequest request = new LoginRequest(
                "user@example.com",
                "password");

        when(authenticationService.loginUser(any())).thenThrow(UnknownUserException.class);

        // WHEN & THEN
        mockMvc.perform(post("/auth/login")
                .contentType(APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

}

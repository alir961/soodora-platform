package com.soodora.api.auth;

import com.soodora.service.auth.AuthenticationService;
import com.soodora.service.auth.LoginRequest;
import com.soodora.service.auth.RegisterRequest;
import com.soodora.service.user.UnknownUserException;
import com.soodora.service.user.UserUnityException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.soodora.api.JsonStringify.asJsonString;
import static com.soodora.test.TestConstants.USER;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationService authenticationService;

    @Test
    void registerUserSuccess() throws Exception {
        // GIVEN
        RegisterRequest request = new RegisterRequest(
                "username",
                "user@example.com",
                "Password1234!",
                "John",
                "Doe",
                "external");

        when(authenticationService.registerUser(any())).thenReturn(USER);

        // WHEN & THEN
        mockMvc.perform(post("/auth/register")
                .contentType(APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void registerUserAlreadyExists() throws Exception {
        // GIVEN
        RegisterRequest request = new RegisterRequest(
                "username",
                "user@example.com",
                "password",
                "John",
                "Doe",
                "external");

        when(authenticationService.registerUser(any())).thenThrow(UserUnityException.class);

        // WHEN & THEN
        mockMvc.perform(post("/auth/register")
                .contentType(APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginUserSuccess() throws Exception {
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
    void loginUserBadCredentials() throws Exception {
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

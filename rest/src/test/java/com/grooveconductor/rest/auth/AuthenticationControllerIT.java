package com.grooveconductor.rest.auth;

import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.application.user.UnknownUserException;
import com.grooveconductor.application.user.UserUnityException;
import com.grooveconductor.application.auth.RegisterRequest;
import com.grooveconductor.application.auth.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.grooveconductor.rest.JsonStringify.asJsonString;
import static com.grooveconductor.test.UserFactory.buildDefaultUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @Test
    void registerUserSuccess() throws Exception {
        // GIVEN
        RegisterRequest request = new RegisterRequest("username", "user@example.com", "password", "John", "Doe");
        UserDTO user = buildDefaultUser();

        when(authenticationService.registerUser(any())).thenReturn(user);

        // WHEN & THEN
        mockMvc.perform(post("/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void registerUserAlreadyExists() throws Exception {
        // GIVEN
        RegisterRequest request = new RegisterRequest("username", "user@example.com", "password", "John", "Doe");

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
        RegisterRequest request = new RegisterRequest("username", "user@example.com", "password", "John", "Doe");

        when(authenticationService.loginUser(any())).thenReturn("token");

        // WHEN & THEN
        mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token"));
    }

    @Test
    void loginUserBadCredentials() throws Exception {
        // GIVEN
        RegisterRequest request = new RegisterRequest("username", "user@example.com", "password", "John", "Doe");

        when(authenticationService.loginUser(any())).thenThrow(UnknownUserException.class);

        // WHEN & THEN
        mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

}
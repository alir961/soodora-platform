package com.grooveconductor.rest.user;

import com.grooveconductor.domain.user.Identifier;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.application.user.UnknownUserException;
import com.grooveconductor.application.auth.JwtTokenService;
import com.grooveconductor.application.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.grooveconductor.domain.user.IdentifierFactory.createFromSlug;
import static com.grooveconductor.test.UserFactory.buildDefaultUser;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenService jwtTokenService;

    private static final UserDTO USER_1 = buildDefaultUser(1);
    private static final UserDTO USER_2 = buildDefaultUser(2);
    private static final UserDTO USER_3 = buildDefaultUser(3);

    @Test
    void retrieveUsers_Success() throws Exception {
        // GIVEN
        when(userService.retrieveAllUsers()).thenReturn(asList(USER_1, USER_2, USER_3));
        when(jwtTokenService.getEmailFromToken(anyString())).thenReturn("user1@example.com");

        // WHEN & THEN
        mockMvc.perform(get("/users")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].slug").value("slug1"))
                .andExpect(jsonPath("$[1].slug").value("slug2"))
                .andExpect(jsonPath("$[2].slug").value("slug3"));
    }

    @Test
    void retrieveUsers_Empty() throws Exception {
        // GIVEN
        when(userService.retrieveAllUsers()).thenReturn(emptyList());
        when(jwtTokenService.getEmailFromToken(anyString())).thenReturn("user1@example.com");

        // WHEN & THEN
        mockMvc.perform(get("/users")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isNoContent());
    }

    @Test
    void retrieveUsers_Forbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(get("/users")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isForbidden());
    }

    @Test
    void retrieveUserBySlug_Success() throws Exception {
        // GIVEN
        Identifier identifier = createFromSlug("slug1");

        when(userService.retrieveUser(identifier)).thenReturn(USER_1);
        when(jwtTokenService.getEmailFromToken(anyString())).thenReturn("user1@example.com");

        // WHEN & THEN
        mockMvc.perform(get("/users/slug1")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("slug1"))
                .andExpect(jsonPath("$.username").value("username1"))
                .andExpect(jsonPath("$.email").value("user1@example.com"));
    }

    @Test
    void retrieveUserBySlug_Unknown() throws Exception {
        // GIVEN
        when(jwtTokenService.getEmailFromToken(anyString())).thenReturn("user1@example.com");
        doThrow(UnknownUserException.class).when(userService).retrieveUser(any());

        // WHEN & THEN
        mockMvc.perform(get("/users/unknown")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isNotFound());
    }

    @Test
    void retrieveUserBySlug_Forbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(get("/users/slug1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteUserBySlug_Success() throws Exception {
        // GIVEN
        Identifier identifier = createFromSlug("slug1");

        when(jwtTokenService.getEmailFromToken(anyString())).thenReturn("user1@example.com");
        doNothing().when(userService).deleteUser(identifier);

        // WHEN & THEN
        mockMvc.perform(delete("/users/slug1")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
        verify(userService, times(1)).deleteUser(identifier);
    }

    @Test
    void deleteUserBySlug_Unknown() throws Exception {
        // GIVEN
        when(jwtTokenService.getEmailFromToken(anyString())).thenReturn("user1@example.com");
        doThrow(UnknownUserException.class).when(userService).deleteUser(any());

        // WHEN & THEN
        mockMvc.perform(delete("/users/unknown")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUserBySlug_Forbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/users/slug1"))
                .andExpect(status().isForbidden());
    }

}
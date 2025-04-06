package com.grooveconductor.api.user;

import com.grooveconductor.domain.user.Identifier;
import com.grooveconductor.domain.user.UpdateUserCommand;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.domain.user.UserRepository;
import com.grooveconductor.service.auth.UserPrincipal;
import com.grooveconductor.service.common.InvalidRequestException;
import com.grooveconductor.service.user.UnknownUserException;
import com.grooveconductor.service.user.UpdateUserRequest;
import com.grooveconductor.service.user.UserService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.grooveconductor.api.JsonStringify.asJsonString;
import static com.grooveconductor.domain.user.IdentifierFactory.createFromEmail;
import static com.grooveconductor.domain.user.IdentifierFactory.createFromSlug;
import static com.grooveconductor.test.TestConstants.USER;
import static com.grooveconductor.test.factory.UserFactory.buildDefaultUser;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    private static final UserDTO USER_1 = buildDefaultUser(1);
    private static final UserDTO USER_2 = buildDefaultUser(2);
    private static final UserDTO USER_3 = buildDefaultUser(3);

    @Test
    void retrieveUsers_Success() throws Exception {
        // GIVEN
        when(userService.retrieveAllUsers()).thenReturn(asList(USER_1, USER_2, USER_3));
        when(userRepository.find(createFromEmail(USER.email()))).thenReturn(USER);

        // WHEN & THEN
        mockMvc.perform(get("/users")
                .with(user(new UserPrincipal(USER))))
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
        when(userRepository.find(createFromEmail(USER.email()))).thenReturn(USER);

        // WHEN & THEN
        mockMvc.perform(get("/users")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isNoContent());
    }

    @Test
    void retrieveUsers_Forbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    void retrieveUserBySlug_Success() throws Exception {
        // GIVEN
        Identifier identifier = createFromSlug("slug1");

        when(userService.retrieveUser(identifier)).thenReturn(USER_1);
        when(userRepository.find(createFromEmail(USER.email()))).thenReturn(USER);

        // WHEN & THEN
        mockMvc.perform(get("/users/slug1")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("slug1"))
                .andExpect(jsonPath("$.username").value("username1"))
                .andExpect(jsonPath("$.email").value("user1@example.com"));
    }

    @Test
    void retrieveUserBySlug_Unknown() throws Exception {
        // GIVEN
        when(userRepository.find(createFromEmail(USER.email()))).thenReturn(USER);
        doThrow(UnknownUserException.class)
                .when(userService).retrieveUser(createFromSlug("unknown"));

        // WHEN & THEN
        mockMvc.perform(get("/users/unknown")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isNotFound());
    }

    @Test
    void retrieveUserBySlug_Forbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(get("/users/slug1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateUser_Success() throws Exception {
        // GIVEN
        UpdateUserRequest updateUserRequest = new UpdateUserRequest(
                "newUsername",
                "firstname",
                "lastname");

        when(userRepository.find(createFromEmail(USER.email()))).thenReturn(USER);

        // WHEN & THEN
        mockMvc.perform(put("/users/" + USER_1.slug())
                .contentType("application/json")
                .content(asJsonString(updateUserRequest))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isAccepted());
    }

    @Test
    void updateUser_InvalidRequest() throws Exception {
        // GIVEN
        UpdateUserRequest updateUserRequest = new UpdateUserRequest("", "", "");

        when(userRepository.find(createFromEmail(USER.email()))).thenReturn(USER);
        doThrow(new InvalidRequestException("Invalid Request")).when(userService)
                .updateUser(any(UpdateUserCommand.class), eq(USER_1.slug()));

        // WHEN & THEN
        mockMvc.perform(put("/users/" + USER_1.slug())
                .contentType("application/json")
                .content(asJsonString(updateUserRequest))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason").value("Invalid Request"));
    }

    @Test
    void deleteUserBySlug_Success() throws Exception {
        // GIVEN
        Identifier identifier = createFromSlug("slug1");

        when(userRepository.find(createFromEmail(USER.email()))).thenReturn(USER);
        doNothing().when(userService).deleteUser(identifier);

        // WHEN & THEN
        mockMvc.perform(delete("/users/slug1")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
        verify(userService).deleteUser(identifier);
    }

    @Test
    void deleteUserBySlug_Unknown() throws Exception {
        // GIVEN
        when(userRepository.find(createFromEmail(USER.email()))).thenReturn(USER);
        doThrow(UnknownUserException.class).when(userService).deleteUser(any());

        // WHEN & THEN
        mockMvc.perform(delete("/users/unknown")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUserBySlug_Forbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/users/slug1"))
                .andExpect(status().isForbidden());
    }

}

package com.soodora.api.user;

import com.soodora.domain.user.Identifier;
import com.soodora.domain.user.UpdateUserCommand;
import com.soodora.domain.user.UserDTO;
import com.soodora.service.auth.UserPrincipal;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.user.UnknownUserException;
import com.soodora.service.user.UpdateUserRequest;
import com.soodora.service.user.UserService;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.soodora.api.JsonStringify.asJsonString;
import static com.soodora.domain.user.IdentifierFactory.createFromSlug;
import static com.soodora.test.TestConstants.ADMIN;
import static com.soodora.test.TestConstants.USER;
import static com.soodora.test.factory.UserFactory.buildDefaultUser;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private static final UserDTO USER_1 = buildDefaultUser(1);
    private static final UserDTO USER_2 = buildDefaultUser(2);
    private static final UserDTO USER_3 = buildDefaultUser(3);

    @Test
    void givenAuthenticatedAdmin_whenRetrieveUsers_thenSuccess() throws Exception {
        // GIVEN
        when(userService.retrieveAllUsers()).thenReturn(asList(USER_1, USER_2, USER_3));

        // WHEN & THEN
        mockMvc.perform(get("/users")
                .with(user(new UserPrincipal(ADMIN))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].slug").value("slug1"))
                .andExpect(jsonPath("$[1].slug").value("slug2"))
                .andExpect(jsonPath("$[2].slug").value("slug3"));
    }

    @Test
    void givenAuthenticatedUser_whenRetrieveUsers_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(get("/users")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenUnauthenticated_whenRetrieveUsers_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenValidSlug_whenRetrieveUserBySlug_thenSuccess() throws Exception {
        // GIVEN
        Identifier identifier = createFromSlug("slug1");

        when(userService.retrieveUser(identifier)).thenReturn(USER_1);

        // WHEN & THEN
        mockMvc.perform(get("/users/slug1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("slug1"))
                .andExpect(jsonPath("$.username").value("username1"))
                .andExpect(jsonPath("$.email").value("user1@example.com"));
    }

    @Test
    void givenInvalidSlug_whenRetrieveUserBySlug_thenNotFound() throws Exception {
        // GIVEN
        Identifier identifier = createFromSlug("invalid");

        doThrow(UnknownUserException.class)
                .when(userService).retrieveUser(identifier);

        // WHEN & THEN
        mockMvc.perform(get("/users/" + identifier.getValue()))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenAuthenticatedUser_whenSearchUsers_thenReturnMatchingUsers() throws Exception {
        // GIVEN
        String username = "username";
        boolean onlyArtists = true;
        Pageable pageable = PageRequest.of(0, 50);
        List<UserDTO> userList = List.of(buildDefaultUser(1), buildDefaultUser(2));
        Page<UserDTO> expectedPage = new PageImpl<>(userList, pageable, userList.size());

        when(userService.searchUsersByUsername(username, onlyArtists, pageable)).thenReturn(expectedPage);

        // WHEN & THEN
        mockMvc.perform(get("/users/search")
                .param("username", username)
                .param("onlyArtists", String.valueOf(onlyArtists))
                .param("page", "0")
                .param("size", "50")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].username", containsInAnyOrder("username1", "username2")));
    }

    @Test
    void givenAuthenticatedAdmin_whenUpdateUser_thenSuccess() throws Exception {
        // GIVEN
        UpdateUserRequest updateUserRequest = new UpdateUserRequest(
                "newUsername",
                "firstname",
                "lastname",
                "https//soodora.com/assets/avatar.png");

        // WHEN & THEN
        mockMvc.perform(put("/users/" + USER_1.slug())
                .contentType("application/json")
                .content(asJsonString(updateUserRequest))
                .with(user(new UserPrincipal(ADMIN))))
                .andExpect(status().isOk());
    }

    @Test
    void givenInvalidRequest_whenUpdateUser_thenBadRequest() throws Exception {
        // GIVEN
        UpdateUserRequest updateUserRequest = new UpdateUserRequest("", "", "", "");

        doThrow(new InvalidRequestException("Invalid Request")).when(userService)
                .updateUser(any(UpdateUserCommand.class), eq(USER_1.slug()));

        // WHEN & THEN
        mockMvc.perform(put("/users/" + USER_1.slug())
                .contentType("application/json")
                .content(asJsonString(updateUserRequest))
                .with(user(new UserPrincipal(ADMIN))))
                .andExpect(jsonPath("$.reason").value("Invalid Request"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenAuthenticatedUser_whenUpdateUser_thenForbidden() throws Exception {
        // GIVEN
        UpdateUserRequest updateUserRequest = new UpdateUserRequest(
                "newUsername",
                "firstname",
                "lastname",
                "https//soodora.com/assets/avatar.png");

        // WHEN & THEN
        mockMvc.perform(put("/users/slug1")
                .contentType("application/json")
                .content(asJsonString(updateUserRequest))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenUnauthenticated_whenUpdateUser_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(put("/users/slug1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenAuthenticatedAdmin_whenDeleteUserBySlug_thenSuccess() throws Exception {
        // GIVEN
        Identifier identifier = createFromSlug("slug1");

        doNothing().when(userService).deleteUser(identifier);

        // WHEN & THEN
        mockMvc.perform(delete("/users/slug1")
                .with(user(new UserPrincipal(ADMIN))))
                .andExpect(status().isOk());
        verify(userService).deleteUser(identifier);
    }

    @Test
    void givenAuthenticatedAdmin_whenDeleteUserBySlug_thenNoContent() throws Exception {
        // GIVEN
        doThrow(UnknownUserException.class).when(userService).deleteUser(any());

        // WHEN & THEN
        mockMvc.perform(delete("/users/unknown")
                .with(user(new UserPrincipal(ADMIN))))
                .andExpect(status().isNoContent());
    }

    @Test
    void givenAuthenticatedUser_whenDeleteUserBySlug_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/users/slug1")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenUnauthenticated_whenDeleteUserBySlug_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/users/slug1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenAuthenticatedUser_whenRetrieveFollowers_thenSuccess() throws Exception {
        // GIVEN
        Set<UserDTO> followers = Set.of(buildDefaultUser(0), buildDefaultUser(1), buildDefaultUser(2));

        when(userService.retrieveFollowers(USER_1.identifier())).thenReturn(followers);

        // WHEN & THEN
        mockMvc.perform(get("/users/" + USER_1.slug() + "/followers")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].slug", containsInAnyOrder("slug0", "slug1", "slug2")));
    }

    @Test
    void givenUnauthorizedRequest_whenRetrieveFollowers_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(post("/users/slug/followers"))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenAuthenticatedUser_whenRetrieveFollowing_thenSuccess() throws Exception {
        // GIVEN
        Set<UserDTO> following = Set.of(buildDefaultUser(0), buildDefaultUser(1), buildDefaultUser(2));

        when(userService.retrieveFollowing(USER_1.identifier())).thenReturn(following);

        // WHEN & THEN
        mockMvc.perform(get("/users/" + USER_1.slug() + "/following")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].slug", containsInAnyOrder("slug0", "slug1", "slug2")));
    }

    @Test
    void givenUnauthorizedRequest_whenRetrieveFollowing_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(post("/users/slug/following"))
                .andExpect(status().isForbidden());
    }

}

package com.soodora.api.user;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.common.InvalidValueException;
import com.soodora.domain.user.User;
import com.soodora.domain.user.value.Identifier;
import com.soodora.service.auth.UserPrincipal;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.user.UnknownUserException;
import com.soodora.service.user.UserService;
import com.soodora.service.user.UserUpdateCommand;

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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static java.util.Collections.emptySet;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.soodora.api.JsonStringify.asJsonString;
import static com.soodora.domain.user.value.Identifier.Factory.createFromSlug;
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

    private static final User USER_1 = buildDefaultUser(1);
    private static final User USER_2 = buildDefaultUser(2);
    private static final User USER_3 = buildDefaultUser(3);

    @Test
    void givenAuthenticatedAdmin_whenRetrieveUsers_thenSuccess() throws Exception {
        // GIVEN
        when(userService.retrieveAllUsers()).thenReturn(Set.of(USER_1, USER_2, USER_3));

        // WHEN & THEN
        mockMvc.perform(get("/users")
                .with(user(new UserPrincipal(ADMIN))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].slug", containsInAnyOrder("slug1", "slug2", "slug3")));
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
                .andExpect(jsonPath("$.username").value("username1"));
    }

    @Test
    void givenInvalidSlug_whenRetrieveUserBySlug_thenNotFound() throws Exception {
        // GIVEN
        Identifier identifier = createFromSlug("invalid");

        doThrow(UnknownUserException.class)
                .when(userService).retrieveUser(identifier);

        // WHEN & THEN
        mockMvc.perform(get("/users/" + identifier.value()))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenAuthenticatedUser_whenSearchUsers_thenReturnMatchingUsers() throws Exception {
        // GIVEN
        String username = "username";
        boolean onlyArtists = true;
        Pageable pageable = PageRequest.of(0, 50);
        List<User> userList = List.of(buildDefaultUser(1), buildDefaultUser(2));
        Page<User> expectedPage = new PageImpl<>(userList, pageable, userList.size());

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
                "description",
                "avatar");

        MockMultipartFile jsonPart = new MockMultipartFile(
                "data",
                "",
                "application/json",
                asJsonString(updateUserRequest).getBytes());

        MockMultipartFile filePart = new MockMultipartFile(
                "file",
                "profile.png",
                "image/png",
                "fake-image".getBytes());

        // WHEN & THEN
        mockMvc.perform(multipart("/users/" + USER_1.getSlug())
                .file(jsonPart)
                .file(filePart)
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                })
                .with(user(new UserPrincipal(ADMIN))))
                .andExpect(status().isOk());
    }

    @Test
    void givenInvalidRequest_whenUpdateUser_thenBadRequest() throws Exception {
        // GIVEN
        UpdateUserRequest updateUserRequest = new UpdateUserRequest("", "", "", "", "");
        MockMultipartFile jsonPart = new MockMultipartFile(
                "data",
                "",
                "application/json",
                asJsonString(updateUserRequest).getBytes());

        MockMultipartFile filePart = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                "dummy image content".getBytes());

        FileData fileData = new FileData(filePart.getBytes(), filePart.getOriginalFilename(),
                filePart.getContentType());

        doThrow(new InvalidValueException("Invalid Request")).when(userService)
                .updateUser(any(Identifier.class), any(UserUpdateCommand.class));

        // WHEN & THEN
        mockMvc.perform(multipart("/users/" + USER_1.getSlug())
                .file(jsonPart)
                .file(filePart)
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                })
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
                "description",
                "avatar");

        MockMultipartFile jsonPart = new MockMultipartFile(
                "data",
                "",
                "application/json",
                asJsonString(updateUserRequest).getBytes());

        // WHEN & THEN
        mockMvc.perform(multipart("/users/slug1")
                .file(jsonPart)
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                })
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
    void givenUnknownRole_whenUpdateUserRoles_thenBadRequest() throws Exception {
        // GIVEN
        Identifier identifier = createFromSlug("slug1");

        doThrow(InvalidRequestException.class).when(userService).updateUserRoles(identifier, emptySet());

        // WHEN & THEN
        mockMvc.perform(put("/users/slug1/roles")
                .contentType("application/json")
                .content("[]")
                .with(user(new UserPrincipal(ADMIN))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenAuthenticatedUser_whenUpdateUserRoles_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(put("/users/slug1/roles")
                .contentType("application/json")
                .content("[]")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenUnauthenticated_whenUpdateUserRoles_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(put("/users/slug1/roles"))
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
        Set<User> followers = Set.of(buildDefaultUser(0), buildDefaultUser(1), buildDefaultUser(2));

        when(userService.retrieveFollowers(any(Identifier.class))).thenReturn(followers);

        // WHEN & THEN
        mockMvc.perform(get("/users/" + USER_1.getSlug() + "/followers")
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
        Set<User> following = Set.of(buildDefaultUser(0), buildDefaultUser(1), buildDefaultUser(2));

        when(userService.retrieveFollowing(any(Identifier.class))).thenReturn(following);

        // WHEN & THEN
        mockMvc.perform(get("/users/" + USER_1.getSlug() + "/following")
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

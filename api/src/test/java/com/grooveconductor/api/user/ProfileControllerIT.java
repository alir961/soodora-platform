package com.grooveconductor.api.user;

import com.grooveconductor.domain.user.Identifier;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.service.auth.UserPrincipal;
import com.grooveconductor.service.user.UnknownUserException;
import com.grooveconductor.service.user.UserService;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.inject.Inject;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.grooveconductor.domain.user.IdentifierFactory.createFromSlug;
import static com.grooveconductor.test.TestConstants.USER;
import static com.grooveconductor.test.factory.UserFactory.buildDefaultUser;

@SpringBootTest
@AutoConfigureMockMvc
class ProfileControllerIT {

    @MockBean
    private UserService userService;

    @Inject
    private MockMvc mockMvc;

    @Test
    void givenAuthenticatedRequest_whenGetProfile_thenReturnProfile() throws Exception {
        // GIVEN
        when(userService.retrieveUser(USER.identifier())).thenReturn(USER);

        // WHEN & THEN
        mockMvc.perform(get("/profile")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("slug").value(USER.slug()));
    }

    @Test
    void givenUnauthenticatedRequest_whenGetProfile_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(get("/profile"))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenAuthenticatedRequest_whenDeleteProfileAvatar_thenReturnProfile() throws Exception {
        // GIVEN
        when(userService.retrieveUser(USER.identifier())).thenReturn(USER);

        // WHEN & THEN
        mockMvc.perform(delete("/profile/:avatar")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
    }

    @Test
    void givenUnauthenticatedRequest_whenDeleteProfileAvatar_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(get("/profile/:avatar"))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenValidUserSlug_whenFollowUser_thenSuccess() throws Exception {
        // GIVEN
        Identifier followingSlug = createFromSlug("followingSlug");

        // WHEN
        mockMvc.perform(post("/profile/followers/" + followingSlug.getValue())
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());

        // THEN
        verify(userService, times(1)).follow(USER.identifier(), followingSlug);
    }

    @Test
    void givenInvalidUserSlug_whenFollowUser_thenBadRequest() throws Exception {
        // GIVEN
        Identifier invalidSlug = createFromSlug("invalidSlug");

        doThrow(UnknownUserException.class).when(userService).follow(USER.identifier(), invalidSlug);

        // WHEN & THEN
        mockMvc.perform(post("/profile/followers/" + invalidSlug.getValue())
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenUnauthorizedRequest_whenFollowUser_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(post("/profile/followers/slug"))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenAuthenticatedUser_whenRetrieveFollowers_thenSuccess() throws Exception {
        // GIVEN
        Set<UserDTO> followers = Set.of(buildDefaultUser(0), buildDefaultUser(1), buildDefaultUser(2));

        when(userService.retrieveFollowers(USER.identifier())).thenReturn(followers);

        // WHEN & THEN
        mockMvc.perform(get("/profile/followers")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].slug", containsInAnyOrder("slug0", "slug1", "slug2")));
    }

    @Test
    void givenUnauthorizedRequest_whenRetrieveFollowers_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(post("/profile/followers"))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenAuthenticatedUser_whenRetrieveFollowing_thenSuccess() throws Exception {
        // GIVEN
        Set<UserDTO> following = Set.of(buildDefaultUser(0), buildDefaultUser(1), buildDefaultUser(2));

        when(userService.retrieveFollowing(USER.identifier())).thenReturn(following);

        // WHEN & THEN
        mockMvc.perform(get("/profile/following")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].slug", containsInAnyOrder("slug0", "slug1", "slug2")));
    }

    @Test
    void givenUnauthorizedRequest_whenRetrieveFollowing_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(post("/profile/following"))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenValidUserSlug_whenUnfollowUser_thenSuccess() throws Exception {
        // GIVEN
        Identifier followingSlug = createFromSlug("followingSlug");

        // WHEN
        mockMvc.perform(delete("/profile/followers/" + followingSlug.getValue())
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());

        // THEN
        verify(userService, times(1)).unfollow(USER.identifier(), followingSlug);
    }

    @Test
    void givenInvalidUserSlug_whenUnfollowUser_thenBadRequest() throws Exception {
        // GIVEN
        Identifier invalidSlug = createFromSlug("invalidSlug");

        doThrow(UnknownUserException.class).when(userService).unfollow(USER.identifier(), invalidSlug);

        // WHEN & THEN
        mockMvc.perform(delete("/profile/followers/" + invalidSlug.getValue())
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenUnauthorizedRequest_whenUnfollowUser_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/profile/followers/slug"))
                .andExpect(status().isForbidden());
    }

}

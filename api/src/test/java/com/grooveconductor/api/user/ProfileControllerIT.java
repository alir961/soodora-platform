package com.grooveconductor.api.user;

import com.grooveconductor.domain.post.PostDTO;
import com.grooveconductor.domain.user.Identifier;
import com.grooveconductor.domain.user.UpdateUserCommand;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.service.auth.UserPrincipal;
import com.grooveconductor.service.common.InvalidRequestException;
import com.grooveconductor.service.post.PostService;
import com.grooveconductor.service.user.UnknownUserException;
import com.grooveconductor.service.user.UpdateUserRequest;
import com.grooveconductor.service.user.UserService;

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

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.grooveconductor.api.JsonStringify.asJsonString;
import static com.grooveconductor.domain.user.IdentifierFactory.createFromSlug;
import static com.grooveconductor.test.TestConstants.USER;
import static com.grooveconductor.test.factory.PostFactory.buildDefaultPost;
import static com.grooveconductor.test.factory.UserFactory.buildDefaultUser;

@SpringBootTest
@AutoConfigureMockMvc
class ProfileControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private PostService postService;

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
    void givenAuthenticatedRequest_whenUpdateUser_thenSuccess() throws Exception {
        // GIVEN
        UpdateUserRequest updateUserRequest = new UpdateUserRequest(
                "newUsername",
                "firstname",
                "lastname",
                "https//soodora.com/assets/avatar.png");

        // WHEN & THEN
        mockMvc.perform(put("/profile")
                .contentType("application/json")
                .content(asJsonString(updateUserRequest))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
    }

    @Test
    void givenInvalidRequest_whenUpdateUser_thenBadRequest() throws Exception {
        // GIVEN
        UpdateUserRequest updateUserRequest = new UpdateUserRequest("", "", "", "");

        doThrow(new InvalidRequestException("Invalid Request")).when(userService)
                .updateUser(any(UpdateUserCommand.class), eq(USER.slug()));

        // WHEN & THEN
        mockMvc.perform(put("/profile")
                .contentType("application/json")
                .content(asJsonString(updateUserRequest))
                .with(user(new UserPrincipal(USER))))
                .andExpect(jsonPath("$.reason").value("Invalid Request"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenUnauthenticatedRequest_whenUpdateUser_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(put("/profile"))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenAuthenticatedRequest_whenDeleteUser_thenSuccess() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/profile")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
        verify(userService).deleteUser(USER.identifier());
    }

    @Test
    void givenUnauthenticatedRequest_whenDeleteUserBySlug_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/users/slug1"))
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

    @Test
    void givenAuthenticatedUser_whenGetFollowingPosts_thenStatusOk() throws Exception {
        // GIVEN
        PostDTO post1 = buildDefaultPost("slug1", "Title 1");
        PostDTO post2 = buildDefaultPost("slug2", "Title 2");
        List<PostDTO> posts = List.of(post1, post2);
        Pageable pageable = PageRequest.of(0, 50);
        Page<PostDTO> pageWithPosts = new PageImpl<>(posts, pageable, posts.size());

        when(postService.retrievePostsFromFollowing(any(Identifier.class), any(Pageable.class)))
                .thenReturn(pageWithPosts);

        // WHEN & THEN
        mockMvc.perform(get("/profile/following/posts")
                .with(user(new UserPrincipal(USER)))
                .param("page", "0")
                .param("size", "50")
                .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].slug").value("slug1"))
                .andExpect(jsonPath("$.content[1].slug").value("slug2"));
    }

    @Test
    void givenAuthenticatedUser_whenSearchProfiles_thenReturnMatchingUsers() throws Exception {
        // GIVEN
        String username = "username";
        boolean onlyArtists = true;
        Pageable pageable = PageRequest.of(0, 50);
        List<UserDTO> userList = List.of(
                buildDefaultUser(1),
                buildDefaultUser(2));
        Page<UserDTO> expectedPage = new PageImpl<>(userList, pageable, userList.size());

        when(userService.searchUsersByUsername(username, onlyArtists, pageable)).thenReturn(expectedPage);

        // WHEN & THEN
        mockMvc.perform(get("/profile/search")
                .param("username", username)
                .param("onlyArtists", String.valueOf(onlyArtists))
                .param("page", "0")
                .param("size", "50")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].username", containsInAnyOrder("username1", "username2")));
    }

}

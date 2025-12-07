package com.soodora.api.post;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.post.*;
import com.soodora.domain.user.PublicUserDTO;
import com.soodora.domain.user.User;
import com.soodora.service.auth.UserPrincipal;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.post.CreatePostRequest;
import com.soodora.service.post.PostEngagementService;
import com.soodora.service.post.PostService;
import com.soodora.service.post.UnknownPostException;
import com.soodora.service.shared.InvalidPermissionException;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.soodora.api.JsonStringify.asJsonString;
import static com.soodora.domain.factory.PostFactory.buildDefaultPost;
import static com.soodora.domain.factory.UserFactory.buildDefaultUser;
import static com.soodora.test.TestConstants.ARTIST_USER;
import static com.soodora.test.TestConstants.USER;

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @MockitoBean
    private PostEngagementService postEngagementService;

    private static final Post POST_1 = buildDefaultPost("postSlug1", "Post 1");
    private static final Post POST_2 = buildDefaultPost("postSlug2", "Post 2");
    private static final Post POST_3 = buildDefaultPost("postSlug3", "Post 3");

    private static final User USER_1 = buildDefaultUser(1);
    private static final User USER_2 = buildDefaultUser(2);

    private static final MockMultipartFile FILE_DATA = new MockMultipartFile(
            "file",
            "test-image.jpg",
            "image/jpeg",
            "dummy image content".getBytes());

    private static final CreatePostRequest VALID_CREATE_REQUEST = new CreatePostRequest(
            "Post 1",
            "Content",
            "My audio",
            "audioUrl");

    private static final MockMultipartFile CREATE_REQUEST_JSON = new MockMultipartFile(
            "data",
            "",
            "application/json",
            asJsonString(VALID_CREATE_REQUEST).getBytes());

    @Test
    void givenValidRequest_whenCreatePost_thenCreated() throws Exception {
        // WHEN & THEN
        mockMvc.perform(multipart("/posts")
                .file(CREATE_REQUEST_JSON)
                .file(FILE_DATA)
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isCreated());
    }

    @Test
    void givenInvalidRequest_whenCreatePost_thenBadRequest() throws Exception {
        // GIVEN
        CreatePostRequest createPostRequest = new CreatePostRequest(
                "",
                "Description",
                "My audio",
                "audioUrl");

        doThrow(new InvalidRequestException("Invalid request")).when(postService)
                .createPost(eq(ARTIST_USER), any(CreatePostCommand.class), any(FileData.class));

        // WHEN & THEN
        mockMvc.perform(post("/posts")
                .contentType("application/json")
                .content(asJsonString(createPostRequest))
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenAuthenticatedUser_whenCreatePost_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(multipart("/posts")
                .file(CREATE_REQUEST_JSON)
                .file(FILE_DATA)
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenUnauthenticatedUser_whenCreatePost_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(multipart("/posts")
                .file(CREATE_REQUEST_JSON)
                .file(FILE_DATA))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenNoFilter_whenSearchPosts_thenOk() throws Exception {
        // GIVEN
        Page<Post> postsPage = new PageImpl<>(List.of(POST_1, POST_2, POST_3));

        when(postService.retrievePosts(any(PostFilter.class), any(Pageable.class))).thenReturn(postsPage);

        // WHEN & THEN
        mockMvc.perform(get("/posts/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[0].slug").value(POST_1.getSlug()))
                .andExpect(jsonPath("$.content[1].slug").value(POST_2.getSlug()))
                .andExpect(jsonPath("$.content[2].slug").value(POST_3.getSlug()));
    }

    @Test
    void givenTitleFilter_whenSearchPosts_thenOk() throws Exception {
        // GIVEN
        Page<Post> filteredPage = new PageImpl<>(List.of(POST_1));

        String userSlug = "slug1";
        boolean hasAudio = false;
        String title = "title1";
        String description = "description1";

        PostFilter postFilter = new PostFilter(
                of(userSlug),
                of(hasAudio),
                of(title),
                empty(),
                of(description));

        when(postService.retrievePosts(eq(postFilter), any(Pageable.class))).thenReturn(filteredPage);

        // WHEN & THEN
        mockMvc.perform(get("/posts/search")
                .param("userSlug", userSlug)
                .param("hasAudio", String.valueOf(hasAudio))
                .param("title", title)
                .param("description", description)
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].slug").value(POST_1.getSlug()));
    }

    @Test
    void givenAudioTitleFilter_whenSearchPosts_thenOk() throws Exception {
        // GIVEN
        Page<Post> filteredPage = new PageImpl<>(List.of(POST_1));

        String userSlug = "slug1";
        boolean hasAudio = true;
        String audioTitle = "audioTitle1";
        String description = "description1";

        PostFilter postFilter = new PostFilter(
                of(userSlug),
                of(hasAudio),
                empty(),
                of(audioTitle),
                of(description));

        when(postService.retrievePosts(eq(postFilter), any(Pageable.class))).thenReturn(filteredPage);

        // WHEN & THEN
        mockMvc.perform(get("/posts/search")
                .param("userSlug", userSlug)
                .param("hasAudio", String.valueOf(hasAudio))
                .param("audioTitle", audioTitle)
                .param("description", description)
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].slug").value(POST_1.getSlug()));
    }

    @Test
    void givenValidSlug_whenGetPost_thenOk() throws Exception {
        // GIVEN
        when(postService.retrievePost("postSlug1")).thenReturn(POST_1);

        // WHEN & THEN
        mockMvc.perform(get("/posts/postSlug1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("postSlug1"));
    }

    @Test
    void givenInvalidSlug_whenGetPost_thenNotFound() throws Exception {
        // GIVEN
        doThrow(new UnknownPostException("Unable to find post"))
                .when(postService).retrievePost("unknown");

        // WHEN & THEN
        mockMvc.perform(get("/posts/unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenValidSlug_whenDeletePost_thenOk() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/posts/postSlug1")
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isOk());
    }

    @Test
    void givenInvalidSlug_whenDeletePost_thenNotFound() throws Exception {
        // GIVEN
        doThrow(new UnknownPostException("Unable to find post"))
                .when(postService).deletePost(ARTIST_USER, "unknown");

        // WHEN & THEN
        mockMvc.perform(delete("/posts/unknown")
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenAuthenticatedUser_whenDeletePost_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/posts/postSlug1")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenUnauthenticatedUser_whenDeletePost_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/posts/postSlug1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenValidSlug_whenLikePost_thenOk() throws Exception {
        // WHEN & THEN
        mockMvc.perform(post("/posts/postSlug1/likes")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
    }

    @Test
    void givenInvalidSlug_whenLikePost_thenNotFound() throws Exception {
        // GIVEN
        doThrow(new UnknownPostException("Unable to find post"))
                .when(postEngagementService).likePost(USER, "unknown");

        // WHEN & THEN
        mockMvc.perform(post("/posts/unknown/likes")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenUnauthenticatedUser_whenLikePost_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(post("/posts/postSlug1/likes"))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenValidSlug_whenUnlikePost_thenOk() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/posts/postSlug1/likes")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
    }

    @Test
    void givenInvalidSlug_whenUnlikePost_thenNotFound() throws Exception {
        // GIVEN
        doThrow(new UnknownPostException("Unable to find post"))
                .when(postEngagementService).unlikePost(USER, "unknown");

        // WHEN & THEN
        mockMvc.perform(delete("/posts/unknown/likes")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenUnauthenticatedUser_whenUnlikePost_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/posts/postSlug1/likes"))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenValidSlug_whenGetLikes_thenOk() throws Exception {
        // GIVEN
        List<LikeDTO> likes = List.of(
                new PostLikeDTO(POST_1.getSlug(), PublicUserDTO.build(USER_1), new Date()),
                new PostLikeDTO(POST_2.getSlug(), PublicUserDTO.build(USER_2), new Date()));

        when(postEngagementService.retrieveLikes("postSlug1"))
                .thenReturn(likes);

        // WHEN & THEN
        mockMvc.perform(get("/posts/postSlug1/likes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*]", containsInAnyOrder(USER_1.getSlug(), USER_2.getSlug())));
    }

    @Test
    void givenInvalidSlug_whenGetLikes_thenNotFound() throws Exception {
        // GIVEN
        when(postEngagementService.retrieveLikes("unknown"))
                .thenThrow(new UnknownPostException("Unable to find post"));

        // WHEN & THEN
        mockMvc.perform(get("/posts/unknown/likes"))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenValidSlug_whenRepostPost_thenOk() throws Exception {
        // WHEN & THEN
        mockMvc.perform(post("/posts/postSlug1/reposts")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
    }

    @Test
    void givenInvalidSlug_whenRepostPost_thenNotFound() throws Exception {
        // GIVEN
        doThrow(new UnknownPostException("Unable to find post"))
                .when(postEngagementService).repostPost(USER, "unknown");

        // WHEN & THEN
        mockMvc.perform(post("/posts/unknown/reposts")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenUnauthenticatedUser_whenRepostPost_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(post("/posts/postSlug1/reposts"))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenValidSlug_whenUnrepostPost_thenOk() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/posts/postSlug1/reposts")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
    }

    @Test
    void givenInvalidSlug_whenUnrepostPost_thenNotFound() throws Exception {
        // GIVEN
        doThrow(new UnknownPostException("Unable to find post"))
                .when(postEngagementService).unrepostPost(USER, "unknown");

        // WHEN & THEN
        mockMvc.perform(delete("/posts/unknown/reposts")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenUnauthenticatedUser_whenUnrepostPost_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/posts/postSlug1/reposts"))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenValidSlug_whenGetReposts_thenOk() throws Exception {
        // GIVEN
        Set<User> reposts = Set.of(USER_1, USER_2);

        when(postEngagementService.retrieveReposts("postSlug1"))
                .thenReturn(reposts);

        // WHEN & THEN
        mockMvc.perform(get("/posts/postSlug1/reposts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*]", containsInAnyOrder(USER_1.getSlug(), USER_2.getSlug())));
    }

    @Test
    void givenInvalidSlug_whenGetReposts_thenNotFound() throws Exception {
        // GIVEN
        when(postEngagementService.retrieveReposts("unknown"))
                .thenThrow(new UnknownPostException("Unable to find post"));

        // WHEN & THEN
        mockMvc.perform(get("/posts/unknown/reposts"))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenAuthenticatedArtist_whenGetGlobalAnalytics_thenOk() throws Exception {
        // GIVEN
        PostAnalytics analytics = new PostAnalytics(1, 2, 3, 0, 1, 2);

        when(postService.computeGlobalAnalytics(ARTIST_USER)).thenReturn(analytics);

        // WHEN & THEN
        mockMvc.perform(get("/posts/analytics")
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likes").value(1))
                .andExpect(jsonPath("$.comments").value(2))
                .andExpect(jsonPath("$.reposts").value(3))
                .andExpect(jsonPath("$.subscribedLikes").value(0))
                .andExpect(jsonPath("$.subscribedComments").value(1))
                .andExpect(jsonPath("$.subscribedReposts").value(2));
    }

    @Test
    void givenAuthenticatedUser_whenGetGlobalAnalytics_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(get("/posts/analytics")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenUnauthenticatedRequest_whenGetGlobalAnalytics_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(get("/posts/analytics"))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenAuthenticatedRequest_whenGetPostAnalytics_thenOk() throws Exception {
        // GIVEN
        String postSlug = "post-slug";
        PostAnalytics analytics = new PostAnalytics(1, 2, 3, 0, 1, 2);

        when(postService.computePostAnalytics(ARTIST_USER, postSlug)).thenReturn(analytics);

        // WHEN & THEN
        mockMvc.perform(get("/posts/post-slug/analytics")
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likes").value(1))
                .andExpect(jsonPath("$.comments").value(2))
                .andExpect(jsonPath("$.reposts").value(3))
                .andExpect(jsonPath("$.subscribedLikes").value(0))
                .andExpect(jsonPath("$.subscribedComments").value(1))
                .andExpect(jsonPath("$.subscribedReposts").value(2));
    }

    @Test
    void givenInvalidPermission_whenGetPostAnalytics_thenForbidden() throws Exception {
        // GIVEN
        when(postService.computePostAnalytics(ARTIST_USER, "post-slug"))
                .thenThrow(new InvalidPermissionException("Only to post owner can access the analytics"));

        // WHEN & THEN
        mockMvc.perform(get("/posts/post-slug/analytics")
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenAuthenticatedUser_whenGetPostAnalytics_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(get("/posts/post-slug/analytics")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenUnauthenticatedRequest_whenGetPostAnalytics_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(get("/posts/post-slug/analytics"))
                .andExpect(status().isForbidden());
    }

}

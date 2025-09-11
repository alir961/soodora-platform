package com.soodora.api.post;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.post.*;
import com.soodora.domain.user.UserDTO;
import com.soodora.domain.user.UserRepository;
import com.soodora.service.auth.UserPrincipal;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.post.CommentPostRequest;
import com.soodora.service.post.CreatePostRequest;
import com.soodora.service.post.PostService;
import com.soodora.service.user.UnknownUserException;

import java.util.Date;
import java.util.List;

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

import static java.lang.String.valueOf;
import static java.util.Optional.of;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static com.soodora.api.JsonStringify.asJsonString;
import static com.soodora.test.TestConstants.ARTIST_USER;
import static com.soodora.test.TestConstants.USER;
import static com.soodora.test.factory.PostFactory.buildDefaultPost;
import static com.soodora.test.factory.UserFactory.buildDefaultUser;

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @MockitoBean
    private UserRepository userRepository;

    private static final PostDTO POST_1 = buildDefaultPost("postSlug1", "Post 1");
    private static final PostDTO POST_2 = buildDefaultPost("postSlug2", "Post 2");
    private static final PostDTO POST_3 = buildDefaultPost("postSlug3", "Post 3");

    private static final UserDTO USER_1 = buildDefaultUser(1);
    private static final UserDTO USER_2 = buildDefaultUser(2);

    @Test
    void createPost_Success() throws Exception {
        // GIVEN
        CreatePostRequest createPostRequest = new CreatePostRequest(
                "Post 1",
                "Content",
                "audioUrl");

        when(userRepository.find(ARTIST_USER.identifier())).thenReturn(ARTIST_USER);

        MockMultipartFile jsonPart = new MockMultipartFile(
                "data",
                "",
                "application/json",
                asJsonString(createPostRequest).getBytes());

        MockMultipartFile filePart = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "fake image content".getBytes());

        // WHEN & THEN
        mockMvc.perform(multipart("/posts")
                .file(jsonPart)
                .file(filePart)
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isCreated());
    }

    @Test
    void createPost_InvalidRequest() throws Exception {
        // GIVEN
        CreatePostRequest createPostRequest = new CreatePostRequest(
                "",
                "Description",
                "audioUrl");
        FileData fileData = new FileData(
                "dummy image content".getBytes(),
                "test-image.jpg",
                "image/jpeg");

        when(userRepository.find(ARTIST_USER.identifier())).thenReturn(ARTIST_USER);
        doThrow(new InvalidRequestException("Invalid request")).when(postService)
                .createPost(any(CreatePostCommand.class), any(UserDTO.class), eq(fileData));

        // WHEN & THEN
        mockMvc.perform(post("/posts")
                .contentType("application/json")
                .content(asJsonString(createPostRequest))
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void retrievePost_Success() throws Exception {
        // GIVEN
        Page<PostDTO> postsPage = new PageImpl<>(List.of(POST_1, POST_2, POST_3));
        when(postService.retrievePosts(any(), any())).thenReturn(postsPage);
        when(userRepository.find(USER.identifier())).thenReturn(USER);

        // WHEN & THEN
        mockMvc.perform(get("/posts/search")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[0].slug").value(POST_1.slug()))
                .andExpect(jsonPath("$.content[1].slug").value(POST_2.slug()))
                .andExpect(jsonPath("$.content[2].slug").value(POST_3.slug()));
    }

    @Test
    void retrievePosts_Empty() throws Exception {
        // GIVEN
        Page<PostDTO> emptyPage = new PageImpl<>(List.of());
        when(postService.retrievePosts(any(), any())).thenReturn(emptyPage);
        when(userRepository.find(USER.identifier())).thenReturn(USER);

        // WHEN & THEN
        mockMvc.perform(get("/posts/search")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    void retrievePostBySlug_Success() throws Exception {
        // GIVEN
        when(postService.retrievePostBySlug("postSlug1")).thenReturn(POST_1);
        when(userRepository.find(USER.identifier())).thenReturn(USER);

        // WHEN & THEN
        mockMvc.perform(get("/posts/postSlug1")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("postSlug1"))
                .andExpect(jsonPath("$.title").value("Post 1"));
    }

    @Test
    void retrievePost_Unknown() throws Exception {
        // GIVEN
        when(userRepository.find(USER.identifier())).thenReturn(USER);
        doThrow(UnknownUserException.class).when(postService).retrievePostBySlug(anyString());

        // WHEN & THEN
        mockMvc.perform(get("/posts/unknown")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenValidFilter_whenSearchPosts_thenRetrieveFilteredPosts() throws Exception {
        // GIVEN
        String userSlug = "slug1";
        boolean hasAudio = true;
        String title = "title1";
        String description = "description1";
        Page<PostDTO> filteredPage = new PageImpl<>(List.of(POST_1));
        PostFilter postFilter = new PostFilter(of(userSlug), of(hasAudio), of(title), of(description));

        when(postService.retrievePosts(eq(postFilter), any(Pageable.class))).thenReturn(filteredPage);
        when(userRepository.find(USER.identifier())).thenReturn(USER);

        // WHEN & THEN
        mockMvc.perform(get("/posts/search")
                .param("userSlug", userSlug)
                .param("hasAudio", valueOf(hasAudio))
                .param("title", title)
                .param("description", description)
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].slug").value(POST_1.slug()));
    }

    @Test
    void deletePost_Success() throws Exception {
        // GIVEN
        when(userRepository.find(USER.identifier())).thenReturn(USER);
        doNothing().when(postService).deletePostBySlug("postSlug1");

        // WHEN & THEN
        mockMvc.perform(delete("/posts/postSlug1")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
        verify(postService).deletePostBySlug("postSlug1");
    }

    @Test
    void deletePost_Unknown() throws Exception {
        // GIVEN
        when(userRepository.find(USER.identifier())).thenReturn(USER);
        doThrow(UnknownUserException.class).when(postService).deletePostBySlug(anyString());

        // WHEN & THEN
        mockMvc.perform(delete("/posts/unknown")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePost_Forbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/posts/postSlug1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void likePost_Success() throws Exception {
        // GIVEN
        when(userRepository.find(USER.identifier())).thenReturn(USER);
        doNothing().when(postService).likePost("postSlug1", USER);

        // WHEN & THEN
        mockMvc.perform(post("/posts/postSlug1/likes")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
    }

    @Test
    void likePost_Forbidden() throws Exception {
        // GIVEN

        // WHEN & THEN
        mockMvc.perform(post("/posts/postSlug1/likes"))
                .andExpect(status().isForbidden());
    }

    @Test
    void unlikePost_Success() throws Exception {
        // GIVEN
        when(userRepository.find(USER.identifier())).thenReturn(USER);
        doNothing().when(postService).unlikePost("postSlug1", USER);

        // WHEN & THEN
        mockMvc.perform(delete("/posts/postSlug1/likes")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
    }

    @Test
    void unlikePost_Forbidden() throws Exception {
        // GIVEN

        // WHEN & THEN
        mockMvc.perform(delete("/posts/postSlug1/likes"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getPostLikes_Success() throws Exception {
        // GIVEN
        when(postService.retrieveLikes("postSlug1"))
                .thenReturn(List.of(
                        new PostLikeDTO(POST_1.slug(), USER_1, new Date()),
                        new PostLikeDTO("postSlug1", USER_2, new Date())));

        // WHEN & THEN
        mockMvc.perform(get("/posts/postSlug1/likes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*]", containsInAnyOrder(USER_1.slug(), USER_2.slug())));
    }

    @Test
    void addComment_Success() throws Exception {
        // GIVEN
        CommentPostRequest commentRequest = new CommentPostRequest("Great post!");
        when(userRepository.find(USER.identifier())).thenReturn(USER);
        when(postService.commentPost(eq("postSlug1"), any(CommentPostCommand.class), eq(USER)))
                .thenReturn(new CommentDTO("commentSlug1", "postSlug1", "Great post!", USER, 0, new Date()));

        // WHEN & THEN
        mockMvc.perform(post("/posts/postSlug1/comments")
                .contentType("application/json")
                .content(asJsonString(commentRequest))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isCreated());
    }

    @Test
    void addComment_Forbidden() throws Exception {
        // GIVEN
        CommentPostRequest commentRequest = new CommentPostRequest("Unauthorized comment");

        // WHEN & THEN
        mockMvc.perform(post("/posts/postSlug1/comments")
                .contentType("application/json")
                .content(asJsonString(commentRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteComment_Success() throws Exception {
        // GIVEN
        when(userRepository.find(USER.identifier())).thenReturn(USER);
        doNothing().when(postService).deleteComment("commentSlug1");

        // WHEN & THEN
        mockMvc.perform(delete("/posts/postSlug1/comments/commentSlug1")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
    }

    @Test
    void deleteComment_Forbidden() throws Exception {
        // GIVEN

        // WHEN & THEN
        mockMvc.perform(delete("/posts/postSlug1/comments/commentSlug1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getComments_Success() throws Exception {
        // GIVEN
        when(postService.retrieveComments("postSlug1"))
                .thenReturn(List.of(
                        new CommentDTO("commentSlug1", "postSlug1", "Great post!", USER, 0, new Date()),
                        new CommentDTO("commentSlug2", "postSlug1", "Thanks for sharing", USER, 0, new Date())));

        // WHEN & THEN
        mockMvc.perform(get("/posts/postSlug1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").value("commentSlug1"))
                .andExpect(jsonPath("$[0].content").value("Great post!"))
                .andExpect(jsonPath("$[0].user.slug").value(USER.slug()))
                .andExpect(jsonPath("$[1].slug").value("commentSlug2"))
                .andExpect(jsonPath("$[1].content").value("Thanks for sharing"))
                .andExpect(jsonPath("$[1].user.slug").value(USER.slug()));
    }

    @Test
    void likeComment_Success() throws Exception {
        // GIVEN
        when(userRepository.find(USER.identifier())).thenReturn(USER);
        doNothing().when(postService).likeComment("commentSlug1", USER);

        // WHEN & THEN
        mockMvc.perform(post("/posts/postSlug1/comments/commentSlug1/likes")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
    }

    @Test
    void likeComment_Forbidden() throws Exception {
        // GIVEN

        // WHEN & THEN
        mockMvc.perform(post("/posts/postSlug1/comments/commentSlug1/likes"))
                .andExpect(status().isForbidden());
    }

    @Test
    void unlikeComment_Success() throws Exception {
        // GIVEN
        when(userRepository.find(USER.identifier())).thenReturn(USER);
        doNothing().when(postService).unlikeComment("commentSlug1", USER);

        // WHEN & THEN
        mockMvc.perform(delete("/posts/postSlug1/comments/commentSlug1/likes")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
    }

    @Test
    void unlikeComment_Forbidden() throws Exception {
        // GIVEN

        // WHEN & THEN
        mockMvc.perform(delete("/posts/postSlug1/comments/commentSlug1/likes"))
                .andExpect(status().isForbidden());
    }

}

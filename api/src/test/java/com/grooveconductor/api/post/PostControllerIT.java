package com.grooveconductor.api.post;

import com.grooveconductor.domain.post.CreatePostCommand;
import com.grooveconductor.domain.post.PostDTO;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.domain.user.UserRepository;
import com.grooveconductor.service.auth.UserPrincipal;
import com.grooveconductor.service.common.InvalidRequestException;
import com.grooveconductor.service.post.CreatePostRequest;
import com.grooveconductor.service.post.PostService;
import com.grooveconductor.service.user.UnknownUserException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.grooveconductor.api.JsonStringify.asJsonString;
import static com.grooveconductor.test.TestConstants.USER;
import static com.grooveconductor.test.factory.PostFactory.buildDefaultPost;

@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @MockBean
    private UserRepository userRepository;

    private static final PostDTO POST_1 = buildDefaultPost("postSlug1", "Post 1");
    private static final PostDTO POST_2 = buildDefaultPost("postSlug2", "Post 2");
    private static final PostDTO POST_3 = buildDefaultPost("postSlug3", "Post 3");

    @Test
    void createPost_Success() throws Exception {
        // GIVEN
        CreatePostRequest createPostRequest = new CreatePostRequest(
                "Post 1",
                "Content",
                "imageUrl",
                "audioUrl",
                "announce");

        when(userRepository.find(USER.identifier())).thenReturn(USER);

        // WHEN & THEN
        mockMvc.perform(post("/posts")
                .contentType("application/json")
                .content(asJsonString(createPostRequest))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isCreated());
    }

    @Test
    void createPost_InvalidRequest() throws Exception {
        // GIVEN
        CreatePostRequest createPostRequest = new CreatePostRequest(
                "",
                "Description",
                "imageUrl",
                "audioUrl",
                "announce");

        when(userRepository.find(USER.identifier())).thenReturn(USER);
        doThrow(new InvalidRequestException("Invalid request")).when(postService)
                .createPost(any(CreatePostCommand.class), any(UserDTO.class));

        // WHEN & THEN
        mockMvc.perform(post("/posts")
                .contentType("application/json")
                .content(asJsonString(createPostRequest))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void retrievePost_Success() throws Exception {
        // GIVEN
        when(postService.retrieveAllPosts()).thenReturn(asList(POST_1, POST_2, POST_3));
        when(userRepository.find(USER.identifier())).thenReturn(USER);

        // WHEN & THEN
        mockMvc.perform(get("/posts")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
    }

    @Test
    void retrievePlaylists_Empty() throws Exception {
        // GIVEN
        when(postService.retrieveAllPosts()).thenReturn(emptyList());
        when(userRepository.find(USER.identifier())).thenReturn(USER);

        // WHEN & THEN
        mockMvc.perform(get("/posts")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
    }

    @Test
    void retrievePlaylists_Forbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(get("/posts"))
                .andExpect(status().isForbidden());
    }

    @Test
    void retrievePlaylist_Success() throws Exception {
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
    void retrievePlaylist_Unknown() throws Exception {
        // GIVEN
        when(userRepository.find(USER.identifier())).thenReturn(USER);
        doThrow(UnknownUserException.class).when(postService).retrievePostBySlug(anyString());

        // WHEN & THEN
        mockMvc.perform(get("/posts/unknown")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isNotFound());
    }

    @Test
    void retrievePlaylist_Forbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(get("/posts/postSlug1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deletePlaylist_Success() throws Exception {
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
    void deletePlaylist_Unknown() throws Exception {
        // GIVEN
        when(userRepository.find(USER.identifier())).thenReturn(USER);
        doThrow(UnknownUserException.class).when(postService).deletePostBySlug(anyString());

        // WHEN & THEN
        mockMvc.perform(delete("/posts/unknown")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePlaylist_Forbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/posts/postSlug1"))
                .andExpect(status().isForbidden());
    }

}

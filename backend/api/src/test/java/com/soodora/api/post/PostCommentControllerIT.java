package com.soodora.api.post;

import com.soodora.domain.post.CommentDTO;
import com.soodora.domain.post.CommentLikeDTO;
import com.soodora.domain.post.CommentPostCommand;
import com.soodora.domain.post.LikeDTO;
import com.soodora.domain.user.PublicUserDTO;
import com.soodora.domain.user.User;
import com.soodora.service.auth.UserPrincipal;
import com.soodora.service.post.CommentPostRequest;
import com.soodora.service.post.PostEngagementService;
import com.soodora.service.post.UnknownPostCommentException;
import com.soodora.service.post.UnknownPostException;
import com.soodora.service.shared.InvalidPermissionException;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.soodora.api.JsonStringify.asJsonString;
import static com.soodora.domain.factory.UserFactory.buildDefaultUser;
import static com.soodora.test.TestConstants.USER;

@SpringBootTest
@AutoConfigureMockMvc
class PostCommentControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostEngagementService postEngagementService;

    private static final User USER_1 = buildDefaultUser(1);
    private static final User USER_2 = buildDefaultUser(2);

    private static final CommentDTO COMMENT_1 = new CommentDTO("commentSlug1", "post-slug", "Great post!",
            PublicUserDTO.build(USER), 0, new Date());
    private static final CommentDTO COMMENT_2 = new CommentDTO("commentSlug2", "post-slug", "Thanks for sharing",
            PublicUserDTO.build(USER), 0, new Date());

    @Test
    void givenValidRequest_whenCommentPost_thenCreated() throws Exception {
        // GIVEN
        CommentPostRequest commentRequest = new CommentPostRequest("Great post!");

        when(postEngagementService.commentPost(eq(USER), eq("post-slug"), any(CommentPostCommand.class)))
                .thenReturn(COMMENT_1);

        // WHEN & THEN
        mockMvc.perform(post("/posts/post-slug/comments")
                .contentType("application/json")
                .content(asJsonString(commentRequest))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isCreated());
    }

    @Test
    void givenInvalidRequest_whenCommentPost_thenBadRequest() throws Exception {
        // GIVEN
        CommentPostRequest commentRequest = new CommentPostRequest("");

        // WHEN & THEN
        mockMvc.perform(post("/posts/post-slug/comments")
                .contentType("application/json")
                .content(asJsonString(commentRequest))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenUnauthenticatedUser_whenCommentPost_thenForbidden() throws Exception {
        // GIVEN
        CommentPostRequest commentRequest = new CommentPostRequest("Great post!");

        // WHEN & THEN
        mockMvc.perform(post("/posts/post-slug/comments")
                .contentType("application/json")
                .content(asJsonString(commentRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenValidSlug_whenGetComments_thenOk() throws Exception {
        // GIVEN
        when(postEngagementService.retrieveComments("post-slug"))
                .thenReturn(List.of(COMMENT_1, COMMENT_2));

        // WHEN & THEN
        mockMvc.perform(get("/posts/post-slug/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").value("commentSlug1"))
                .andExpect(jsonPath("$[1].slug").value("commentSlug2"));
    }

    @Test
    void givenInvalidSlug_whenGetComments_thenNotFound() throws Exception {
        // GIVEN
        when(postEngagementService.retrieveComments("post-slug"))
                .thenThrow(new UnknownPostException("Unable to find post"));

        // WHEN & THEN
        mockMvc.perform(get("/posts/post-slug/comments"))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenValidSlug_whenGetCommentLikes_thenOk() throws Exception {
        // GIVEN
        List<LikeDTO> likes = List.of(
                new CommentLikeDTO(COMMENT_1.slug(), PublicUserDTO.build(USER_1), new Date()),
                new CommentLikeDTO(COMMENT_1.slug(), PublicUserDTO.build(USER_2), new Date()));

        when(postEngagementService.retrieveCommentLikes("comment-slug"))
                .thenReturn(likes);

        // WHEN & THEN
        mockMvc.perform(get("/posts/post-slug/comments/comment-slug/likes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*]", containsInAnyOrder(USER_1.getSlug(), USER_2.getSlug())));
    }

    @Test
    void givenInvalidSlug_whenGetCommentLikes_thenNotFound() throws Exception {
        // GIVEN
        when(postEngagementService.retrieveCommentLikes("comment-slug"))
                .thenThrow(new UnknownPostCommentException("Unable to find post comment"));

        // WHEN & THEN
        mockMvc.perform(get("/posts/post-slug/comments/comment-slug/likes"))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenValidSlug_whenLikeComment_thenOk() throws Exception {
        // WHEN & THEN
        mockMvc.perform(post("/posts/post-slug/comments/comment-slug/likes")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
    }

    @Test
    void givenInvalidSlug_whenLikeComment_thenNotFound() throws Exception {
        // GIVEN
        doThrow(new UnknownPostCommentException("Unable to find post comment"))
                .when(postEngagementService).likeComment(USER, "comment-slug");

        // WHEN & THEN
        mockMvc.perform(post("/posts/post-slug/comments/comment-slug/likes")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenUnauthenticatedUser_whenLikeComment_thenNotFound() throws Exception {
        // WHEN & THEN
        mockMvc.perform(post("/posts/post-slug/comments/comment-slug/likes"))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenValidSlug_whenUnlikeComment_thenOk() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/posts/post-slug/comments/comment-slug/likes")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
    }

    @Test
    void givenInvalidSlug_whenUnlikeComment_thenNotFound() throws Exception {
        // GIVEN
        doThrow(new UnknownPostCommentException("Unable to find post comment"))
                .when(postEngagementService).unlikeComment(USER, "comment-slug");

        // WHEN & THEN
        mockMvc.perform(delete("/posts/post-slug/comments/comment-slug/likes")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenUnauthenticatedUser_whenUnlikeComment_thenNotFound() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/posts/post-slug/comments/comment-slug/likes"))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenAuthenticatedUser_whenDeleteComment_thenOk() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/posts/post-slug/comments/comment-slug")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
    }

    @Test
    void givenInvalidPermission_whenDeleteComment_thenForbidden() throws Exception {
        // GIVEN
        doThrow(new InvalidPermissionException("Only the owner can delete the comment"))
                .when(postEngagementService).deleteComment(USER, "comment-slug");

        // WHEN & THEN
        mockMvc.perform(delete("/posts/post-slug/comments/comment-slug")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenUnauthenticatedUser_whenDeleteComment_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/posts/post-slug/comments/comment-slug"))
                .andExpect(status().isForbidden());
    }

}

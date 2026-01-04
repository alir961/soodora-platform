package com.soodora.service.post;

import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.post.*;
import com.soodora.domain.user.PublicUserDTO;
import com.soodora.domain.user.UnknownIdentifierException;
import com.soodora.domain.user.User;
import com.soodora.domain.user.UserRepository;
import com.soodora.domain.user.value.Identifier;
import com.soodora.service.shared.InvalidPermissionException;
import com.soodora.service.user.UnknownUserException;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

import static com.soodora.domain.factory.PostFactory.buildDefaultPost;
import static com.soodora.domain.user.value.Identifier.Factory.createFromSlug;
import static com.soodora.test.TestConstants.*;

@ExtendWith(MockitoExtension.class)
class PostEngagementServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostEngagementService postService;

    private static final Post POST = buildDefaultPost("slug", "Post");

    private static final CommentPostCommand COMMENT_COMMAND = new CommentPostCommand("content");
    private static final CommentDTO COMMENT = new CommentDTO("slug", POST.getSlug(), "content",
            PublicUserDTO.build(USER), 0, new Date());
    private static final CommentLikeDTO COMMENT_LIKE = new CommentLikeDTO(COMMENT.slug(), PublicUserDTO.build(USER),
            new Date());
    private static final PostLikeDTO POST_LIKE = new PostLikeDTO(POST.getSlug(), PublicUserDTO.build(USER), new Date());

    @Test
    void givenValidSlug_whenLikePost_thenPostIsLiked() {
        // WHEN
        postService.likePost(USER, POST.getSlug());

        // THEN
        verify(postRepository).likePost(USER, POST.getSlug());
    }

    @Test
    void givenInvalidSlug_whenLikePost_thenThrowUnknownPostException() {
        // GIVEN
        doThrow(UnknownSlugException.class)
                .when(postRepository).likePost(USER, "unknown");

        // WHEN
        Executable executable = () -> postService.likePost(USER, "unknown");

        // THEN
        assertThrows(UnknownPostException.class, executable);
    }

    @Test
    void givenValidSlug_whenUnlikePost_thenPostIsUnliked() {
        // WHEN
        postService.unlikePost(USER, POST.getSlug());

        // THEN
        verify(postRepository).unlikePost(USER, POST.getSlug());
    }

    @Test
    void givenInvalidSlug_whenUnlikePost_thenThrowUnknownPostException() {
        // GIVEN
        doThrow(UnknownSlugException.class)
                .when(postRepository).unlikePost(USER, "unknown");

        // WHEN
        Executable executable = () -> postService.unlikePost(USER, "unknown");

        // THEN
        assertThrows(UnknownPostException.class, executable);
    }

    @Test
    void givenValidSlug_whenRetrieveLikes_thenReturnPostLikes() {
        // GIVEN
        List<LikeDTO> expectedLikes = List.of(POST_LIKE);

        when(postRepository.retrieveLikes(POST.getSlug()))
                .thenReturn(expectedLikes);

        // WHEN
        List<LikeDTO> result = postService.retrieveLikes(POST.getSlug());

        // THEN
        assertEquals(expectedLikes, result);
    }

    @Test
    void givenInvalidSlug_whenRetrieveLikes_thenThrowUnknownPostException() {
        // GIVEN
        doThrow(UnknownSlugException.class)
                .when(postRepository).retrieveLikes("unknown");

        // WHEN
        Executable executable = () -> postService.retrieveLikes("unknown");

        // THEN
        assertThrows(UnknownPostException.class, executable);
    }

    @Test
    void givenValidSlug_whenCommentPost_thenPostIsCommented() {
        // GIVEN
        when(postRepository.commentPost(USER, POST.getSlug(), COMMENT_COMMAND))
                .thenReturn(COMMENT);

        // WHEN
        CommentDTO result = postService.commentPost(USER, POST.getSlug(), COMMENT_COMMAND);

        // THEN
        assertEquals(COMMENT, result);
    }

    @Test
    void givenInvalidSlug_whenCommentPost_thenThrowUnknownPostException() {
        // GIVEN
        doThrow(UnknownSlugException.class)
                .when(postRepository).commentPost(USER, "unknown", COMMENT_COMMAND);

        // WHEN
        Executable executable = () -> postService.commentPost(USER, "unknown", COMMENT_COMMAND);

        // THEN
        assertThrows(UnknownPostException.class, executable);
    }

    @Test
    void givenValidSlug_whenRetrieveComments_thenReturnPostComments() {
        // GIVEN
        List<CommentDTO> expectedComments = singletonList(COMMENT);

        when(postRepository.retrieveComments(POST.getSlug()))
                .thenReturn(expectedComments);

        // WHEN
        List<CommentDTO> result = postService.retrieveComments(POST.getSlug());

        // THEN
        assertEquals(expectedComments, result);
    }

    @Test
    void givenInvalidSlug_whenRetrieveComments_thenThrowUnknownPostException() {
        // GIVEN
        doThrow(UnknownSlugException.class)
                .when(postRepository).retrieveComments("unknown");

        // WHEN & THEN
        Executable executable = () -> postService.retrieveComments("unknown");

        assertThrows(UnknownPostException.class, executable);
    }

    @Test
    void givenCommentOwner_whenDeleteComment_thenCommentIsDeleted() {
        // GIVEN
        when(postRepository.isCommentOwner(USER, COMMENT.slug()))
                .thenReturn(true);

        // WHEN
        postService.deleteComment(USER, COMMENT.slug());

        // THEN
        verify(postRepository).deleteComment(COMMENT.slug());
    }

    @Test
    void givenAdministrator_whenDeleteComment_thenCommentIsDeleted() {
        // GIVEN
        when(postRepository.isCommentOwner(ADMIN, COMMENT.slug()))
                .thenReturn(false);

        // WHEN
        postService.deleteComment(ADMIN, COMMENT.slug());

        // THEN
        verify(postRepository).deleteComment(COMMENT.slug());
    }

    @Test
    void givenInvalidUser_whenDeleteComment_thenThrowInvalidPermissionException() {
        // GIVEN
        when(postRepository.isCommentOwner(USER, COMMENT.slug()))
                .thenReturn(false);

        // WHEN
        Executable executable = () -> postService.deleteComment(USER, COMMENT.slug());

        // THEN
        assertThrows(InvalidPermissionException.class, executable, "Only the owner can delete the comment");
    }

    @Test
    void givenInvalidSlug_whenDeleteComment_thenThrowUnknownPostCommentException() {
        // GIVEN
        when(postRepository.isCommentOwner(USER, "unknown"))
                .thenThrow(UnknownSlugException.class);

        // WHEN
        Executable executable = () -> postService.deleteComment(USER, "unknown");

        // THEN
        assertThrows(UnknownPostCommentException.class, executable);
    }

    @Test
    void givenInvalidSlug_whenLikeComment_thenThrowUnknownPostCommentException() {
        // GIVEN
        doThrow(UnknownSlugException.class)
                .when(postRepository).likeComment(USER, "unknown");

        // WHEN
        Executable executable = () -> postService.likeComment(USER, "unknown");

        // THEN
        assertThrows(UnknownPostCommentException.class, executable);
    }

    @Test
    void givenInvalidSlug_whenUnlikeComment_thenThrowUnknownPostCommentException() {
        // GIVEN
        doThrow(UnknownSlugException.class)
                .when(postRepository).unlikeComment(USER, "unknown");

        // WHEN
        Executable executable = () -> postService.unlikeComment(USER, "unknown");

        // THEN
        assertThrows(UnknownPostCommentException.class, executable);
    }

    @Test
    void givenValidSlug_whenRetrieveCommentLikes_thenReturnCommentLikes() {
        // GIVEN
        List<LikeDTO> expectedLikes = List.of(COMMENT_LIKE, COMMENT_LIKE);

        when(postRepository.retrieveCommentLikes(COMMENT.slug())).thenReturn(expectedLikes);

        // WHEN
        List<LikeDTO> result = postService.retrieveCommentLikes(COMMENT.slug());

        // THEN
        assertEquals(expectedLikes, result);
    }

    @Test
    void givenInvalidSlug_whenRetrieveCommentLikes_thenThrowUnknownPostCommentException() {
        // GIVEN
        doThrow(UnknownSlugException.class)
                .when(postRepository).retrieveCommentLikes("unknown");

        // WHEN
        Executable executable = () -> postService.retrieveCommentLikes("unknown");

        // THEN
        assertThrows(UnknownPostCommentException.class, executable);
    }

    @Test
    void givenInvalidSlug_whenRepostPost_thenThrowUnknownPostException() {
        // GIVEN
        doThrow(UnknownSlugException.class)
                .when(postRepository).repost(USER, "unknown");

        // WHEN
        Executable executable = () -> postService.repostPost(USER, "unknown");

        // THEN
        assertThrows(UnknownPostException.class, executable);
    }

    @Test
    void givenInvalidSlug_whenUnrepostPost_thenThrowUnknownPostException() {
        // GIVEN
        doThrow(UnknownSlugException.class)
                .when(postRepository).unrepost(USER, "unknown");

        // WHEN
        Executable executable = () -> postService.unrepostPost(USER, "unknown");

        // THEN
        assertThrows(UnknownPostException.class, executable);
    }

    @Test
    void givenValidSlug_whenRetrieveReposts_thenReturnRepostUsers() {
        // GIVEN
        when(postRepository.retrieveReposts(POST.getSlug()))
                .thenReturn(singleton(USER));

        // WHEN
        Set<User> reposts = postService.retrieveReposts(POST.getSlug());

        // THEN
        assertFalse(reposts.isEmpty());
    }

    @Test
    void givenInvalidSlug_whenRetrieveReposts_thenReturnRepostUsers() {
        // GIVEN
        doThrow(UnknownSlugException.class)
                .when(postRepository).retrieveReposts("unknown");

        // WHEN
        Executable executable = () -> postService.retrieveReposts("unknown");

        // THEN
        assertThrows(UnknownPostException.class, executable);
    }

    @Test
    void givenValidUserSlug_whenRetrieveUserLikes_thenReturnUserLikes() {
        // GIVEN
        List<LikeDTO> expected = List.of(POST_LIKE, COMMENT_LIKE);
        Identifier identifier = createFromSlug(USER.getSlug());

        when(userRepository.retrieveLikes(identifier))
                .thenReturn(expected);

        // WHEN
        List<LikeDTO> result = postService.retrieveUserLikes(USER.getSlug());

        // THEN
        assertEquals(expected, result);
    }

    @Test
    void givenInvalidUserSlug_whenRetrieveUserLikes_thenReturnUserLikes() {
        // GIVEN
        Identifier identifier = createFromSlug(USER.getSlug());

        when(userRepository.retrieveLikes(identifier))
                .thenThrow(UnknownIdentifierException.class);

        // WHEN
        Executable executable = () -> postService.retrieveUserLikes(USER.getSlug());

        // THEN
        assertThrows(UnknownUserException.class, executable);
    }

    @Test
    void givenValidUserSlug_whenRetrieveUserComments_thenReturnUserComments() {
        // GIVEN
        List<CommentDTO> expected = List.of(COMMENT, COMMENT);
        Identifier identifier = createFromSlug(USER.getSlug());

        when(userRepository.retrievePostComments(identifier))
                .thenReturn(expected);

        // WHEN
        List<CommentDTO> result = postService.retrieveUserComments(USER.getSlug());

        // THEN
        assertEquals(expected, result);
    }

    @Test
    void givenInvalidUserSlug_whenRetrieveUserComments_thenReturnUserComments() {
        // GIVEN
        Identifier identifier = createFromSlug(USER.getSlug());

        when(userRepository.retrievePostComments(identifier))
                .thenThrow(UnknownIdentifierException.class);

        // WHEN
        Executable executable = () -> postService.retrieveUserComments(USER.getSlug());

        // THEN
        assertThrows(UnknownUserException.class, executable);
    }

}

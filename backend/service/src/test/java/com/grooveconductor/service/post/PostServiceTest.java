package com.grooveconductor.service.post;

import com.grooveconductor.domain.common.UnknownSlugException;
import com.grooveconductor.domain.post.*;
import com.grooveconductor.domain.user.UserRepository;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static com.grooveconductor.test.TestConstants.USER;
import static com.grooveconductor.test.factory.PostFactory.buildDefaultPost;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    private PostService postService;

    private static final PostDTO POST_1 = buildDefaultPost("slug1", "Post 1");
    private static final PostDTO POST_2 = buildDefaultPost("slug2", "Post 2");
    private static final PostDTO POST_3 = buildDefaultPost("slug3", "Post 3");
    private static final List<PostDTO> POSTS = List.of(POST_1, POST_2, POST_3);

    @BeforeEach
    void setUp() {
        postService = new PostService(userRepository, postRepository);
    }

    @Test
    void createPost_Success() {
        // GIVEN
        CreatePostCommand command = new CreatePostCommand("New Post", "Content", "imageUrl", "audioUrl",
                PostType.ANNOUNCE);

        when(postRepository.save(any(), eq(USER))).thenReturn(POST_1);

        // WHEN
        postService.createPost(command, USER);

        // THEN
        verify(postRepository).save(any(), eq(USER));
    }

    @Test
    void retrieveAllPosts_Success() {
        // GIVEN
        when(postRepository.findAll()).thenReturn(POSTS);

        // WHEN
        List<PostDTO> retrievedPosts = postService.retrieveAllPosts();

        // THEN
        assertEquals(POSTS, retrievedPosts);
    }

    @Test
    void retrieveAllPosts_NoContent() {
        // GIVEN
        when(postRepository.findAll()).thenReturn(emptyList());

        // WHEN
        List<PostDTO> retrievedPosts = postService.retrieveAllPosts();

        // THEN
        assertTrue(retrievedPosts.isEmpty());
    }

    @Test
    void retrievePosts_NoFilter() {
        // GIVEN
        PostFilter noFilter = new PostFilter(empty());

        when(postRepository.findAll()).thenReturn(POSTS);

        // WHEN
        List<PostDTO> retrievedPosts = postService.retrievePosts(noFilter);

        // THEN
        assertEquals(POSTS, retrievedPosts);
    }

    @Test
    void retrievePost_FilterUserSlug() {
        // GIVEN
        PostFilter filter = new PostFilter(of(USER.slug()));

        when(userRepository.retrievePosts(any())).thenReturn(List.of(POST_1, POST_3));

        // WHEN
        List<PostDTO> retrievedPost = postService.retrievePosts(filter);

        // THEN
        assertEquals(2, retrievedPost.size());
        assertEquals(POST_1, retrievedPost.get(0));
        assertEquals(POST_3, retrievedPost.get(1));
    }

    @Test
    void retrievePostBySlug_Unknown() {
        // GIVEN
        String unknownSlug = "unknown";

        when(postRepository.find(unknownSlug)).thenThrow(UnknownPostException.class);

        // WHEN
        Executable executable = () -> postService.retrievePostBySlug(unknownSlug);

        // THEN
        assertThrows(UnknownPostException.class, executable);
    }

    @Test
    void deletePostBySlug_Success() {
        // GIVEN
        doNothing().when(postRepository).delete(POST_1.slug());

        // WHEN
        Executable executable = () -> postService.deletePostBySlug(POST_1.slug());

        // THEN
        assertDoesNotThrow(executable);
        verify(postRepository).delete(POST_1.slug());
    }

    @Test
    void deletePostBySlug_Unknown() {
        // GIVEN
        doThrow(UnknownSlugException.class).when(postRepository).delete(POST_1.slug());

        // WHEN
        Executable executable = () -> postService.deletePostBySlug(POST_1.slug());

        // THEN
        assertThrows(UnknownPostException.class, executable);
    }

    @Test
    void likePost_Success() {
        // GIVEN
        doNothing().when(postRepository).likePost(POST_1.slug(), USER);

        // WHEN
        assertDoesNotThrow(() -> postService.likePost(POST_1.slug(), USER));

        // THEN
        verify(postRepository).likePost(POST_1.slug(), USER);
    }

    @Test
    void likePost_UnknownSlugException_ThrowsUnknownPostException() {
        // GIVEN
        doThrow(new UnknownSlugException("not found")).when(postRepository).likePost(any(), any());

        // WHEN & THEN
        assertThrows(UnknownPostException.class, () -> postService.likePost("invalid-slug", USER));
    }

    @Test
    void unlikePost_Success() {
        // GIVEN
        doNothing().when(postRepository).unlikePost(POST_1.slug(), USER);

        // WHEN
        assertDoesNotThrow(() -> postService.unlikePost(POST_1.slug(), USER));

        // THEN
        verify(postRepository).unlikePost(POST_1.slug(), USER);
    }

    @Test
    void unlikePost_UnknownSlugException_ThrowsUnknownPostException() {
        // GIVEN
        doThrow(new UnknownSlugException("not found")).when(postRepository).unlikePost(any(), any());

        // WHEN & THEN
        assertThrows(UnknownPostException.class, () -> postService.unlikePost("invalid-slug", USER));
    }

    @Test
    void commentPost_Success() {
        // GIVEN
        CommentPostCommand command = new CommentPostCommand("Nice post");
        CommentDTO expectedComment = new CommentDTO("slug", "postSlug", "content", USER, 0, new Date());
        when(postRepository.commentPost(POST_1.slug(), command, USER)).thenReturn(expectedComment);

        // WHEN
        CommentDTO result = postService.commentPost(POST_1.slug(), command, USER);

        // THEN
        assertEquals(expectedComment, result);
        verify(postRepository).commentPost(POST_1.slug(), command, USER);
    }

    @Test
    void commentPost_UnknownSlugException_ThrowsUnknownPostException() {
        // GIVEN
        CommentPostCommand command = new CommentPostCommand("Invalid");
        doThrow(new UnknownSlugException("error")).when(postRepository).commentPost(any(), any(), any());

        // WHEN & THEN
        assertThrows(UnknownPostException.class, () -> postService.commentPost("invalid", command, USER));
    }

    @Test
    void likeComment_Success() {
        // GIVEN
        doNothing().when(postRepository).likeComment("comment-slug", USER);

        // WHEN & THEN
        assertDoesNotThrow(() -> postService.likeComment("comment-slug", USER));
    }

    @Test
    void likeComment_UnknownSlugException_ThrowsUnknownPostCommentException() {
        // GIVEN
        doThrow(new UnknownSlugException("error")).when(postRepository).likeComment(any(), any());

        // WHEN & THEN
        assertThrows(UnknownPostCommentException.class, () -> postService.likeComment("invalid", USER));
    }

    @Test
    void unlikeComment_Success() {
        // GIVEN
        doNothing().when(postRepository).unlikeComment("comment-slug", USER);

        // WHEN & THEN
        assertDoesNotThrow(() -> postService.unlikeComment("comment-slug", USER));
    }

    @Test
    void unlikeComment_UnknownSlugException_ThrowsUnknownPostCommentException() {
        // GIVEN
        doThrow(new UnknownSlugException("error")).when(postRepository).unlikeComment(any(), any());

        // WHEN & THEN
        assertThrows(UnknownPostCommentException.class, () -> postService.unlikeComment("invalid", USER));
    }

    @Test
    void deleteComment_Success() {
        // GIVEN
        doNothing().when(postRepository).deleteComment("comment-slug");

        // WHEN & THEN
        assertDoesNotThrow(() -> postService.deleteComment("comment-slug"));
    }

    @Test
    void deleteComment_UnknownSlugException_ThrowsUnknownPostCommentException() {
        // GIVEN
        doThrow(new UnknownSlugException("error")).when(postRepository).deleteComment(any());

        // WHEN & THEN
        assertThrows(UnknownPostCommentException.class, () -> postService.deleteComment("invalid"));
    }

    @Test
    void retrieveCommentLikes_Success() {
        // GIVEN
        List<LikeDTO> expectedLikes = List.of(
                new CommentLikeDTO("comment-slug", USER, new Date()),
                new CommentLikeDTO("comment-slug", USER, new Date()));
        when(postRepository.retrieveCommentLikes("comment-slug")).thenReturn(expectedLikes);

        // WHEN
        List<LikeDTO> result = postService.retrieveCommentLikes("comment-slug");

        // THEN
        assertEquals(expectedLikes, result);
    }

    @Test
    void retrieveCommentLikes_UnknownSlugException_ThrowsUnknownPostCommentException() {
        // GIVEN
        doThrow(new UnknownSlugException("error")).when(postRepository).retrieveCommentLikes(any());

        // WHEN & THEN
        assertThrows(UnknownPostCommentException.class, () -> postService.retrieveCommentLikes("invalid"));
    }

    @Test
    void retrieveCommentCounters_Success() {
        // GIVEN
        CommentCounterDTO counters = new CommentCounterDTO(5);
        when(postRepository.getCommentCounters("comment-slug")).thenReturn(counters);

        // WHEN
        CommentCounterDTO result = postService.retrieveCommentCounters("comment-slug");

        // THEN
        assertEquals(counters, result);
    }

    @Test
    void retrieveCommentCounters_UnknownSlugException_ThrowsUnknownPostCommentException() {
        // GIVEN
        doThrow(new UnknownSlugException("error")).when(postRepository).getCommentCounters(any());

        // WHEN & THEN
        assertThrows(UnknownPostCommentException.class, () -> postService.retrieveCommentCounters("invalid"));
    }

    @Test
    void retrieveLikes_Success() {
        // GIVEN
        List<LikeDTO> expectedLikes = List.of(new PostLikeDTO(POST_1.slug(), USER, new Date()));
        when(postRepository.retrieveLikes(POST_1.slug())).thenReturn(expectedLikes);

        // WHEN
        List<LikeDTO> result = postService.retrieveLikes(POST_1.slug());

        // THEN
        assertEquals(expectedLikes, result);
    }

    @Test
    void retrieveLikes_UnknownSlugException_ThrowsUnknownPostException() {
        // GIVEN
        doThrow(new UnknownSlugException("error")).when(postRepository).retrieveLikes(any());

        // WHEN & THEN
        assertThrows(UnknownPostException.class, () -> postService.retrieveLikes("invalid"));
    }

    @Test
    void retrieveComments_Success() {
        // GIVEN
        List<CommentDTO> expectedComments = List.of(new CommentDTO("slug", "postSlug", "content", USER, 0, new Date()));
        when(postRepository.retrieveComments(POST_1.slug())).thenReturn(expectedComments);

        // WHEN
        List<CommentDTO> result = postService.retrieveComments(POST_1.slug());

        // THEN
        assertEquals(expectedComments, result);
    }

    @Test
    void retrieveComments_UnknownSlugException_ThrowsUnknownPostException() {
        // GIVEN
        doThrow(new UnknownSlugException("error")).when(postRepository).retrieveComments(any());

        // WHEN & THEN
        assertThrows(UnknownPostException.class, () -> postService.retrieveComments("invalid"));
    }

    @Test
    void retrievePostCounters_Success() {
        // GIVEN
        PostCounterDTO counters = new PostCounterDTO(10, 5);
        when(postRepository.getPostCounters(POST_1.slug())).thenReturn(counters);

        // WHEN
        PostCounterDTO result = postService.retrievePostCounters(POST_1.slug());

        // THEN
        assertEquals(counters, result);
    }

    @Test
    void retrievePostCounters_UnknownSlugException_ThrowsUnknownPostException() {
        // GIVEN
        doThrow(new UnknownSlugException("error")).when(postRepository).getPostCounters(any());

        // WHEN & THEN
        assertThrows(UnknownPostException.class, () -> postService.retrievePostCounters("invalid"));
    }

    @Test
    void retrieveUserLikes_WithUserSlug_ReturnsLikes() throws MissingUserSlugException {
        // GIVEN
        PostFilter filter = new PostFilter(of(USER.slug()));
        List<LikeDTO> expected = List.of(
                new PostLikeDTO(POST_1.slug(), USER, new Date()),
                new CommentLikeDTO("comment-slug", USER, new Date()));
        when(userRepository.retrieveLikes(any())).thenReturn(expected);

        // WHEN
        List<LikeDTO> result = postService.retrieveUserLikes(filter);

        // THEN
        assertEquals(expected, result);
    }

    @Test
    void retrieveUserLikes_WithoutUserSlug_ThrowsException() {
        // GIVEN
        PostFilter filter = new PostFilter(empty());

        // WHEN & THEN
        assertThrows(MissingUserSlugException.class, () -> postService.retrieveUserLikes(filter));
    }

    @Test
    void retrieveUserComments_WithUserSlug_ReturnsComments() throws MissingUserSlugException {
        // GIVEN
        PostFilter filter = new PostFilter(of(USER.slug()));
        List<CommentDTO> expected = List.of(new CommentDTO("slug", "postSlug", "content", USER, 0, new Date()));
        when(userRepository.retrievePostComments(any())).thenReturn(expected);

        // WHEN
        List<CommentDTO> result = postService.retrieveUserComments(filter);

        // THEN
        assertEquals(expected, result);
    }

    @Test
    void retrieveUserComments_WithoutUserSlug_ThrowsException() {
        // GIVEN
        PostFilter filter = new PostFilter(empty());

        // WHEN & THEN
        assertThrows(MissingUserSlugException.class, () -> postService.retrieveUserComments(filter));
    }

}

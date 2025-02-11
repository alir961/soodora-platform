package com.grooveconductor.service.post;

import com.grooveconductor.domain.common.UnknownSlugException;
import com.grooveconductor.domain.post.*;
import com.grooveconductor.domain.user.UserRepository;

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
        CreatePostCommand command = new CreatePostCommand("New Post", "Content", "imageUrl", "audioUrl", PostType.ANNOUNCE);

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

        when(postRepository.find(unknownSlug)).thenThrow(UnknownSlugException.class);

        // WHEN
        Executable executable = () -> postService.retrievePostBySlug(unknownSlug);

        // THEN
        assertThrows(UnknownSlugException.class, executable); // Change expected exception here
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

}

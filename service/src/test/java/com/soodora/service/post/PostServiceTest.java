package com.soodora.service.post;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.post.*;
import com.soodora.domain.user.PublicUserDTO;
import com.soodora.domain.user.User;
import com.soodora.domain.user.UserRepository;
import com.soodora.domain.user.value.Identifier;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.data.domain.Sort.by;

import static com.soodora.domain.user.value.Identifier.Factory.createFromSlug;
import static com.soodora.test.TestConstants.USER;
import static com.soodora.test.factory.PostFactory.buildDefaultPost;
import static com.soodora.test.factory.PostFactory.buildDefaultPostWithoutAudio;
import static com.soodora.test.factory.UserFactory.buildDefaultUser;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    private PostService postService;

    private static final Post POST_1 = buildDefaultPost("slug1", "Post 1");
    private static final Post POST_2 = buildDefaultPost("slug2", "Post 2");
    private static final Post POST_3 = buildDefaultPost("slug3", "Post 3");
    private static final Post POST_4_WITHOUT_AUDIO = buildDefaultPostWithoutAudio("slug4", "Post 4");
    private static final Pageable PAGEABLE = PageRequest.of(0, 50, Sort.by("slug").ascending());
    private static final List<Post> POST_LIST = List.of(POST_1, POST_2, POST_3);
    private static final Page<Post> POSTS = new PageImpl<>(POST_LIST, PAGEABLE, POST_LIST.size());

    @BeforeEach
    void setUp() {
        postService = new PostService(userRepository, postRepository);
    }

    @Test
    void createPost_Success() {
        // GIVEN
        CreatePostCommand command = new CreatePostCommand("New Post", "Content", "My audio", "audioUrl");
        FileData fileData = new FileData(
                "dummy image content".getBytes(),
                "test-image.jpg",
                "image/jpeg");

        when(postRepository.save(any(), eq(USER), eq(fileData))).thenReturn(POST_1);

        // WHEN
        postService.createPost(command, USER, fileData);

        // THEN
        verify(postRepository).save(any(), eq(USER), eq(fileData));
    }

    @Test
    void retrievePosts_NoFilter() {
        // GIVEN
        PostFilter noFilter = new PostFilter(empty(), empty(), empty(), empty(), empty());

        when(postRepository.findAll(PAGEABLE)).thenReturn(POSTS);

        // WHEN
        Page<Post> retrievedPosts = postService.retrievePosts(noFilter, PAGEABLE);

        // THEN
        assertEquals(POSTS, retrievedPosts);
    }

    @Test
    void retrievePost_FilterUserSlug() {
        // GIVEN
        PostFilter filter = new PostFilter(of(USER.getSlug()), empty(), empty(), empty(), empty());

        List<Post> filteredPosts = List.of(POST_1, POST_3);
        Page<Post> pageFilteredPosts = new PageImpl<>(filteredPosts, PAGEABLE, filteredPosts.size());

        when(postRepository.findByFilter(filter, PAGEABLE)).thenReturn(pageFilteredPosts);

        // WHEN
        Page<Post> retrievedPosts = postService.retrievePosts(filter, PAGEABLE);

        // THEN
        assertEquals(2, retrievedPosts.getContent().size());
        assertEquals(POST_1, retrievedPosts.getContent().get(0));
        assertEquals(POST_3, retrievedPosts.getContent().get(1));
    }

    @Test
    void retrievePost_FilterHasAudioUrl_True() {
        // GIVEN
        PostFilter filter = new PostFilter(empty(), of(true), empty(), empty(), empty());

        List<Post> postsWithAudio = List.of(POST_1, POST_2);
        Page<Post> pageWithAudio = new PageImpl<>(postsWithAudio, PAGEABLE, postsWithAudio.size());

        when(postRepository.findByFilter(filter, PAGEABLE)).thenReturn(pageWithAudio);

        // WHEN
        Page<Post> retrievedPosts = postService.retrievePosts(filter, PAGEABLE);

        // THEN
        assertEquals(postsWithAudio.size(), retrievedPosts.getContent().size());
        assertEquals(postsWithAudio, retrievedPosts.getContent());
    }

    @Test
    void retrievePost_FilterHasAudioUrl_False() {
        // GIVEN
        PostFilter filter = new PostFilter(empty(), of(false), empty(), empty(), empty());

        List<Post> postsWithoutAudio = List.of(POST_4_WITHOUT_AUDIO);
        Page<Post> pageWithoutAudio = new PageImpl<>(postsWithoutAudio, PAGEABLE, postsWithoutAudio.size());

        when(postRepository.findByFilter(filter, PAGEABLE)).thenReturn(pageWithoutAudio);

        // WHEN
        Page<Post> retrievedPosts = postService.retrievePosts(filter, PAGEABLE);

        // THEN
        assertEquals(postsWithoutAudio.size(), retrievedPosts.getContent().size());
        assertEquals(postsWithoutAudio, retrievedPosts.getContent());
    }

    @Test
    void retrievePost_FilterUserSlug_FilterHasAudioUrl_True() {
        // GIVEN
        PostFilter filter = new PostFilter(of(USER.getSlug()), of(true), empty(), empty(), empty());

        List<Post> filteredPosts = List.of(POST_1, POST_3);
        Page<Post> pageFilteredPosts = new PageImpl<>(filteredPosts, PAGEABLE, filteredPosts.size());

        when(postRepository.findByFilter(filter, PAGEABLE)).thenReturn(pageFilteredPosts);

        // WHEN
        Page<Post> retrievedPosts = postService.retrievePosts(filter, PAGEABLE);

        // THEN
        assertEquals(filteredPosts.size(), retrievedPosts.getContent().size());
        assertEquals(filteredPosts, retrievedPosts.getContent());
    }

    @Test
    void retrievePost_FilterUserSlug_FilterHasAudioUrl_False() {
        // GIVEN
        PostFilter filter = new PostFilter(of(USER.getSlug()), of(false), empty(), empty(), empty());

        List<Post> filteredPosts = List.of(POST_4_WITHOUT_AUDIO);
        Page<Post> pageFilteredPosts = new PageImpl<>(filteredPosts, PAGEABLE, filteredPosts.size());

        when(postRepository.findByFilter(filter, PAGEABLE)).thenReturn(pageFilteredPosts);

        // WHEN
        Page<Post> retrievedPosts = postService.retrievePosts(filter, PAGEABLE);

        // THEN
        assertEquals(filteredPosts.size(), retrievedPosts.getContent().size());
        assertEquals(filteredPosts, retrievedPosts.getContent());
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
        doNothing().when(postRepository).delete(POST_1.getSlug());

        // WHEN
        Executable executable = () -> postService.deletePostBySlug(POST_1.getSlug());

        // THEN
        assertDoesNotThrow(executable);
        verify(postRepository).delete(POST_1.getSlug());
    }

    @Test
    void deletePostBySlug_Unknown() {
        // GIVEN
        doThrow(UnknownSlugException.class).when(postRepository).delete(POST_1.getSlug());

        // WHEN
        Executable executable = () -> postService.deletePostBySlug(POST_1.getSlug());

        // THEN
        assertThrows(UnknownPostException.class, executable);
    }

    @Test
    void likePost_Success() {
        // GIVEN
        doNothing().when(postRepository).likePost(POST_1.getSlug(), USER);

        // WHEN
        assertDoesNotThrow(() -> postService.likePost(POST_1.getSlug(), USER));

        // THEN
        verify(postRepository).likePost(POST_1.getSlug(), USER);
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
        doNothing().when(postRepository).unlikePost(POST_1.getSlug(), USER);

        // WHEN
        assertDoesNotThrow(() -> postService.unlikePost(POST_1.getSlug(), USER));

        // THEN
        verify(postRepository).unlikePost(POST_1.getSlug(), USER);
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
        CommentDTO expectedComment = new CommentDTO("slug", "postSlug", "content", PublicUserDTO.build(USER), 0,
                new Date());
        when(postRepository.commentPost(POST_1.getSlug(), command, USER)).thenReturn(expectedComment);

        // WHEN
        CommentDTO result = postService.commentPost(POST_1.getSlug(), command, USER);

        // THEN
        assertEquals(expectedComment, result);
        verify(postRepository).commentPost(POST_1.getSlug(), command, USER);
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
                new CommentLikeDTO("comment-slug", PublicUserDTO.build(USER), new Date()),
                new CommentLikeDTO("comment-slug", PublicUserDTO.build(USER), new Date()));
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
    void retrieveLikes_Success() {
        // GIVEN
        List<LikeDTO> expectedLikes = List.of(new PostLikeDTO(POST_1.getSlug(), PublicUserDTO.build(USER), new Date()));
        when(postRepository.retrieveLikes(POST_1.getSlug())).thenReturn(expectedLikes);

        // WHEN
        List<LikeDTO> result = postService.retrieveLikes(POST_1.getSlug());

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
        List<CommentDTO> expectedComments = List
                .of(new CommentDTO("slug", "postSlug", "content", PublicUserDTO.build(USER), 0, new Date()));
        when(postRepository.retrieveComments(POST_1.getSlug())).thenReturn(expectedComments);

        // WHEN
        List<CommentDTO> result = postService.retrieveComments(POST_1.getSlug());

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
    void retrieveUserLikes_WithUserSlug_ReturnsLikes() throws MissingUserSlugException {
        // GIVEN
        PostFilter filter = new PostFilter(of(USER.getSlug()), empty(), empty(), empty(), empty());
        List<LikeDTO> expected = List.of(
                new PostLikeDTO(POST_1.getSlug(), PublicUserDTO.build(USER), new Date()),
                new CommentLikeDTO("comment-slug", PublicUserDTO.build(USER), new Date()));
        when(userRepository.retrieveLikes(any())).thenReturn(expected);

        // WHEN
        List<LikeDTO> result = postService.retrieveUserLikes(filter);

        // THEN
        assertEquals(expected, result);
    }

    @Test
    void retrieveUserLikes_WithoutUserSlug_ThrowsException() {
        // GIVEN
        PostFilter filter = new PostFilter(empty(), empty(), empty(), empty(), empty());

        // WHEN & THEN
        assertThrows(MissingUserSlugException.class, () -> postService.retrieveUserLikes(filter));
    }

    @Test
    void retrieveUserComments_WithUserSlug_ReturnsComments() throws MissingUserSlugException {
        // GIVEN
        PostFilter filter = new PostFilter(of(USER.getSlug()), empty(), empty(), empty(), empty());
        List<CommentDTO> expected = List
                .of(new CommentDTO("slug", "postSlug", "content", PublicUserDTO.build(USER), 0, new Date()));
        when(userRepository.retrievePostComments(any())).thenReturn(expected);

        // WHEN
        List<CommentDTO> result = postService.retrieveUserComments(filter);

        // THEN
        assertEquals(expected, result);
    }

    @Test
    void retrieveUserComments_WithoutUserSlug_ThrowsException() {
        // GIVEN
        PostFilter filter = new PostFilter(empty(), empty(), empty(), empty(), empty());

        // WHEN & THEN
        assertThrows(MissingUserSlugException.class, () -> postService.retrieveUserComments(filter));
    }

    @Test
    void retrievePostsFromFollowings_returnsPostsPage() {
        // GIVEN
        Identifier identifier = createFromSlug("slug1");
        Pageable pageable = PageRequest.of(0, 50, by("slug").ascending());

        User user2 = buildDefaultUser(2);
        User user3 = buildDefaultUser(3);

        Set<User> followings = Set.of(user2, user3);
        Set<String> followingSlugs = followings.stream()
                .map(User::getSlug)
                .collect(toSet());

        Post post1 = buildDefaultPost("slug2", "title1");
        Post post2 = buildDefaultPost("slug3", "title2");

        Page<Post> expectedPage = new PageImpl<>(List.of(post1, post2), pageable, 2);

        User user = USER;

        user.follow(user2);
        user.follow(user3);

        when(userRepository.findWithFollowers(identifier)).thenReturn(user);
        when(postRepository.findByFollowingSlugs(followingSlugs, pageable)).thenReturn(expectedPage);

        // WHEN
        Page<Post> result = postService.retrievePostsFromFollowing(identifier, pageable);

        // THEN
        assertNotNull(result);
        assertEquals(expectedPage.getTotalElements(), result.getTotalElements());
        assertEquals(expectedPage.getContent(), result.getContent());

        verify(postRepository).findByFollowingSlugs(followingSlugs, pageable);
    }

}

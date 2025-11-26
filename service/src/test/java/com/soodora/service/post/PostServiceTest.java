package com.soodora.service.post;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.post.*;
import com.soodora.domain.user.User;
import com.soodora.domain.user.UserRepository;
import com.soodora.domain.user.value.Identifier;
import com.soodora.service.shared.InvalidPermissionException;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static com.soodora.domain.factory.PostFactory.buildDefaultPost;
import static com.soodora.domain.factory.UserFactory.buildDefaultUser;
import static com.soodora.domain.user.value.Identifier.Factory.createFromSlug;
import static com.soodora.test.TestConstants.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    private static final Post POST_1 = buildDefaultPost("slug1", "Post 1");
    private static final Post POST_2 = buildDefaultPost("slug2", "Post 2");
    private static final Post POST_3 = buildDefaultPost("slug3", "Post 3");
    private static final Pageable PAGEABLE = PageRequest.of(0, 50, Sort.by("slug").ascending());
    private static final List<Post> POST_LIST = List.of(POST_1, POST_2, POST_3);
    private static final Page<Post> POSTS = new PageImpl<>(POST_LIST, PAGEABLE, POST_LIST.size());
    private static final FileData FILE_DATA = new FileData(
            "dummy image content".getBytes(),
            "test-image.jpg",
            "image/jpeg");

    @Test
    void givenValidCommand_whenCreatePost_thenSavePost() {
        // GIVEN
        CreatePostCommand command = new CreatePostCommand("New Post", "Content", "My audio", "audioUrl");

        when(postRepository.save(USER, command, FILE_DATA))
                .thenReturn(POST_1);

        // WHEN
        postService.createPost(USER, command, FILE_DATA);

        // THEN
        verify(postRepository).save(USER, command, FILE_DATA);
    }

    @Test
    void givenValidSlug_whenRetrievePost_thenReturnPost() {
        // GIVEN
        when(postRepository.find(POST_1.getSlug()))
                .thenReturn(POST_1);

        // WHEN
        Post post = postService.retrievePost(POST_1.getSlug());

        // THEN
        assertEquals(POST_1, post);
    }

    @Test
    void givenInvalidSlug_whenRetrievePost_thenThrowUnknownPostException() {
        // GIVEN
        when(postRepository.find("unknown"))
                .thenThrow(UnknownPostException.class);

        // WHEN
        Executable executable = () -> postService.retrievePost("unknown");

        // THEN
        assertThrows(UnknownPostException.class, executable);
    }

    @Test
    void givenEmptyFilter_whenRetrievePosts_thenReturnAllPosts() {
        // GIVEN
        PostFilter emptyFilter = new PostFilter(empty(), empty(), empty(), empty(), empty());

        when(postRepository.findAll(PAGEABLE))
                .thenReturn(POSTS);

        // WHEN
        Page<Post> retrievedPosts = postService.retrievePosts(emptyFilter, PAGEABLE);

        // THEN
        assertEquals(POSTS, retrievedPosts);
    }

    @Test
    void givenUserAudioFilter_whenRetrievePosts_thenReturnFilteredPosts() {
        // GIVEN
        PostFilter filter = new PostFilter(of(ARTIST_USER.getSlug()), of(true), empty(), empty(), empty());

        List<Post> filteredPosts = List.of(POST_1, POST_3);
        Page<Post> pageFilteredPosts = new PageImpl<>(filteredPosts, PAGEABLE, filteredPosts.size());

        when(postRepository.findByFilter(filter, PAGEABLE))
                .thenReturn(pageFilteredPosts);

        // WHEN
        Page<Post> retrievedPosts = postService.retrievePosts(filter, PAGEABLE);

        // THEN
        assertEquals(2, retrievedPosts.getContent().size());
        assertEquals(POST_1, retrievedPosts.getContent().get(0));
        assertEquals(POST_3, retrievedPosts.getContent().get(1));
    }

    @Test
    void givenAuthenticatedUser_whenRetrieveRecommendedPosts_thenReturnRecommendedPosts() {
        // GIVEN
        when(postRepository.findRecommendedPosts(USER, PAGEABLE))
                .thenReturn(POSTS);

        // WHEN
        Page<Post> retrievedPosts = postService.retrieveRecommendedPosts(USER, PAGEABLE);

        // THEN
        assertEquals(POSTS, retrievedPosts);
    }

    @Test
    void givenUnauthenticatedUser_whenRetrieveRecommendedPosts_thenReturnAllPosts() {
        // GIVEN
        when(postRepository.findAll(PAGEABLE))
                .thenReturn(POSTS);

        // WHEN
        Page<Post> retrievedPosts = postService.retrieveRecommendedPosts(null, PAGEABLE);

        // THEN
        assertEquals(POSTS, retrievedPosts);
    }

    @Test
    void givenFollowingWithPosts_whenRetrievePostsFromFollowings_thenReturnPostsPage() {
        // GIVEN
        Identifier identifier = createFromSlug("slug1");

        User user = USER;
        User following1 = buildDefaultUser(2);
        User following2 = buildDefaultUser(3);
        Set<String> followingSlugs = Stream.of(following1, following2)
                .map(User::getSlug)
                .collect(toSet());

        Post post1 = buildDefaultPost("slug1", "title1");
        Post post2 = buildDefaultPost("slug2", "title2");
        Page<Post> expectedPage = new PageImpl<>(List.of(post1, post2), PAGEABLE, 2);

        user.follow(following1);
        user.follow(following2);

        when(userRepository.findWithFollowers(identifier))
                .thenReturn(user);
        when(postRepository.findByFollowingSlugs(followingSlugs, PAGEABLE))
                .thenReturn(expectedPage);

        // WHEN
        Page<Post> result = postService.retrievePostsFromFollowing(identifier, PAGEABLE);

        // THEN
        assertNotNull(result);
        assertEquals(expectedPage.getTotalElements(), result.getTotalElements());
        assertEquals(expectedPage.getContent(), result.getContent());
    }

    @Test
    void givenPostOwner_whenDeletePost_thenDeletePost() {
        // GIVEN
        when(postRepository.find(POST_1.getSlug()))
                .thenReturn(POST_1);

        // WHEN
        postService.deletePost(ARTIST_USER, POST_1.getSlug());

        // THEN
        verify(postRepository).delete(POST_1.getSlug());
    }

    @Test
    void givenAdministrator_whenDeletePost_thenDeletePost() {
        // GIVEN
        when(postRepository.find(POST_1.getSlug()))
                .thenReturn(POST_1);

        // WHEN
        postService.deletePost(ADMIN, POST_1.getSlug());

        // THEN
        verify(postRepository).delete(POST_1.getSlug());
    }

    @Test
    void givenInvalidUser_whenDeletePost_thenThrowInvalidPermissionException() {
        // GIVEN
        when(postRepository.find(POST_1.getSlug()))
                .thenReturn(POST_1);

        // WHEN
        Executable executable = () -> postService.deletePost(USER, POST_1.getSlug());

        // THEN
        assertThrows(InvalidPermissionException.class, executable, "Only the owner can delete the post");
    }

    @Test
    void givenInvalidSlug_whenDeletePost_thenThrowUnknownPostException() {
        // GIVEN
        doThrow(UnknownSlugException.class)
                .when(postRepository).find(POST_1.getSlug());

        // WHEN
        Executable executable = () -> postService.deletePost(ARTIST_USER, POST_1.getSlug());

        // THEN
        assertThrows(UnknownPostException.class, executable);
    }

    @Test
    void givenOwnerUser_whenComputePostAnalytics_thenReturnsAnalytics() {
        // GIVEN
        when(postRepository.find(POST_1.getSlug()))
                .thenReturn(POST_1);

        // WHEN
        PostAnalytics analytics = postService.computePostAnalytics(ARTIST_USER, POST_1.getSlug());

        // THEN
        assertNotNull(analytics);
    }

    @Test
    void givenAdminUser_whenComputePostAnalytics_thenReturnsAnalytics() {
        // GIVEN
        when(postRepository.find(POST_1.getSlug()))
                .thenReturn(POST_1);

        // WHEN
        PostAnalytics analytics = postService.computePostAnalytics(ADMIN, POST_1.getSlug());

        // THEN
        assertNotNull(analytics);
    }

    @Test
    void givenInvalidUser_whenComputePostAnalytics_thenThrowsInvalidPermissionException() {
        // GIVEN
        when(postRepository.find(POST_1.getSlug()))
                .thenReturn(POST_1);

        // WHEN
        Executable executable = () -> postService.computePostAnalytics(USER, POST_1.getSlug());

        // THEN
        assertThrows(InvalidPermissionException.class, executable, "Only the post owner can access the analytics");
    }

}

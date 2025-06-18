package com.grooveconductor.infrastructure.post;

import com.grooveconductor.domain.post.PostDTO;
import com.grooveconductor.domain.post.PostRepository;
import com.grooveconductor.infrastructure.user.UserEntity;
import com.grooveconductor.infrastructure.user.UserJpaRepository;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.Collections.emptySet;
import static java.util.Date.from;
import static java.util.List.of;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import static com.grooveconductor.infrastructure.common.SlugGenerator.generateSlug;
import static com.grooveconductor.infrastructure.user.UserEntityFactory.buildDefaultUserEntity;

@ExtendWith(MockitoExtension.class)
class PostRepositoryTest {

    private static final Pageable PAGE = PageRequest.of(0, 10);

    private final UserEntity currentUser = buildDefaultUserEntity();

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private PostJpaRepository postJpaRepository;

    @Mock
    private CommentJpaRepository commentJpaRepository;

    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        postRepository = new PostRepositoryImpl(userJpaRepository, postJpaRepository, commentJpaRepository);

        when(userJpaRepository.findBySlug(currentUser.getSlug())).thenReturn(Optional.of(currentUser));
    }

    @Test
    void givenLikedPosts_whenFindRecommendedPosts_thenSortPostsByScore() {
        // GIVEN
        PostEntity p1 = createPost(1, 0, 0, 1, false);
        PostEntity p2 = createPost(10, 0, 0, 1, false);

        when(postJpaRepository.findAll()).thenReturn(of(p1, p2));

        // WHEN
        PostDTO[] result = postRepository.findRecommendedPosts(currentUser.toDTO(), PAGE).get().toArray(PostDTO[]::new);

        // THEN
        assertEquals(p2.getSlug(), result[0].slug());
        assertEquals(p1.getSlug(), result[1].slug());
    }

    @Test
    void givenCommentedPosts_whenFindRecommendedPosts_thenSortPostsByScore() {
        // GIVEN
        PostEntity p1 = createPost(0, 1, 0, 1, false);
        PostEntity p2 = createPost(0, 10, 0, 1, false);

        when(postJpaRepository.findAll()).thenReturn(of(p1, p2));

        // WHEN
        PostDTO[] result = postRepository.findRecommendedPosts(currentUser.toDTO(), PAGE).get().toArray(PostDTO[]::new);

        // THEN
        assertEquals(p2.getSlug(), result[0].slug());
        assertEquals(p1.getSlug(), result[1].slug());
    }

    @Test
    void givenRepostedPosts_whenFindRecommendedPosts_thenSortPostsByScore() {
        // GIVEN
        PostEntity p1 = createPost(0, 0, 1, 1, false);
        PostEntity p2 = createPost(0, 0, 10, 1, false);

        when(postJpaRepository.findAll()).thenReturn(of(p1, p2));

        // WHEN
        PostDTO[] result = postRepository.findRecommendedPosts(currentUser.toDTO(), PAGE).get().toArray(PostDTO[]::new);

        // THEN
        assertEquals(p2.getSlug(), result[0].slug());
        assertEquals(p1.getSlug(), result[1].slug());
    }

    @Test
    void givenInteractedPosts_whenFindRecommendedPosts_thenSortPostsByScore() {
        // GIVEN
        PostEntity p1 = createPost(1, 1, 0, 1, false);
        PostEntity p2 = createPost(2, 2, 1, 1, false);

        when(postJpaRepository.findAll()).thenReturn(of(p1, p2));

        // WHEN
        PostDTO[] result = postRepository.findRecommendedPosts(currentUser.toDTO(), PAGE).get().toArray(PostDTO[]::new);

        // THEN
        assertEquals(p2.getSlug(), result[0].slug());
        assertEquals(p1.getSlug(), result[1].slug());
    }

    @Test
    void givenTimeSpacedPosts_whenFindRecommendedPosts_thenSortPostsByScore() {
        // GIVEN
        PostEntity p1 = createPost(0, 0, 0, 1, false);
        PostEntity p2 = createPost(0, 0, 0, 48, false);

        when(postJpaRepository.findAll()).thenReturn(of(p1, p2));

        // WHEN
        PostDTO[] result = postRepository.findRecommendedPosts(currentUser.toDTO(), PAGE).get().toArray(PostDTO[]::new);

        // THEN
        assertEquals(p1.getSlug(), result[0].slug());
        assertEquals(p2.getSlug(), result[1].slug());
    }

    @Test
    void givenPostsWithEngagement_whenFindRecommendedPosts_thenSortPostsByScore() {
        // GIVEN
        PostEntity p1 = createPost(10, 10, 5, 1, false);
        PostEntity p2 = createPost(1, 0, 0, 1, false);

        when(postJpaRepository.findAll()).thenReturn(of(p1, p2));

        // WHEN
        PostDTO[] result = postRepository.findRecommendedPosts(currentUser.toDTO(), PAGE).get().toArray(PostDTO[]::new);

        // THEN
        assertEquals(p1.getSlug(), result[0].slug());
        assertEquals(p2.getSlug(), result[1].slug());
    }

    @Test
    void givenPostsWithTimedEngagement_whenFindRecommendedPosts_thenSortPostsByScore() {
        // GIVEN
        PostEntity p1 = createPost(10, 10, 5, 48, false);
        PostEntity p2 = createPost(1, 0, 0, 1, false);

        when(postJpaRepository.findAll()).thenReturn(of(p1, p2));

        // WHEN
        PostDTO[] result = postRepository.findRecommendedPosts(currentUser.toDTO(), PAGE).get().toArray(PostDTO[]::new);

        // THEN
        assertEquals(p2.getSlug(), result[0].slug());
        assertEquals(p1.getSlug(), result[1].slug());
    }

    @Test
    void givenFollowedPosts_whenFindRecommendedPosts_thenSortPostsByScore() {
        // GIVEN
        PostEntity p1 = createPost(2, 2, 1, 1, false);
        PostEntity p2 = createPost(1, 2, 0, 1, true);

        when(postJpaRepository.findAll()).thenReturn(of(p1, p2));

        // WHEN
        PostDTO[] result = postRepository.findRecommendedPosts(currentUser.toDTO(), PAGE).get().toArray(PostDTO[]::new);

        // THEN
        assertEquals(p2.getSlug(), result[0].slug());
        assertEquals(p1.getSlug(), result[1].slug());
    }

    @Test
    void givenMixedPosts_whenFindRecommendedPosts_thenSortPostsByScore() {
        // GIVEN
        PostEntity p1 = createPost(1, 1, 0, 72, false);
        PostEntity p2 = createPost(3, 2, 1, 2, false);
        PostEntity p3 = createPost(5, 2, 1, 1, true);

        when(postJpaRepository.findAll()).thenReturn(of(p1, p2, p3));

        // WHEN
        PostDTO[] result = postRepository.findRecommendedPosts(currentUser.toDTO(), PAGE).get().toArray(PostDTO[]::new);

        // THEN
        assertEquals(p3.getSlug(), result[0].slug());
        assertEquals(p2.getSlug(), result[1].slug());
        assertEquals(p1.getSlug(), result[2].slug());
    }

    private PostEntity createPost(int likes, int comments, int reposts, int ageInHours, boolean isFollowed) {
        PostEntity post = new PostEntity();

        post.setSlug(generateSlug());
        post.setLikes(range(0, likes).mapToObj(index -> new LikeEntity()).collect(toSet()));
        post.setComments(range(0, comments).mapToObj(index -> new CommentEntity()).collect(toSet()));
        post.setReposts(range(0, reposts).mapToObj(index -> new UserEntity()).collect(toSet()));
        post.setCreatedAt(from(now().minus(ageInHours, HOURS)));

        UserEntity author = new UserEntity();

        author.setRoles(emptySet());

        if (isFollowed)
            currentUser.setFollowers(Set.of(author));
        else
            currentUser.setFollowers(emptySet());
        post.setUser(author);
        return post;
    }
}

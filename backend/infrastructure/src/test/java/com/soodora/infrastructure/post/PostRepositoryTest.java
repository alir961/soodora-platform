package com.soodora.infrastructure.post;

import com.soodora.domain.cdn.ImageStorageUploader;
import com.soodora.domain.playlist.PlaylistRepository;
import com.soodora.domain.post.Post;
import com.soodora.domain.post.PostRepository;
import com.soodora.infrastructure.artist.ArtistEntity;
import com.soodora.infrastructure.artist.ArtistJpaRepository;
import com.soodora.infrastructure.playlist.PlaylistJpaRepository;
import com.soodora.infrastructure.user.UserEntity;
import com.soodora.infrastructure.user.UserJpaRepository;

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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import static com.soodora.domain.common.SlugGenerator.generateSlug;
import static com.soodora.infrastructure.post.PostEntityFactory.buildDefaultCommentEntity;
import static com.soodora.infrastructure.post.PostEntityFactory.buildDefaultPostLikeEntity;
import static com.soodora.infrastructure.user.UserEntityFactory.buildDefaultUserEntity;

@ExtendWith(MockitoExtension.class)
class PostRepositoryTest {

    private static final Pageable PAGE = PageRequest.of(0, 10);

    private final UserEntity currentUser = buildDefaultUserEntity();

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private ArtistJpaRepository artistJpaRepository;

    @Mock
    private PostJpaRepository postJpaRepository;

    @Mock
    private CommentJpaRepository commentJpaRepository;

    @Mock
    private ImageStorageUploader imageStorageUploader;

    @Mock
    private PlaylistRepository playlistRepository;

    @Mock
    private PlaylistJpaRepository playlistJpaRepository;

    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        postRepository = new PostRepositoryImpl(userJpaRepository, postJpaRepository, commentJpaRepository,
                artistJpaRepository, imageStorageUploader, playlistRepository, playlistJpaRepository);

        when(userJpaRepository.findBySlug(currentUser.getSlug())).thenReturn(Optional.of(currentUser));
    }

    @Test
    void givenLikedPosts_whenFindRecommendedPosts_thenSortPostsByScore() {
        // GIVEN
        PostEntity p1 = createPost(1, 0, 0, 1, false);
        PostEntity p2 = createPost(10, 0, 0, 1, false);

        when(postJpaRepository.findAll()).thenReturn(of(p1, p2));

        // WHEN
        Post[] result = postRepository.findRecommendedPosts(currentUser.aggregate(), PAGE).get()
                .toArray(Post[]::new);

        // THEN
        assertEquals(p2.getSlug(), result[0].getSlug());
        assertEquals(p1.getSlug(), result[1].getSlug());
    }

    @Test
    void givenCommentedPosts_whenFindRecommendedPosts_thenSortPostsByScore() {
        // GIVEN
        PostEntity p1 = createPost(0, 1, 0, 1, false);
        PostEntity p2 = createPost(0, 10, 0, 1, false);

        when(postJpaRepository.findAll()).thenReturn(of(p1, p2));

        // WHEN
        Post[] result = postRepository.findRecommendedPosts(currentUser.aggregate(), PAGE).get()
                .toArray(Post[]::new);

        // THEN
        assertEquals(p2.getSlug(), result[0].getSlug());
        assertEquals(p1.getSlug(), result[1].getSlug());
    }

    @Test
    void givenRepostedPosts_whenFindRecommendedPosts_thenSortPostsByScore() {
        // GIVEN
        PostEntity p1 = createPost(0, 0, 1, 1, false);
        PostEntity p2 = createPost(0, 0, 10, 1, false);

        when(postJpaRepository.findAll()).thenReturn(of(p1, p2));

        // WHEN
        Post[] result = postRepository.findRecommendedPosts(currentUser.aggregate(), PAGE).get()
                .toArray(Post[]::new);

        // THEN
        assertEquals(p2.getSlug(), result[0].getSlug());
        assertEquals(p1.getSlug(), result[1].getSlug());
    }

    @Test
    void givenInteractedPosts_whenFindRecommendedPosts_thenSortPostsByScore() {
        // GIVEN
        PostEntity p1 = createPost(1, 1, 0, 1, false);
        PostEntity p2 = createPost(2, 2, 1, 1, false);

        when(postJpaRepository.findAll()).thenReturn(of(p1, p2));

        // WHEN
        Post[] result = postRepository.findRecommendedPosts(currentUser.aggregate(), PAGE).get()
                .toArray(Post[]::new);

        // THEN
        assertEquals(p2.getSlug(), result[0].getSlug());
        assertEquals(p1.getSlug(), result[1].getSlug());
    }

    @Test
    void givenTimeSpacedPosts_whenFindRecommendedPosts_thenSortPostsByScore() {
        // GIVEN
        PostEntity p1 = createPost(0, 0, 0, 1, false);
        PostEntity p2 = createPost(0, 0, 0, 48, false);

        when(postJpaRepository.findAll()).thenReturn(of(p1, p2));

        // WHEN
        Post[] result = postRepository.findRecommendedPosts(currentUser.aggregate(), PAGE).get()
                .toArray(Post[]::new);

        // THEN
        assertEquals(p1.getSlug(), result[0].getSlug());
        assertEquals(p2.getSlug(), result[1].getSlug());
    }

    @Test
    void givenPostsWithEngagement_whenFindRecommendedPosts_thenSortPostsByScore() {
        // GIVEN
        PostEntity p1 = createPost(10, 10, 5, 1, false);
        PostEntity p2 = createPost(1, 0, 0, 1, false);

        when(postJpaRepository.findAll()).thenReturn(of(p1, p2));

        // WHEN
        Post[] result = postRepository.findRecommendedPosts(currentUser.aggregate(), PAGE).get()
                .toArray(Post[]::new);

        // THEN
        assertEquals(p1.getSlug(), result[0].getSlug());
        assertEquals(p2.getSlug(), result[1].getSlug());
    }

    @Test
    void givenPostsWithTimedEngagement_whenFindRecommendedPosts_thenSortPostsByScore() {
        // GIVEN
        PostEntity p1 = createPost(10, 10, 5, 48, false);
        PostEntity p2 = createPost(1, 0, 0, 1, false);

        when(postJpaRepository.findAll()).thenReturn(of(p1, p2));

        // WHEN
        Post[] result = postRepository.findRecommendedPosts(currentUser.aggregate(), PAGE).get()
                .toArray(Post[]::new);

        // THEN
        assertEquals(p2.getSlug(), result[0].getSlug());
        assertEquals(p1.getSlug(), result[1].getSlug());
    }

    @Test
    void givenFollowedPosts_whenFindRecommendedPosts_thenSortPostsByScore() {
        // GIVEN
        PostEntity p1 = createPost(2, 2, 1, 1, false);
        PostEntity p2 = createPost(1, 2, 0, 1, true);

        when(postJpaRepository.findAll()).thenReturn(of(p1, p2));

        // WHEN
        Post[] result = postRepository.findRecommendedPosts(currentUser.aggregate(), PAGE).get()
                .toArray(Post[]::new);

        // THEN
        assertEquals(p2.getSlug(), result[0].getSlug());
        assertEquals(p1.getSlug(), result[1].getSlug());
    }

    @Test
    void givenMixedPosts_whenFindRecommendedPosts_thenSortPostsByScore() {
        // GIVEN
        PostEntity p1 = createPost(1, 1, 0, 72, false);
        PostEntity p2 = createPost(3, 2, 1, 2, false);
        PostEntity p3 = createPost(5, 2, 1, 1, true);

        when(postJpaRepository.findAll()).thenReturn(of(p1, p2, p3));

        // WHEN
        Post[] result = postRepository.findRecommendedPosts(currentUser.aggregate(), PAGE).get()
                .toArray(Post[]::new);

        // THEN
        assertEquals(p3.getSlug(), result[0].getSlug());
        assertEquals(p2.getSlug(), result[1].getSlug());
        assertEquals(p1.getSlug(), result[2].getSlug());
    }

    private PostEntity createPost(int likes, int comments, int reposts, int ageInHours, boolean isFollowed) {
        PostEntity post = new PostEntity();

        post.setSlug(generateSlug());
        post.setLikes(range(0, likes).mapToObj(i -> buildDefaultPostLikeEntity()).collect(toSet()));
        post.setComments(range(0, comments).mapToObj(i -> buildDefaultCommentEntity()).collect(toSet()));
        post.setReposts(range(0, reposts).mapToObj(i -> buildDefaultUserEntity()).collect(toSet()));
        post.setCreatedAt(from(now().minus(ageInHours, HOURS)));

        ArtistEntity artist = new ArtistEntity();
        UserEntity artistUser = buildDefaultUserEntity();

        if (isFollowed)
            currentUser.setFollowers(Set.of(artistUser));
        else
            currentUser.setFollowers(emptySet());

        artist.setUser(artistUser);
        post.setArtist(artist);

        return post;
    }
}

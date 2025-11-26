package com.soodora.domain.post;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import static com.soodora.domain.factory.PostFactory.buildDefaultPost;

class PostTest {

    @Test
    void givenPostWithoutEngagement_whenComputeAnalytics_thenComputeAnalytics() {
        // GIVEN
        Post post = buildDefaultPost("post1", "Post 1");

        // WHEN
        PostAnalytics postAnalytics = post.computeAnalytics();

        // THEN
        assertEquals(0, postAnalytics.getLikes());
        assertEquals(0, postAnalytics.getComments());
        assertEquals(0, postAnalytics.getReposts());
        assertEquals(0, postAnalytics.getSubscribedLikes());
        assertEquals(0, postAnalytics.getSubscribedComments());
        assertEquals(0, postAnalytics.getSubscribedReposts());
    }

}

package com.soodora.domain.indicator;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostIndicator {

    private int numberOfPosts;
    private int numberOfAudios;
    private int numberOfLikes;
    private int numberOfComments;
    private int numberOfReposts;
    private int numberOfPostsWithoutEngagement;

}

package com.soodora.domain.post;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostAnalytics {

    private int likes;
    private int comments;
    private int reposts;
    private int subscribedLikes;
    private int subscribedComments;
    private int subscribedReposts;

}

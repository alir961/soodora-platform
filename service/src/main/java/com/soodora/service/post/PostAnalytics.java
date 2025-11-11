package com.soodora.service.post;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostAnalytics {

    int likes;
    int comments;
    int reposts;
    int subscribedLikes;
    int subscribedComments;
    int subscribedReposts;

}

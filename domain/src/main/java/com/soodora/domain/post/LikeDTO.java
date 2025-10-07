package com.soodora.domain.post;

import com.soodora.domain.user.User;

import java.util.Date;

public interface LikeDTO {
    User user();
    Date likedAt();
}

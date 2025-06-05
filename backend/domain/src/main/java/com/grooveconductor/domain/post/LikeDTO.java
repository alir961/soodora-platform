package com.grooveconductor.domain.post;

import com.grooveconductor.domain.user.UserDTO;

import java.util.Date;

public interface LikeDTO {
    UserDTO user();
    Date likedAt();
}

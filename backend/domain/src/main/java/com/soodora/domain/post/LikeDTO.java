package com.soodora.domain.post;

import com.soodora.domain.user.UserDTO;

import java.util.Date;

public interface LikeDTO {
    UserDTO user();
    Date likedAt();
}

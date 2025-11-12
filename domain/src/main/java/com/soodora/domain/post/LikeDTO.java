package com.soodora.domain.post;

import com.soodora.domain.user.PublicUserDTO;

import java.util.Date;

public interface LikeDTO {
    PublicUserDTO user();
    Date likedAt();
}

package com.soodora.domain.post;

import com.soodora.domain.user.UserDTO;

import java.util.Date;

public record PostLikeDTO(String postSlug, UserDTO user, Date likedAt) implements LikeDTO {
}

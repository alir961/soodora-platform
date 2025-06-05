package com.grooveconductor.domain.post;

import com.grooveconductor.domain.user.UserDTO;

import java.util.Date;

public record PostLikeDTO(String postSlug, UserDTO user, Date likedAt) implements LikeDTO {
}

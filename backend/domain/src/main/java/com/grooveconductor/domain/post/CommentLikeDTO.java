package com.grooveconductor.domain.post;

import com.grooveconductor.domain.user.UserDTO;

import java.util.Date;

public record CommentLikeDTO(String commentSlug, UserDTO user, Date likedAt) implements LikeDTO {
}

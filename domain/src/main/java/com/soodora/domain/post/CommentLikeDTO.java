package com.soodora.domain.post;

import com.soodora.domain.user.User;

import java.util.Date;

public record CommentLikeDTO(String commentSlug, User user, Date likedAt) implements LikeDTO {
}

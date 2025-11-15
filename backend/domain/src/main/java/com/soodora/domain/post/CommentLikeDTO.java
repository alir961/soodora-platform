package com.soodora.domain.post;

import com.soodora.domain.user.PublicUserDTO;

import java.util.Date;

public record CommentLikeDTO(String commentSlug, PublicUserDTO user, Date likedAt) implements LikeDTO {
}

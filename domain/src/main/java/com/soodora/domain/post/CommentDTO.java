package com.soodora.domain.post;

import com.soodora.domain.user.PublicUserDTO;

import java.util.Date;

public record CommentDTO(
        String slug,
        String postSlug,
        String content,
        PublicUserDTO user,
        int likeCount,
        Date createdAt) {
}

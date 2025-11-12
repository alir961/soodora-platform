package com.soodora.domain.post;

import com.soodora.domain.user.User;

import java.util.Date;

public record CommentDTO(
        String slug,
        String postSlug,
        String content,
        User user,
        int likeCount,
        Date createdAt) {
}

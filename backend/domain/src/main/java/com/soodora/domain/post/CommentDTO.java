package com.soodora.domain.post;

import com.soodora.domain.user.UserDTO;

import java.util.Date;

public record CommentDTO(
        String slug,
        String postSlug,
        String content,
        UserDTO user,
        int likeCount,
        Date createdAt) {
}

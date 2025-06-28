package com.soodora.domain.post;

import com.soodora.domain.user.UserDTO;

import java.util.Date;

public record PostDTO(
        String slug,
        String title,
        String content,
        String imageUrl,
        String audioUrl,
        UserDTO owner,
        int likeCount,
        int repostCount,
        int commentCount,
        Date createdAt) {

}

package com.grooveconductor.domain.post;

import com.grooveconductor.domain.user.UserDTO;

public record PostDTO(
        String slug,
        String title,
        String content,
        String imageUrl,
        String audioUrl,
        PostType type,
        UserDTO owner,
        int likeCount,
        int commentCount) {

}

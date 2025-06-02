package com.grooveconductor.domain.post;

import com.grooveconductor.domain.user.UserDTO;
import java.util.Date;

public record CommentDTO(
        String slug,
        String postSlug,
        String content,
        UserDTO user,
        int likeCount,
        Date createdAt
) {}

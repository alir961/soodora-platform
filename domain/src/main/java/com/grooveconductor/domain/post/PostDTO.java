package com.grooveconductor.domain.post;

import com.grooveconductor.domain.user.UserDTO;

public record PostDTO(
        String slug,
        String title,
        String content,
        UserDTO owner) {

}

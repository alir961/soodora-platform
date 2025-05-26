package com.grooveconductor.domain.linkpage;

import com.grooveconductor.domain.link.LinkDTO;
import com.grooveconductor.domain.user.UserDTO;

import java.util.List;

public record LinkPageDTO(
        String slug,
        String title,
        String description,
        List<LinkDTO> links,
        UserDTO owner) {

}

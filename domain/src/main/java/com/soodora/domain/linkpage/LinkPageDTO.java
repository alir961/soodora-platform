package com.soodora.domain.linkpage;

import com.soodora.domain.link.LinkDTO;
import com.soodora.domain.user.UserDTO;

import java.util.List;

public record LinkPageDTO(
        String slug,
        String title,
        String description,
        List<LinkDTO> links,
        UserDTO owner) {

}

package com.soodora.domain.link;

import java.util.List;

public interface LinkRepository {

    LinkDTO save(CreateLinkCommand command, String userSlug);
    LinkDTO update(UpdateLinkCommand command, String slug);
    LinkDTO findBySlug(String slug);
    List<LinkDTO> findAll();
    void deleteBySlug(String slug);

}

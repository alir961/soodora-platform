package com.soodora.domain.link;

import java.util.List;

public interface LinkRepository {

    Link save(CreateLinkCommand command, String userSlug);
    Link update(UpdateLinkCommand command, String slug);
    Link findBySlug(String slug);
    List<Link> findAll();
    void deleteBySlug(String slug);

}

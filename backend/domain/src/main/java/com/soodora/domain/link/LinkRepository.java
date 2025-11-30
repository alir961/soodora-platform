package com.soodora.domain.link;

import com.soodora.domain.linkhub.LinkHub;

import java.util.List;

public interface LinkRepository {

    Link save(String artistSlug, CreateLinkCommand command);

    Link findBySlug(String linkSlug);
    List<Link> findAll();

    Link update(String linkSlug, UpdateLinkCommand command);

    void deleteBySlug(String linkSlug);

    LinkHub findLinkHubByLinkSlug(String linkSlug);

}

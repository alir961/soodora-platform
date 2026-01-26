package com.soodora.domain.linkhub;

import com.soodora.domain.user.User;

import java.util.List;

public interface LinkHubRepository {

    LinkHub save(User user, CreateLinkHubCommand command);

    LinkHub findBySlug(String linkHubSlug);
    List<LinkHub> findAll();

    LinkHub update(String linkHubSlug, UpdateLinkHubCommand command);

    void deleteBySlug(String linkHubSlug);

    void persistView(String linkHubSlug);

}

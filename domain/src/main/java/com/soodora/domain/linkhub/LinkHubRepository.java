package com.soodora.domain.linkhub;

import com.soodora.domain.user.User;

import java.util.List;

public interface LinkHubRepository {

    LinkHub save(CreateLinkHubCommand command, User user);
    LinkHub update(UpdateLinkHubCommand command, String slug);
    LinkHub findBySlug(String slug);
    List<LinkHub> findAll();
    void deleteBySlug(String slug);

    void persistView(String slug);

}

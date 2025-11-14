package com.soodora.domain.linkhub;

import com.soodora.domain.user.User;

import java.util.List;

public interface LinkHubRepository {

    LinkHubDTO save(CreateLinkHubCommand command, User user);
    LinkHubDTO update(UpdateLinkHubCommand command, String slug);
    LinkHubDTO findBySlug(String slug);
    List<LinkHubDTO> findAll();
    void deleteBySlug(String slug);

}

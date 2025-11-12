package com.soodora.domain.linkpage;

import com.soodora.domain.user.User;

import java.util.List;

public interface LinkPageRepository {

    LinkPageDTO save(CreateLinkPageCommand command, User user);
    LinkPageDTO update(UpdateLinkPageCommand command, String slug);
    LinkPageDTO findBySlug(String slug);
    List<LinkPageDTO> findAll();
    void deleteBySlug(String slug);

}

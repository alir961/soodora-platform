package com.grooveconductor.domain.linkpage;

import com.grooveconductor.domain.user.UserDTO;

import java.util.List;

public interface LinkPageRepository {

    LinkPageDTO save(CreateLinkPageCommand command, UserDTO user);
    LinkPageDTO update(UpdateLinkPageCommand command, String slug);
    LinkPageDTO findBySlug(String slug);
    List<LinkPageDTO> findAll();
    void deleteBySlug(String slug);

}

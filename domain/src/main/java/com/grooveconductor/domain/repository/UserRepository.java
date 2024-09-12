package com.grooveconductor.domain.repository;

import com.grooveconductor.domain.command.RegisterCommand;
import com.grooveconductor.domain.dto.UserDTO;

import java.util.List;

public interface UserRepository {

    UserDTO save(RegisterCommand command);
    UserDTO findBySlug(String slug);
    UserDTO findByEmail(String email);
    List<UserDTO> findAll();
    void deleteBySlug(String slug);
    boolean isSlugUsed(String slug);
    boolean isEmailUsed(String email);
    String retrieveEncodedPassword(String email);

}

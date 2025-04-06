package com.grooveconductor.domain.post;

import com.grooveconductor.domain.user.UserDTO;

import java.util.List;

public interface PostRepository {

    PostDTO save(CreatePostCommand command, UserDTO user);
    PostDTO find(String slug);
    List<PostDTO> findAll();
    void delete(String slug);

}

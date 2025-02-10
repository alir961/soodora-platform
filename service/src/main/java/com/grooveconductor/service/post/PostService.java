package com.grooveconductor.service.post;

import com.grooveconductor.domain.common.UnknownSlugException;
import com.grooveconductor.domain.post.CreatePostCommand;
import com.grooveconductor.domain.post.PostDTO;
import com.grooveconductor.domain.post.PostFilter;
import com.grooveconductor.domain.post.PostRepository;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.grooveconductor.domain.user.IdentifierFactory.createFromSlug;

@Service
@RequiredArgsConstructor
public class PostService {

  private final UserRepository userRepository;
  private final PostRepository postRepository;

  public void createPost(CreatePostCommand command, UserDTO user) {
    postRepository.save(command, user);
  }

  public List<PostDTO> retrieveAllPosts() {
    return postRepository.findAll();
  }

  public List<PostDTO> retrievePosts(PostFilter filter) {
    if (filter.userSlug().isPresent())
      return userRepository.retrievePosts(createFromSlug(filter.userSlug().get()));
    return retrieveAllPosts();
  }

  public PostDTO retrievePostBySlug(String slug) {
    return postRepository.find(slug);
  }

  public void deletePostBySlug(String slug) throws UnknownPostException {
    try {
      postRepository.delete(slug);
    } catch (UnknownSlugException exception) {
      throw new UnknownPostException(exception.getMessage());
    }
  }

}

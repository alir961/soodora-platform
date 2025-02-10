package com.grooveconductor.infrastructure.post;

import com.grooveconductor.domain.common.UnknownSlugException;
import com.grooveconductor.domain.post.CreatePostCommand;
import com.grooveconductor.domain.post.PostDTO;
import com.grooveconductor.domain.post.PostRepository;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.infrastructure.common.DomainEntity;
import com.grooveconductor.infrastructure.user.UserEntity;
import com.grooveconductor.infrastructure.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.grooveconductor.infrastructure.common.SlugGenerator.generateSlug;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {

  private final PostJpaRepository repository;
  private final UserJpaRepository userRepository;

  protected PostEntity findPostBySlugOrThrow(String slug) {
    return repository.findBySlug(slug)
        .orElseThrow(() -> new UnknownSlugException("Unable to find product with slug: #" + slug));
  }

  @Override
  public PostDTO save(CreatePostCommand command, UserDTO user) {
    PostEntity entity = new PostEntity();
    UserEntity userEntity = userRepository.findBySlug(user.slug())
        .orElseThrow(() -> new UnknownSlugException("Unable to find user #" + user.slug()));

    entity.setTitle(command.title());
    entity.setContent(command.content());
    entity.setSlug(generateUniqueSlug());
    entity.setUser(userEntity);
    return repository.save(entity).toDTO();
  }

  @Override
  public PostDTO find(String slug) {
    return findPostBySlugOrThrow(slug).toDTO();
  }

  @Override
  public List<PostDTO> findAll() {
    return repository.findAll().stream()
        .map(DomainEntity::toDTO)
        .toList();
  }

  @Override
  public void delete(String slug) {
    Optional<PostEntity> product = repository.findBySlug(slug);

    repository.deleteById(product.orElseThrow().getId());
  }

  private String generateUniqueSlug() {
    String slug = generateSlug();

    while (repository.findBySlug(slug).isPresent())
      slug = generateSlug();
    return slug;
  }

}

package com.soodora.infrastructure.user;

import com.soodora.infrastructure.post.PostEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findBySlug(String slug);

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    Page<UserEntity> findByUsernameContainingIgnoreCase(String username, Pageable pageable);

    @Query(value = "SELECT p.* FROM posts p JOIN likes l ON l.post_id = p.id JOIN users u ON l.user_id = u.id WHERE u.slug = :userSlug AND p.audio_url IS NOT NULL AND p.audio_url <> ''", nativeQuery = true)
    Optional<List<PostEntity>> findLikedPostsWithAudio(@Param("userSlug") String userSlug);

}

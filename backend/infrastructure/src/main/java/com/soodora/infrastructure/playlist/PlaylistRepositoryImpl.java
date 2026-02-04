package com.soodora.infrastructure.playlist;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.cdn.ImageStorageUploader;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.playlist.*;
import com.soodora.domain.post.Post;
import com.soodora.domain.user.User;
import com.soodora.infrastructure.common.DomainEntityAdapter;
import com.soodora.infrastructure.common.EntitySpecification;
import com.soodora.infrastructure.post.PostEntity;
import com.soodora.infrastructure.post.PostJpaRepository;
import com.soodora.infrastructure.user.UserEntity;
import com.soodora.infrastructure.user.UserJpaRepository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import static com.soodora.domain.common.SlugGenerator.generateSlug;
import static com.soodora.domain.common.StringValidator.isNotNullOrBlank;

@Repository
@RequiredArgsConstructor
public class PlaylistRepositoryImpl implements PlaylistRepository {

    private final PlaylistJpaRepository repository;
    private final UserJpaRepository userRepository;
    private final PostJpaRepository postRepository;
    private final ImageStorageUploader imageStorageUploader;

    @Override
    @Transactional
    public Playlist save(CreatePlaylistCommand command, User user, FileData playlistImage) {
        UserEntity userEntity = findUserBySlugOrThrow(user.getSlug());
        PlaylistEntity entity = new PlaylistEntity();

        entity.setName(command.name());
        entity.setDescription(command.description());
        entity.setPrivacy(command.privacy());
        entity.setSlug(generateUniqueSlug());
        entity.setUser(userEntity);

        if (playlistImage != null && playlistImage.content() != null) {
            String imageUrl = imageStorageUploader.upload(playlistImage);

            entity.setImageUrl(imageUrl);
        }

        return repository.save(entity).aggregate();
    }

    @Override
    @Transactional
    public Playlist update(UpdatePlaylistCommand command, String slug, FileData playlistImage) {
        PlaylistEntity existingPlaylist = findPlaylistBySlugOrThrow(slug);

        if (isNotNullOrBlank(command.name()))
            existingPlaylist.setName(command.name());
        if (isNotNullOrBlank(command.description()))
            existingPlaylist.setDescription(command.description());
        if (command.privacyState() != null)
            existingPlaylist.setPrivacy(command.privacyState());
        if (playlistImage != null && playlistImage.content() != null) {
            String imageUrl = imageStorageUploader.upload(playlistImage);

            existingPlaylist.setImageUrl(imageUrl);
        }

        return repository.save(existingPlaylist).aggregate();
    }

    @Override
    @Transactional
    public Playlist findBySlug(String slug) {
        return findPlaylistBySlugOrThrow(slug).aggregate();
    }

    @Override
    @Transactional
    public List<Playlist> findAll() {
        return repository.findAll().stream()
                .map(DomainEntityAdapter::aggregate)
                .toList();
    }

    @Override
    @Transactional
    public Page<Playlist> findByFilter(PlaylistFilter filter, Pageable pageable) {
        EntitySpecification<PlaylistEntity> playlistSpecification = new PlaylistSpecification(filter);

        return repository.findAll(playlistSpecification.build(), pageable)
                .map(DomainEntityAdapter::aggregate);
    }

    @Override
    @Transactional
    public boolean isPlaylistNameUsedByUser(String name, String userSlug) {
        UserEntity user = findUserBySlugOrThrow(userSlug);

        return user.getPlaylists().stream()
                .anyMatch(playlist -> playlist.getName().equalsIgnoreCase(name));
    }

    @Override
    @Transactional
    public Playlist addPostToPlaylist(Playlist playlist, String postSlug) {
        PlaylistEntity playlistEntity = findPlaylistBySlugOrThrow(playlist.getSlug());
        PostEntity post = findPostBySlugOrThrow(postSlug);

        playlistEntity.addPost(post);
        playlistEntity = repository.save(playlistEntity);
        return playlistEntity.aggregate();
    }

    @Override
    @Transactional
    public Playlist removePostFromPlaylist(Playlist playlist, String postSlug) {
        PlaylistEntity playlistEntity = findPlaylistBySlugOrThrow(playlist.getSlug());
        PostEntity post = findPostBySlugOrThrow(postSlug);

        playlistEntity.removePost(post);
        playlistEntity = repository.save(playlistEntity);
        return playlistEntity.aggregate();
    }

    @Override
    @Transactional
    public void deletePlaylistAndUnlinkPosts(String slug) {
        PlaylistEntity playlist = findPlaylistBySlugOrThrow(slug);

        playlist.clearPosts();
        repository.delete(playlist);
    }

    @Override
    @Transactional
    public Page<Post> findPlaylistPosts(String playlistSlug, Pageable pageable) {
        return postRepository
                .findByPlaylistSlug(playlistSlug, pageable)
                .map(DomainEntityAdapter::aggregate);
    }

    protected PlaylistEntity findPlaylistBySlugOrThrow(String slug) {
        return repository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find playlist with slug: #" + slug));
    }

    protected PostEntity findPostBySlugOrThrow(String slug) {
        return postRepository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find post with slug: #" + slug));
    }

    protected UserEntity findUserBySlugOrThrow(String slug) {
        return userRepository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find user with slug: #" + slug));
    }

    private String generateUniqueSlug() {
        String slug = generateSlug();

        while (repository.findBySlug(slug).isPresent())
            slug = generateSlug();
        return slug;
    }

}

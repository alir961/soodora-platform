package com.soodora.infrastructure.playlist;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.cdn.ImageStorageUploader;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.playlist.*;
import com.soodora.domain.post.PostDTO;
import com.soodora.domain.user.UserDTO;
import com.soodora.infrastructure.common.DomainEntity;
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
    public PlaylistDTO save(CreatePlaylistCommand command, UserDTO user, FileData playlistImage) {
        UserEntity userEntity = findUserBySlugOrThrow(user.slug());
        PlaylistEntity entity = new PlaylistEntity();

        entity.setName(command.name());
        entity.setDescription(command.description());
        entity.setPrivacy(command.privacy());
        if (playlistImage != null && playlistImage.content() != null) {
            String imageUrl = imageStorageUploader.upload(playlistImage);
            entity.setImageUrl(imageUrl);
        }
        entity.setSlug(generateUniqueSlug());
        entity.setUser(userEntity);

        return repository.save(entity).toDTO();
    }

    @Override
    @Transactional
    public PlaylistDTO update(UpdatePlaylistCommand command, String slug, FileData playlistImage) {
        PlaylistEntity existingPlaylist = findPlaylistBySlugOrThrow(slug);

        if (isNotNullOrBlank(command.name()))
            existingPlaylist.setName(command.name());
        if (isNotNullOrBlank(command.description()))
            existingPlaylist.setDescription(command.description());
        if (playlistImage != null && playlistImage.content() != null) {
            String imageUrl = imageStorageUploader.upload(playlistImage);
            existingPlaylist.setImageUrl(imageUrl);
        }
        if (command.privacyState() != null)
            existingPlaylist.setPrivacy(command.privacyState());

        return repository.save(existingPlaylist).toDTO();
    }

    @Override
    @Transactional
    public PlaylistFullDTO findBySlug(String slug, Pageable pageableAudioPosts) {
        return getPlaylistWithPosts(slug, pageableAudioPosts);
    }

    @Override
    @Transactional
    public List<PlaylistDTO> findAll() {
        return repository.findAll().stream()
                .map(DomainEntity::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public Page<PlaylistDTO> findByFilter(PlaylistFilter filter, Pageable pageable) {
        return repository.findAll(PlaylistSpecification.build(filter), pageable).map(PlaylistEntity::toDTO);
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
    public PlaylistFullDTO addPostToPlaylist(String playlistSlug, String postSlug, Pageable pageableAudioPosts) {
        PlaylistEntity playlist = findPlaylistBySlugOrThrow(playlistSlug);
        PostEntity post = findPostBySlugOrThrow(postSlug);

        playlist.addPost(post);
        repository.save(playlist);

        return playlist.toFullDTO(playlistPostsPage(playlistSlug, pageableAudioPosts));
    }

    @Override
    @Transactional
    public PlaylistFullDTO removePostFromPlaylist(String playlistSlug, String postSlug, Pageable pageableAudioPosts) {
        PlaylistEntity playlist = findPlaylistBySlugOrThrow(playlistSlug);
        PostEntity post = findPostBySlugOrThrow(postSlug);

        playlist.removePost(post);
        repository.save(playlist);

        return playlist.toFullDTO(playlistPostsPage(playlistSlug, pageableAudioPosts));
    }

    @Override
    @Transactional
    public void deletePlaylistAndUnlinkPosts(String slug) {
        PlaylistEntity playlist = findPlaylistBySlugOrThrow(slug);
        playlist.clearPosts();
        repository.delete(playlist);
    }

    @Transactional
    public PlaylistFullDTO getPlaylistWithPosts(String playlistSlug, Pageable pageableAudioPosts) {
        PlaylistEntity playlist = findPlaylistBySlugOrThrow(playlistSlug);

        return playlist.toFullDTO(playlistPostsPage(playlistSlug, pageableAudioPosts));
    }

    @Transactional
    public Page<PostDTO> playlistPostsPage(String playlistSlug, Pageable pageableAudioPosts) {
        return postRepository
                .findByPlaylistSlug(playlistSlug, pageableAudioPosts)
                .map(PostEntity::toDTO);
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

package com.soodora.api.post;

import com.soodora.domain.artist.ArtistDTO;
import com.soodora.domain.post.Post;

import java.util.Date;

public record PostDTO(
        String slug,
        String title,
        String content,
        String imageUrl,
        String audioTitle,
        String audioUrl,
        ArtistDTO owner,
        int likeCount,
        int repostCount,
        int commentCount,
        Date createdAt,
        Date updatedAt) {

    public static PostDTO build(Post post) {
        return new PostDTO(
                post.getSlug(),
                post.getTitle(),
                post.getContent(),
                post.getImageUrl(),
                post.getAudioTitle(),
                post.getAudioUrl(),
                ArtistDTO.build(post.getOwner()),
                post.getLikedUsers().size(),
                post.getRepostedUsers().size(),
                post.getCommentedUsers().size(),
                post.getCreatedAt(),
                post.getUpdatedAt());
    }

}

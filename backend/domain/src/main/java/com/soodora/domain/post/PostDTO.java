package com.soodora.domain.post;

import com.soodora.domain.artist.ArtistDTO;

import java.util.Date;

public record PostDTO(
        String slug,
        String title,
        String content,
        String imageUrl,
        String audioUrl,
        ArtistDTO owner,
        int likeCount,
        int repostCount,
        int commentCount,
        Date createdAt) {

}

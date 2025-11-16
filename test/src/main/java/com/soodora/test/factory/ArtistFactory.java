package com.soodora.test.factory;

import com.soodora.domain.artist.ArtistDTO;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ArtistFactory {

    public static ArtistDTO buildDefaultArtist(String id) {
        return new ArtistDTO(
                "slug" + id,
                "username" + id,
                "description",
                "avatarUrl");
    }

}

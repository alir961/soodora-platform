package com.grooveconductor.test.factory;

import com.grooveconductor.domain.artist.ArtistDTO;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ArtistFactory {

    public static ArtistDTO buildArtistDTO(String id) {
        return new ArtistDTO(
                "slug" + id,
                "name" + id,
                "description",
                "imageUrl"
        );
    }

}

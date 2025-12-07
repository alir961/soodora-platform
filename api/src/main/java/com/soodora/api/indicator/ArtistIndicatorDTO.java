package com.soodora.api.indicator;

import com.soodora.domain.indicator.ArtistIndicator;

public record ArtistIndicatorDTO(
        int artists,
        int premiumArtists,
        int artistsWithoutFollower,
        int artistsWithoutPost,
        int artistsWithoutAudio,
        int artistsWithoutSoodoraKit,
        int artistsWithoutLinkHub) {

    public static ArtistIndicatorDTO build(ArtistIndicator artistIndicator) {
        return new ArtistIndicatorDTO(
                artistIndicator.getArtists(),
                artistIndicator.getPremiumArtists(),
                artistIndicator.getArtistsWithoutFollower(),
                artistIndicator.getArtistsWithoutPost(),
                artistIndicator.getArtistsWithoutAudio(),
                artistIndicator.getArtistsWithoutMediaKit(),
                artistIndicator.getArtistsWithoutLinkHub());
    }

}

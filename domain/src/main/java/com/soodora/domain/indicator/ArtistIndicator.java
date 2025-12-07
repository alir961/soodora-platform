package com.soodora.domain.indicator;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ArtistIndicator {

    private int artists;
    private int premiumArtists;
    private int artistsWithoutFollower;
    private int artistsWithoutPost;
    private int artistsWithoutAudio;
    private int artistsWithoutProduct;
    private int artistsWithoutLinkHub;
    private int artistsWithoutMediaKit;

}

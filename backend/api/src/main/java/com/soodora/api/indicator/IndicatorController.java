package com.soodora.api.indicator;

import com.soodora.domain.indicator.*;
import com.soodora.service.indicator.IndicatorService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/indicators")
@PreAuthorize("hasRole('ADMIN')")
public class IndicatorController implements IndicatorResource {

    private final IndicatorService indicatorService;

    @GetMapping("/artists")
    public ArtistIndicatorDTO getArtistsIndicators() {
        ArtistIndicator artistIndicators = indicatorService.computeArtistIndicators();

        return ArtistIndicatorDTO.build(artistIndicators);
    }

    @GetMapping("/posts")
    public PostIndicatorDTO getPostsIndicators() {
        PostIndicator indicators = indicatorService.computePostIndicators();

        return PostIndicatorDTO.build(indicators);
    }

    @GetMapping("/products")
    public ProductIndicatorDTO getProductsIndicators() {
        ProductIndicator indicators = indicatorService.computeProductIndicators();

        return ProductIndicatorDTO.build(indicators);
    }

    @GetMapping("/link-hubs")
    public LinkHubIndicatorDTO getLinkHubsIndicators() {
        LinkHubIndicator indicators = indicatorService.computeLinkHubIndicators();

        return LinkHubIndicatorDTO.build(indicators);
    }

    @GetMapping("/media-kits")
    public MediaKitIndicatorDTO getMediaKitsIndicators() {
        MediaKitIndicator indicators = indicatorService.computeMediaKitIndicators();

        return MediaKitIndicatorDTO.build(indicators);
    }

}

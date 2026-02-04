package com.soodora.api.indicator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Indicator Controller")
@SecurityRequirement(name = "bearerAuth")
public interface IndicatorResource {

    @Operation(summary = "Get Artist Indicators", description = "Computes and returns metrics like total artists, premium artists, and artists without content/followers. Requires ADMIN role.", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved artist indicators", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ArtistIndicatorDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have the ADMIN role")
    })
    ArtistIndicatorDTO getArtistsIndicators();

    @Operation(summary = "Get Post Indicators", description = "Computes and returns metrics like total posts, audio posts, total engagement (likes/comments/reposts), and posts without engagement. Requires ADMIN role.", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved post indicators", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostIndicatorDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have the ADMIN role")
    })
    PostIndicatorDTO getPostsIndicators();

    @Operation(summary = "Get Product Indicators", description = "Computes and returns metrics like total products, out-of-stock count, total revenue, average price, and average products per artist. Requires ADMIN role.", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved product indicators", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductIndicatorDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have the ADMIN role")
    })
    ProductIndicatorDTO getProductsIndicators();

    @Operation(summary = "Get Link Hub Indicators", description = "Computes and returns metrics like total link hubs, total links, and cumulative view counts across all link hubs. Requires ADMIN role.", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved Link Hub indicators", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LinkHubIndicatorDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have the ADMIN role")
    })
    LinkHubIndicatorDTO getLinkHubsIndicators();

    @Operation(summary = "Get Media Kit Indicators", description = "Computes and returns metrics like total media kits, total items across all kits, and cumulative download counts. Requires ADMIN role.", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved Media Kit indicators", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MediaKitIndicatorDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have the ADMIN role")
    })
    MediaKitIndicatorDTO getMediaKitsIndicators();

    @Operation(summary = "Get User Retention Rate", description = "Computes and returns global user retention rate. Requires ADMIN role.", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved retention rate", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Double.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have the ADMIN role")
    })
    double getRetentionRate();

}

package com.soodora.api.linkhub;

import com.soodora.api.common.ExceptionResponse;
import com.soodora.api.common.JsonResponse;
import com.soodora.domain.user.User;
import com.soodora.service.link.CreateLinkRequest;
import com.soodora.service.link.UpdateLinkRequest;
import com.soodora.service.linkhub.CreateLinkHubRequest;
import com.soodora.service.linkhub.UpdateLinkHubRequest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Link Hub Controller", description = "Manage LinkHub pages and links.")
public interface LinkHubResource {

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a LinkHub", description = "Creates a LinkHub for the authenticated user. Returns the generated slug.", responses = {
            @ApiResponse(responseCode = "201", description = "LinkHub created"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    ResponseEntity<JsonResponse> createLinkHub(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @RequestBody CreateLinkHubRequest request);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update a LinkHub", description = "Updates an existing LinkHub identified by its slug.", responses = {
            @ApiResponse(responseCode = "200", description = "LinkHub updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "LinkHub not found")
    })
    ResponseEntity<JsonResponse> updateLinkHub(
            @Parameter(description = "Slug of the LinkHub") @PathVariable String slug,
            @RequestBody UpdateLinkHubRequest request);

    @Operation(summary = "Get LinkHub by user slug", description = "Returns the LinkHub for a given user slug. If no slug is provided, returns the authenticated user's LinkHub.", responses = {
            @ApiResponse(responseCode = "200", description = "LinkHub returned")
    })
    LinkHubDTO getUserLinkHub(
            @Parameter(description = "Slug of the user owning the LinkHub") @RequestParam(value = "userSlug", required = false) String userSlug);

    @Operation(summary = "Get a LinkHub by slug", responses = {
            @ApiResponse(responseCode = "200", description = "LinkHub returned"),
            @ApiResponse(responseCode = "404", description = "LinkHub not found")
    })
    LinkHubDTO getLinkHub(
            @Parameter(description = "Slug of the LinkHub") @PathVariable String slug);

    @Operation(summary = "Get all LinkHubs", responses = {
            @ApiResponse(responseCode = "200", description = "List of LinkHubs")
    })
    List<LinkHubDTO> getAllLinkHubs();

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete a LinkHub", responses = {
            @ApiResponse(responseCode = "204", description = "LinkHub deleted"),
            @ApiResponse(responseCode = "404", description = "LinkHub not found")
    })
    void deleteLinkHub(@Parameter(description = "Slug of the LinkHub") @PathVariable String slug);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Add a link to a LinkHub", description = "Creates a new link inside the LinkHub of the authenticated user.", responses = {
            @ApiResponse(responseCode = "201", description = "Link created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "LinkHub not found for user")
    })
    ResponseEntity<JsonResponse> addLink(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @PathVariable String hubSlug,
            @RequestBody CreateLinkRequest request);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update a link", responses = {
            @ApiResponse(responseCode = "200", description = "Link updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Link not found")
    })
    ResponseEntity<JsonResponse> updateLink(
            @PathVariable String hubSlug,
            @PathVariable String linkSlug,
            @RequestBody UpdateLinkRequest request);

    @Operation(summary = "Get all links", responses = {
            @ApiResponse(responseCode = "200", description = "List of links")
    })
    List<LinkDTO> getAllLinks();

    @Operation(summary = "Get a link by slug", responses = {
            @ApiResponse(responseCode = "200", description = "Link returned"),
            @ApiResponse(responseCode = "404", description = "Link not found")
    })
    LinkDTO getLinkBySlug(
            @PathVariable String hubSlug,
            @PathVariable String linkSlug);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete a link", responses = {
            @ApiResponse(responseCode = "204", description = "Link deleted"),
            @ApiResponse(responseCode = "404", description = "Link not found")
    })
    void deleteLink(
            @PathVariable String hubSlug,
            @PathVariable String linkSlug);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get analytics for a LinkHub", description = "Only the LinkHub owner or an admin can access analytics.", responses = {
            @ApiResponse(responseCode = "200", description = "Analytics returned"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "LinkHub not found")
    })
    LinkHubAnalyticsDTO getLinkHubAnalytics(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @PathVariable String hubSlug);

}

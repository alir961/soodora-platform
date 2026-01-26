package com.soodora.api.event;

import com.soodora.api.common.ExceptionResponse;
import com.soodora.api.common.JsonResponse;
import com.soodora.domain.user.User;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Event Controller")
public interface EventResource {

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create event for logged artist", responses = {
            @ApiResponse(responseCode = "201", description = "Event created", content = @Content(schema = @Schema(implementation = EventDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden, user not allowed to create event", content = @Content)
    })
    ResponseEntity<JsonResponse> createEvent(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @RequestBody(required = true) SaveEventRequest request);

    @Operation(summary = "Retrieve filtered events", responses = {
            @ApiResponse(responseCode = "200", description = "Events retrieved", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = EventDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Artist with that slug cannot be found", content = @Content)
    })
    List<EventDTO> retrieveFilteredEvents(@RequestParam(required = false) String artistSlug);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update event from given slug", responses = {
            @ApiResponse(responseCode = "200", description = "Event updated", content = @Content(schema = @Schema(implementation = EventDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden, user not allowed to update this event", content = @Content),
            @ApiResponse(responseCode = "404", description = "Event with that slug cannot be found", content = @Content)
    })
    ResponseEntity<JsonResponse> updateEvent(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @PathVariable String eventSlug,
            @RequestBody(required = true) SaveEventRequest request);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete event from given slug", responses = {
            @ApiResponse(responseCode = "200", description = "Event deleted"),
            @ApiResponse(responseCode = "403", description = "Forbidden, user not allowed to update this event"),
            @ApiResponse(responseCode = "404", description = "Event with that slug cannot be found")
    })
    void deleteEvent(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @PathVariable String eventSlug);

}

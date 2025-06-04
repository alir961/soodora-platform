package com.grooveconductor.api.event;

import com.grooveconductor.domain.event.EventDTO;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.service.event.CreateEventRequest;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Event Controller")
@SecurityRequirement(name = "bearerAuth")
public interface EventResource {

    @Operation(summary = "Create event for logged artist", responses = {
            @ApiResponse(responseCode = "201", description = "Event created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "403", description = "User must be artist")
    })
    ResponseEntity<?> createEvent(
            UserDTO user,
            @RequestBody(required = true) CreateEventRequest request);

    @Operation(summary = "Retrieve filtered events", responses = {
            @ApiResponse(responseCode = "200", description = "Events retrieved", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = EventDTO.class)))),
            @ApiResponse(responseCode = "204", description = "No events retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "403", description = "Forbidden request")
    })
    ResponseEntity<?> getEvents(
            String artistSlug);

    @Operation(summary = "Retrieve logged artist events", responses = {
            @ApiResponse(responseCode = "200", description = "Events retrieved", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = EventDTO.class)))),
            @ApiResponse(responseCode = "403", description = "User must be artist")
    })
    ResponseEntity<?> getAuthenticatedArtistEvents(
            UserDTO user);

}

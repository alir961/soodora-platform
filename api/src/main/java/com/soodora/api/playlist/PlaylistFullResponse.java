package com.soodora.api.playlist;

import com.soodora.api.common.JsonResponse;
import com.soodora.domain.playlist.PopulatedPlaylistDTO;

public record PlaylistFullResponse(PopulatedPlaylistDTO playlist) implements JsonResponse {

}

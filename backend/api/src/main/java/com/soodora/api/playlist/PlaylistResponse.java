package com.soodora.api.playlist;

import com.soodora.api.common.JsonResponse;
import com.soodora.domain.playlist.PlaylistDTO;

public record PlaylistResponse(PlaylistDTO playlistDTO) implements JsonResponse {

}

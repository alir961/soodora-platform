package com.soodora.api.playlist;

import com.soodora.api.common.JsonResponse;
import com.soodora.domain.playlist.PlaylistFullDTO;

public record PlaylistFullResponse(PlaylistFullDTO playlistFullDTO) implements JsonResponse {

}

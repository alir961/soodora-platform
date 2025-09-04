package com.soodora.api.mediakit;

import com.soodora.api.common.JsonResponse;
import com.soodora.domain.mediakit.MediaKitDTO;

public record MediaKitResponse(MediaKitDTO mediaKitDTO) implements JsonResponse {

}

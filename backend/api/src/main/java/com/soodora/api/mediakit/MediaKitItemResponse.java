package com.soodora.api.mediakit;

import com.soodora.api.common.JsonResponse;
import com.soodora.domain.mediakititem.MediaKitItemDTO;

public record MediaKitItemResponse(MediaKitItemDTO mediaKitItemDTO) implements JsonResponse {

}

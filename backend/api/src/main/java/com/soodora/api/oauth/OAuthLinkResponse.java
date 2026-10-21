package com.soodora.api.oauth;

import com.soodora.api.common.JsonResponse;

public record OAuthLinkResponse(String url) implements JsonResponse {

}

package com.soodora.domain.post;

public record CreatePostCommand(String title, String content, String audioUrl) {
}

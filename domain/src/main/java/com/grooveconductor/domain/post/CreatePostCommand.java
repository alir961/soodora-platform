package com.grooveconductor.domain.post;

public record CreatePostCommand(String title, String content, String imageUrl, String audioUrl) {
}

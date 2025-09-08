package com.soodora.domain.cdn;

public record FileData(byte[] content, String filename, String contentType) {

}

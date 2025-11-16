package com.soodora.domain.quote;

public record CreateQuoteCommand(
        String name,
        String clientName,
        String clientEmail,
        String clientAddress,
        String clientPhone,
        String description) {
}

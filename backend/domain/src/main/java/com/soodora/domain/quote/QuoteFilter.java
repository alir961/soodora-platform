package com.soodora.domain.quote;

import java.util.Optional;

public record QuoteFilter(Optional<String> userSlug, Optional<String> name, Optional<String> description,
        Optional<String> clientName) {

    public boolean isEmpty() {
        return userSlug().isEmpty()
                && name.isEmpty()
                && description.isEmpty()
                && clientName.isEmpty();
    }

}

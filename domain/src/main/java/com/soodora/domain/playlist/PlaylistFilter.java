package com.soodora.domain.playlist;

import java.util.Optional;

public record PlaylistFilter(Optional<String> userSlug, Optional<String> name,
        Optional<String> description) {

    public boolean isEmpty() {
        return userSlug.isEmpty()
                && name.isEmpty()
                && description.isEmpty();
    }

}

package com.soodora.domain.playlist;

import java.util.Optional;

public record PlaylistFilter(Optional<String> userSlug, Optional<String> title,
        Optional<String> description) {

    public boolean isEmpty() {
        return userSlug.isEmpty()
                && title.isEmpty()
                && description.isEmpty();
    }

}

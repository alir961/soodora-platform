package com.soodora.domain.post;

import java.util.Optional;

public record PostFilter(Optional<String> userSlug, Optional<Boolean> hasAudio, Optional<String> title,
        Optional<String> audioTitle,
        Optional<String> description) {

    public boolean isEmpty() {
        return userSlug.isEmpty()
                && hasAudio.isEmpty()
                && title.isEmpty()
                && audioTitle.isEmpty()
                && description.isEmpty();
    }

}

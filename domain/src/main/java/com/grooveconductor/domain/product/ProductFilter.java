package com.grooveconductor.domain.product;

import java.util.Optional;

public record ProductFilter(Optional<String> userSlug, Optional<String> name, Optional<String> description,
        Optional<String> category) {

    public boolean isEmpty() {
        return userSlug().isEmpty()
                && name.isEmpty()
                && description.isEmpty()
                && category.isEmpty();
    }

}

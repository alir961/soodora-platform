package com.soodora.infrastructure.playlist;

import com.soodora.domain.playlist.PlaylistFilter;
import com.soodora.infrastructure.common.EntitySpecification;

import org.springframework.data.jpa.domain.Specification;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlaylistSpecification extends EntitySpecification<PlaylistEntity> {

    private final PlaylistFilter filter;

    public Specification<PlaylistEntity> build() {
        Specification<PlaylistEntity> specification = notPrivate();

        if (filter.userSlug().isPresent())
            specification = hasUserSlug(filter.userSlug().get());
        if (filter.name().isPresent())
            specification = append(specification, hasName(filter.name().get()));
        if (filter.description().isPresent())
            specification = append(specification, hasDescription(filter.description().get()));
        return specification;
    }

    private static Specification<PlaylistEntity> hasUserSlug(String slug) {
        return (root, query, cb) -> cb.equal(root.get("user").get("slug"), slug);
    }

    private static Specification<PlaylistEntity> hasName(String name) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    private static Specification<PlaylistEntity> hasDescription(String description) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%");
    }

    private static Specification<PlaylistEntity> notPrivate() {
        return (root, query, cb) -> cb.notEqual(root.get("privacy"), 1);
    }

}

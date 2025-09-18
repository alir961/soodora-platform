package com.soodora.infrastructure.playlist;

import com.soodora.domain.playlist.PlaylistFilter;

import org.springframework.data.jpa.domain.Specification;

public class PlaylistSpecification {

    public static Specification<PlaylistEntity> build(PlaylistFilter filter) {
        Specification<PlaylistEntity> spec = null;

        if (filter.userSlug().isPresent())
            spec = appendSpec(spec, hasUserSlug(filter.userSlug().get()));

        if (filter.name().isPresent())
            spec = appendSpec(spec, hasName(filter.name().get()));

        if (filter.description().isPresent())
            spec = appendSpec(spec, hasDescription(filter.description().get()));

        return spec;
    }

    private static Specification<PlaylistEntity> appendSpec(Specification<PlaylistEntity> base,
            Specification<PlaylistEntity> addition) {
        if (base == null) {
            return addition;
        }
        return base.and(addition);
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

}

package com.soodora.infrastructure.playlist;

import com.soodora.domain.playlist.PlaylistFilter;

import org.springframework.data.jpa.domain.Specification;

public class PlaylistSpecification {

    public static Specification<PlaylistEntity> build(PlaylistFilter filter) {
        Specification<PlaylistEntity> spec = null;

        if (filter.userSlug().isPresent())
            spec = appendSpec(spec, hasUserSlug(filter.userSlug().get()));

        if (filter.title().isPresent())
            spec = appendSpec(spec, hasTitle(filter.title().get()));

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

    private static Specification<PlaylistEntity> hasTitle(String title) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    private static Specification<PlaylistEntity> hasDescription(String description) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("content")), "%" + description.toLowerCase() + "%");
    }

}

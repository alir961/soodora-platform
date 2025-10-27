package com.soodora.infrastructure.playlist;

import com.soodora.domain.playlist.PlaylistFilter;

import org.springframework.data.jpa.domain.Specification;

public class PlaylistSpecification {

    public static Specification<PlaylistEntity> build(PlaylistFilter filter) {
        Specification<PlaylistEntity> spec;

        if (filter.userSlug().isPresent())
            spec = hasUserSlug(filter.userSlug().get());
        else
            spec = notPrivate();

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

    private static Specification<PlaylistEntity> notPrivate() {
        return (root, query, cb) -> cb.notEqual(root.get("privacy"), 1);
    }

}

package com.grooveconductor.infrastructure.post;

import com.grooveconductor.domain.post.PostFilter;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Expression;

public class PostSpecification {

    public static Specification<PostEntity> build(PostFilter filter) {
        Specification<PostEntity> spec = null;

        if (filter.userSlug().isPresent())
            spec = appendSpec(spec, hasUserSlug(filter.userSlug().get()));

        if (filter.hasAudio().isPresent())
            spec = appendSpec(spec, hasAudio(filter.hasAudio().get()));

        if (filter.title().isPresent())
            spec = appendSpec(spec, hasTitle(filter.title().get()));

        if (filter.description().isPresent())
            spec = appendSpec(spec, hasDescription(filter.description().get()));

        return spec;
    }

    private static Specification<PostEntity> appendSpec(Specification<PostEntity> base,
            Specification<PostEntity> addition) {
        if (base == null) {
            return addition;
        }
        return base.and(addition);
    }

    private static Specification<PostEntity> hasUserSlug(String slug) {
        return (root, query, cb) -> cb.equal(root.get("user").get("slug"), slug);
    }

    private static Specification<PostEntity> hasAudio(Boolean hasAudio) {
        return (root, query, cb) -> {
            Expression<String> audioUrl = root.get("audioUrl");

            if (hasAudio) {
                return cb.and(
                        cb.isNotNull(audioUrl),
                        cb.notEqual(cb.trim(audioUrl), ""));
            } else {
                return cb.or(
                        cb.isNull(audioUrl),
                        cb.equal(cb.trim(audioUrl), ""));
            }
        };
    }

    private static Specification<PostEntity> hasTitle(String title) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    private static Specification<PostEntity> hasDescription(String description) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("content")), "%" + description.toLowerCase() + "%");
    }
}

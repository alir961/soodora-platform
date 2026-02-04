package com.soodora.infrastructure.post;

import com.soodora.domain.post.PostFilter;
import com.soodora.infrastructure.common.EntitySpecification;

import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Expression;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PostSpecification extends EntitySpecification<PostEntity> {

    private final PostFilter filter;

    @Override
    public Specification<PostEntity> build() {
        Specification<PostEntity> specification = null;
        Optional<String> userSlug = filter.userSlug();
        Optional<Boolean> hasAudio = filter.hasAudio();
        Optional<String> title = filter.title();
        Optional<String> audioTitle = filter.audioTitle();
        Optional<String> description = filter.description();

        if (userSlug.isPresent())
            specification = append(null, hasUserSlug(userSlug.get()));
        if (hasAudio.isPresent())
            specification = append(specification, hasAudio(hasAudio.get()));
        if (title.isPresent())
            specification = append(specification, hasTitle(title.get()));
        if (audioTitle.isPresent())
            specification = append(specification, hasAudioTitle(audioTitle.get()));
        if (description.isPresent())
            specification = append(specification, hasDescription(description.get()));
        return specification;
    }

    private static Specification<PostEntity> hasUserSlug(String slug) {
        return (root, query, cb) -> cb.equal(root.get("user").get("slug"), slug);
    }

    private static Specification<PostEntity> hasAudio(boolean hasAudio) {
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

    private static Specification<PostEntity> hasAudioTitle(String audioTitle) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("audioTitle")), "%" + audioTitle.toLowerCase() + "%");
    }

    private static Specification<PostEntity> hasDescription(String description) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("content")), "%" + description.toLowerCase() + "%");
    }

}

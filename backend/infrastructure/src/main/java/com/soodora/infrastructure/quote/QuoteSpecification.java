package com.soodora.infrastructure.quote;

import com.soodora.domain.quote.QuoteFilter;

import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class QuoteSpecification {

    public static Specification<QuoteEntity> build(QuoteFilter filter) {
        Specification<QuoteEntity> specification = null;
        Optional<String> userSlugFilter = filter.userSlug();
        Optional<String> nameFilter = filter.name();
        Optional<String> descriptionFilter = filter.description();
        Optional<String> clientNameFilter = filter.clientName();

        if (userSlugFilter.isPresent())
            specification = append(specification, hasUserSlug(userSlugFilter.get()));
        if (nameFilter.isPresent())
            specification = append(specification, hasName(nameFilter.get()));
        if (descriptionFilter.isPresent())
            specification = append(specification, hasDescription(descriptionFilter.get()));
        if (clientNameFilter.isPresent())
            specification = append(specification, hasClientName(clientNameFilter.get()));
        return specification;
    }

    private static Specification<QuoteEntity> append(Specification<QuoteEntity> base,
            Specification<QuoteEntity> addition) {
        return base == null ? addition : base.and(addition);
    }

    private static Specification<QuoteEntity> hasUserSlug(String slug) {
        return (root, query, cb) -> cb.equal(root.get("artist").get("user").get("slug"), slug);
    }

    private static Specification<QuoteEntity> hasName(String name) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    private static Specification<QuoteEntity> hasDescription(String description) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%");
    }

    private static Specification<QuoteEntity> hasClientName(String clientName) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("clientName")), "%" + clientName.toLowerCase() + "%");
    }

}

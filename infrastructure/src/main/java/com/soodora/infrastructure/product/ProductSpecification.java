package com.soodora.infrastructure.product;

import com.soodora.domain.product.ProductFilter;
import com.soodora.infrastructure.common.EntitySpecification;

import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;

import lombok.RequiredArgsConstructor;

import static com.soodora.domain.product.ProductStatus.ACTIVE;

@RequiredArgsConstructor
public class ProductSpecification extends EntitySpecification<ProductEntity> {

    private final ProductFilter filter;

    public Specification<ProductEntity> build() {
        Specification<ProductEntity> specification = isActive();
        Optional<String> userSlugFilter = filter.userSlug();
        Optional<String> nameFilter = filter.name();
        Optional<String> descriptionFilter = filter.description();
        Optional<String> categoryFilter = filter.category();

        if (userSlugFilter.isPresent())
            specification = append(specification, hasUserSlug(userSlugFilter.get()));
        if (nameFilter.isPresent())
            specification = append(specification, hasName(nameFilter.get()));
        if (descriptionFilter.isPresent())
            specification = append(specification, hasDescription(descriptionFilter.get()));
        if (categoryFilter.isPresent())
            specification = append(specification, hasCategory(categoryFilter.get()));
        return specification;
    }

    private static Specification<ProductEntity> hasUserSlug(String slug) {
        return (root, query, cb) -> cb.equal(root.get("vendor").get("user").get("slug"), slug);
    }

    private static Specification<ProductEntity> hasName(String name) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    private static Specification<ProductEntity> hasDescription(String description) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%");
    }

    private static Specification<ProductEntity> hasCategory(String category) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get("category")), category.toLowerCase());
    }

    private static Specification<ProductEntity> isActive() {
        return (root, query, cb) -> cb.equal(root.get("status"), ACTIVE);
    }
}

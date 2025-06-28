package com.soodora.infrastructure.product;

import com.soodora.domain.product.ProductFilter;

import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {

    public static Specification<ProductEntity> build(ProductFilter filter) {
        Specification<ProductEntity> spec = null;

        if (filter.userSlug().isPresent())
            spec = appendSpec(spec, hasUserSlug(filter.userSlug().get()));

        if (filter.name().isPresent())
            spec = appendSpec(spec, hasName(filter.name().get()));

        if (filter.description().isPresent())
            spec = appendSpec(spec, hasDescription(filter.description().get()));

        if (filter.category().isPresent())
            spec = appendSpec(spec, hasCategory(filter.category().get()));

        return spec;
    }

    private static Specification<ProductEntity> appendSpec(Specification<ProductEntity> base,
            Specification<ProductEntity> addition) {
        return base == null ? addition : base.and(addition);
    }

    private static Specification<ProductEntity> hasUserSlug(String slug) {
        return (root, query, cb) -> cb.equal(root.get("user").get("slug"), slug);
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

}

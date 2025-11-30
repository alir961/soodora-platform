package com.soodora.infrastructure.common;

import org.springframework.data.jpa.domain.Specification;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@NoArgsConstructor(access = PROTECTED)
public abstract class EntitySpecification<T> {

    public abstract Specification<T> build();

    protected Specification<T> append(Specification<T> base, Specification<T> addition) {
        if (base == null)
            return addition;
        return base.and(addition);
    }

}

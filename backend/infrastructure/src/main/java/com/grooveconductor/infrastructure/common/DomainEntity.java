package com.grooveconductor.infrastructure.common;

public interface DomainEntity<T> {

    T toDTO();

}

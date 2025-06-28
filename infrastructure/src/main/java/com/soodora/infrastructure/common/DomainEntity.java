package com.soodora.infrastructure.common;

public interface DomainEntity<T> {

    T toDTO();

}

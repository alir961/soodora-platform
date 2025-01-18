package com.grooveconductor.service.common;

public interface Request<T> {

    T toCommand() throws InvalidRequestException;

}

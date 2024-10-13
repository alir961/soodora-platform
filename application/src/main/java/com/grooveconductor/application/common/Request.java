package com.grooveconductor.application.common;

public interface Request<T> {

    T toCommand() throws InvalidRequestException;

}

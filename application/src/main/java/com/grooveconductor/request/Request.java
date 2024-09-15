package com.grooveconductor.request;

import com.grooveconductor.exception.InvalidRequestException;

public interface Request<T> {

    T toCommand() throws InvalidRequestException;

}

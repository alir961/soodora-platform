package com.soodora.service.common;

public interface Request<T> {

    T toCommand() throws InvalidRequestException;

}

package com.grooveconductor.request;

public interface Request<T> {

    T toCommand();

}

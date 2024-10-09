package com.grooveconductor.rest;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonStringify {

    public static String asJsonString(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

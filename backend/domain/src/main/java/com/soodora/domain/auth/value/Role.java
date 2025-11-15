package com.soodora.domain.auth.value;

public record Role(String value) {

    public Role(String value) {
        this.value = value.replaceFirst("ROLE_", "");
    }

    public String completeRole() {
        return "ROLE_" + value;
    }

}

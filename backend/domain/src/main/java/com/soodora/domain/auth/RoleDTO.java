package com.soodora.domain.auth;

public record RoleDTO(Long id, String name) {

    public boolean isArtistRole() {
        return name.equals("ROLE_ARTIST") || name.equals("ROLE_PREMIUM");
    }

}

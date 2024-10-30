package com.grooveconductor.domain.user;

import lombok.Getter;

import java.util.Optional;

@Getter
public class UpdateUserCommand {
    private final String username;
    private final String firstname;
    private final String lastname;

  public UpdateUserCommand(
      Optional<String> username,
      Optional<String> firstname,
      Optional<String> lastname
  ) {

    this.username = username.orElse(null);
    this.firstname = firstname.orElse(null);
    this.lastname = lastname.orElse(null);
  }
}

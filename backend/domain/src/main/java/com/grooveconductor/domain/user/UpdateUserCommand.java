package com.grooveconductor.domain.user;

import lombok.Getter;

@Getter
public class UpdateUserCommand {
    private final String username;
    private final String firstname;
    private final String lastname;

  public UpdateUserCommand(
      String username,
      String firstname,
      String lastname
  ) {

    this.username = username;
    this.firstname = firstname;
    this.lastname = lastname;
  }
}

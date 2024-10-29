package com.grooveconductor.application.user;

import com.grooveconductor.application.common.InvalidRequestException;
import com.grooveconductor.application.common.Request;
import com.grooveconductor.domain.user.UpdateUserCommand;

public record UpdateUserRequest(String username, String firstname, String lastname) implements Request<UpdateUserCommand> {

  @Override
  public UpdateUserCommand toCommand() throws InvalidRequestException {
    if (username == null) throw new InvalidRequestException("User name is not valid");
    if (firstname == null) throw new InvalidRequestException("Firstname is not valid");
    if (lastname == null) throw new InvalidRequestException("Lastname is not valid");

    return new UpdateUserCommand(username, firstname, lastname);
  }

}

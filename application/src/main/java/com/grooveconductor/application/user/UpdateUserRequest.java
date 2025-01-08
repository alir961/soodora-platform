package com.grooveconductor.application.user;

import com.grooveconductor.application.common.InvalidRequestException;
import com.grooveconductor.application.common.Request;
import com.grooveconductor.domain.user.UpdateUserCommand;

public record UpdateUserRequest(
    String username,
    String firstname,
    String lastname
) implements Request<UpdateUserCommand> {

  @Override
  public UpdateUserCommand toCommand() throws InvalidRequestException {

    return new UpdateUserCommand(
        username,
        firstname,
        lastname);
  }

}

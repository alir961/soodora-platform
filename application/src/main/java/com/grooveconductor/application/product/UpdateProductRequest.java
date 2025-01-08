package com.grooveconductor.application.product;

import com.grooveconductor.application.common.Request;
import com.grooveconductor.domain.product.UpdateProductCommand;

public record UpdateProductRequest(
    String name,
    String description,
    String imageUrl,
    String category,
    Long price,
    Long quantity
) implements Request<UpdateProductCommand> {

  @Override
  public UpdateProductCommand toCommand() {
    return new UpdateProductCommand(
        name,
        description,
        imageUrl,
        category,
        price,
        quantity);
  }

}

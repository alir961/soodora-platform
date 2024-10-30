package com.grooveconductor.domain.product;

import lombok.Getter;

import java.util.Optional;

@Getter
public class UpdateProductCommand {
    private final String name;
    private final String description;
    private final String imageUrl;
    private final String category;
    private final long price;
    private final long quantity;

  public UpdateProductCommand(
      Optional<String> name,
      Optional<String> description,
      Optional<String> imageUrl,
      Optional<String> category,
      Optional<Long> price,
      Optional<Long> quantity
  ) {

    this.name = name.orElse(null);
    this.description = description.orElse(null);
    this.imageUrl = imageUrl.orElse(null);
    this.category = category.orElse(null);
    this.price = price.orElse(0L);
    this.quantity = quantity.orElse(0L);
  }

}

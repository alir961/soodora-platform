CREATE TABLE products
(
    id            UUID          NOT NULL,
    slug          VARCHAR(255)  NULL,
    name          VARCHAR(255)  NULL,
    description   VARCHAR(255)  NULL,
    image_url     VARCHAR(5000) NULL,
    category      VARCHAR(255)  NULL,
    price         BIGINT        NULL,
    quantity      BIGINT        NULL,
    CONSTRAINT pk_products PRIMARY KEY (id)
);

ALTER TABLE products
    ADD CONSTRAINT uc_products_slug UNIQUE (slug);
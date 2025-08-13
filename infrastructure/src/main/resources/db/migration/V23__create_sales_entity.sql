CREATE TABLE sales (
    id UUID NOT NULL,
    slug VARCHAR(255) UNIQUE NOT NULL,
    product_id UUID NOT NULL,
    artist_id UUID NOT NULL,
    buyer_id UUID NOT NULL,
    quantity_sold BIGINT NOT NULL,
    amount NUMERIC(15, 2) NOT NULL,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    CONSTRAINT pk_sales PRIMARY KEY (id)
);

ALTER TABLE sales
    ADD CONSTRAINT FK_SALES_ON_PRODUCT
        FOREIGN KEY (product_id) REFERENCES products(id);

ALTER TABLE sales
    ADD CONSTRAINT FK_SALES_ON_ARTIST
        FOREIGN KEY (artist_id) REFERENCES artists(id);

ALTER TABLE sales
    ADD CONSTRAINT FK_SALES_ON_USER
        FOREIGN KEY (buyer_id) REFERENCES users(id);

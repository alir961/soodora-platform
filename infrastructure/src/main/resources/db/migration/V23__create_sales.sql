CREATE TABLE sales (
                       id UUID NOT NULL,
                       slug VARCHAR(255) UNIQUE NOT NULL,
                       product_id UUID NOT NULL,
                       user_id UUID NOT NULL,
                       quantity_sold BIGINT NOT NULL,
                       amount NUMERIC(15, 2) NOT NULL,
                       created_at TIMESTAMP NULL,
                       updated_at TIMESTAMP NULL,
                       CONSTRAINT pk_sales PRIMARY KEY (id)
);

ALTER TABLE sales
    ADD CONSTRAINT fk_sales_product
        FOREIGN KEY (product_id) REFERENCES products(id);

ALTER TABLE sales
    ADD CONSTRAINT fk_sales_user
        FOREIGN KEY (user_id) REFERENCES users(id);

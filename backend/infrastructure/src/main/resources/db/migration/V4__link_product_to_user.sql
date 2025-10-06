ALTER TABLE products
    ADD user_id       UUID NULL,
    ADD created_at    datetime NULL,
    ADD updated_at    datetime NULL;

ALTER TABLE products
    ADD CONSTRAINT FK_PRODUCTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE products
    ADD user_slug     VARCHAR(255);
    ADD user_id       UUID;
    ADD created_at    datetime;
    ADD updated_at    datetime;

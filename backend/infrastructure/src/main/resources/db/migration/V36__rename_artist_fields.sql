ALTER TABLE products
    RENAME COLUMN artist_id TO vendor_id;

ALTER TABLE sales
    RENAME COLUMN artist_id TO vendor_id;

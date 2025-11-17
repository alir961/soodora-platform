ALTER TABLE products DROP FOREIGN KEY FK_PRODUCTS_ON_USER;
ALTER TABLE posts DROP FOREIGN KEY FK_POSTS_ON_USER;
ALTER TABLE link_pages DROP FOREIGN KEY FK_LINK_PAGES_USER;

ALTER TABLE products ADD COLUMN artist_id UUID;
ALTER TABLE posts ADD COLUMN artist_id UUID;
ALTER TABLE link_pages ADD COLUMN artist_id UUID;

UPDATE products
    JOIN artists ON products.user_id = artists.user_id
    SET products.artist_id = artists.id;

UPDATE posts
    JOIN artists ON posts.user_id = artists.user_id
    SET posts.artist_id = artists.id;

UPDATE link_pages
    JOIN artists ON link_pages.user_id = artists.user_id
    SET link_pages.artist_id = artists.id;

ALTER TABLE posts DROP COLUMN user_id;
ALTER TABLE link_pages DROP COLUMN user_id;
ALTER TABLE products DROP COLUMN user_id;

ALTER TABLE products
    ADD CONSTRAINT FK_PRODUCTS_ON_ARTIST FOREIGN KEY (artist_id) REFERENCES artists(id);

ALTER TABLE posts
    ADD CONSTRAINT FK_POSTS_ON_ARTIST FOREIGN KEY (artist_id) REFERENCES artists(id);

ALTER TABLE link_pages
    ADD CONSTRAINT FK_LINK_PAGES_ON_ARTIST FOREIGN KEY (artist_id) REFERENCES artists(id);

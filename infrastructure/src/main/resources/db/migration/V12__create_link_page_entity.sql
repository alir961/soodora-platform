CREATE TABLE link_pages
(
    id          UUID         NOT NULL,
    slug        VARCHAR(255) NOT NULL,
    title       VARCHAR(255) NULL,
    description VARCHAR(255) NULL,
    user_id     UUID         NULL,
    created_at  datetime     NULL,
    updated_at  datetime     NULL,
    CONSTRAINT pk_link_pages PRIMARY KEY (id)
);

ALTER TABLE link_pages
    ADD CONSTRAINT uc_link_pages_slug UNIQUE (slug);

ALTER TABLE link_pages
    ADD CONSTRAINT FK_LINK_PAGES_USER FOREIGN KEY (user_id) REFERENCES users (id);
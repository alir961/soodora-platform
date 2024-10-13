CREATE TABLE playlists
(
    id            UUID          NOT NULL,
    slug          VARCHAR(255)  NULL,
    name          VARCHAR(255)  NULL,
    description   VARCHAR(255)  NULL,
    state         TINYINT(255)  NULL,
    image_url     VARCHAR(5000) NULL,
    user_id       UUID          NULL,
    created_at    datetime      NULL,
    updated_at    datetime      NULL,
    CONSTRAINT pk_playlists PRIMARY KEY (id)
);

ALTER TABLE playlists
    ADD CONSTRAINT uc_playlists_slug UNIQUE (slug);

ALTER TABLE playlists
    ADD CONSTRAINT FK_PLAYLISTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);
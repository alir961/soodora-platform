CREATE TABLE artists
(
    id            UUID         NOT NULL,
    slug          VARCHAR(255) NULL,
    name          VARCHAR(255) NOT NULL,
    description   VARCHAR(255) NULL,
    image_url     VARCHAR(255) NULL,
    created_at    datetime     NULL,
    updated_at    datetime     NULL,
    user_id       UUID         NULL,
    CONSTRAINT pk_artists PRIMARY KEY (id)
);

ALTER TABLE users
    ADD COLUMN artist_id  BINARY(16)   NULL;

ALTER TABLE artists
    ADD CONSTRAINT uc_artists_name UNIQUE (name);

ALTER TABLE artists
    ADD CONSTRAINT uc_artists_slug UNIQUE (slug);

ALTER TABLE artists
    ADD CONSTRAINT uc_artists_user UNIQUE (user_id);

ALTER TABLE artists
    ADD CONSTRAINT FK_ARTISTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE users
    ADD CONSTRAINT uc_users_artist UNIQUE (artist_id);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_ARTIST FOREIGN KEY (artist_id) REFERENCES artists (id);
CREATE TABLE users
(
    id         UUID         NOT NULL,
    slug       VARCHAR(255) NOT NULL,
    username   VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    firstname  VARCHAR(255) NULL,
    lastname   VARCHAR(255) NULL,
    created_at datetime     NULL,
    updated_at datetime     NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uc_users_slug UNIQUE (slug);

ALTER TABLE users
    ADD CONSTRAINT uc_users_username UNIQUE (username);
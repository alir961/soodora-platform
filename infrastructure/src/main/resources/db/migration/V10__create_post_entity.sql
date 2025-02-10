CREATE TABLE posts
(
    id            UUID          NOT NULL,
    slug          VARCHAR(255)  NULL,
    title         VARCHAR(255)  NULL,
    content       VARCHAR(5000) NULL,
    user_id       UUID          NULL,
    created_at    datetime      NULL,
    updated_at    datetime      NULL,
    CONSTRAINT pk_posts PRIMARY KEY (id)
);

ALTER TABLE posts
    ADD CONSTRAINT uc_posts_slug UNIQUE (slug);

ALTER TABLE posts
    ADD CONSTRAINT FK_POSTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);
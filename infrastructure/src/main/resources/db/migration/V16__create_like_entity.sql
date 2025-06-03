CREATE TABLE likes
(
    id          UUID         NOT NULL,
    slug        VARCHAR(255) NOT NULL,
    post_id     UUID         NULL,
    comment_id  UUID         NULL,
    user_id     UUID         NOT NULL,
    liked_at    TIMESTAMP    NULL,
    CONSTRAINT pk_likes PRIMARY KEY (id)
);

ALTER TABLE likes
    ADD CONSTRAINT uc_likes_slug UNIQUE (slug);

ALTER TABLE likes
    ADD CONSTRAINT FK_LIKES_POST FOREIGN KEY (post_id) REFERENCES posts (id);

ALTER TABLE likes
    ADD CONSTRAINT FK_LIKES_COMMENT FOREIGN KEY (comment_id) REFERENCES comments (id);

ALTER TABLE likes
    ADD CONSTRAINT FK_LIKES_USER FOREIGN KEY (user_id) REFERENCES users (id);
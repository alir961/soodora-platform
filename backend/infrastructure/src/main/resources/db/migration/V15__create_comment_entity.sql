CREATE TABLE comments
(
    id         UUID          NOT NULL,
    slug       VARCHAR(255)  NOT NULL,
    content    VARCHAR(2000) NOT NULL,
    like_count INT           NOT NULL,
    post_id    UUID          NOT NULL,
    user_id    UUID          NOT NULL,
    created_at TIMESTAMP     NULL,
    CONSTRAINT pk_comments PRIMARY KEY (id)
);

ALTER TABLE comments
    ADD CONSTRAINT uc_comments_slug UNIQUE (slug);

ALTER TABLE comments
    ADD CONSTRAINT FK_COMMENTS_POST FOREIGN KEY (post_id) REFERENCES posts (id);

ALTER TABLE comments
    ADD CONSTRAINT FK_COMMENTS_USER FOREIGN KEY (user_id) REFERENCES users (id);
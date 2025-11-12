CREATE TABLE oauth_tokens
(
    id            UUID   NOT NULL,
    provider      TINYINT     NULL,
    refresh_token VARCHAR(255) NULL,
    user_id       UUID   NULL,
    CONSTRAINT pk_oauth_tokens PRIMARY KEY (id)
);

ALTER TABLE oauth_tokens
    ADD CONSTRAINT uc_oauth_tokens_user UNIQUE (user_id);

ALTER TABLE oauth_tokens
    ADD CONSTRAINT FK_OAUTH_TOKENS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);
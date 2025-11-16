TRUNCATE TABLE oauth_tokens;

ALTER TABLE oauth_tokens
    ADD external_id VARCHAR(255) NOT NULL;

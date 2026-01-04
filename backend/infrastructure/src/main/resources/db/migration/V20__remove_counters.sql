ALTER TABLE posts
    DROP COLUMN like_count,
    DROP COLUMN comment_count;

ALTER TABLE comments
    DROP COLUMN like_count;
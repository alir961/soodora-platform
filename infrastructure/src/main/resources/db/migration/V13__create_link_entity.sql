CREATE TABLE links
(
    id           UUID          NOT NULL,
    slug         VARCHAR(255)  NOT NULL,
    title        VARCHAR(255)  NOT NULL,
    description  VARCHAR(255)  NULL,
    url          VARCHAR(5000) NULL,
    icon_url     VARCHAR(5000) NULL,
    link_page_id UUID          NOT NULL,
    created_at   datetime      NULL,
    updated_at   datetime      NULL,
    CONSTRAINT pk_links PRIMARY KEY (id)
);

ALTER TABLE links
    ADD CONSTRAINT uc_links_slug UNIQUE (slug);

ALTER TABLE links
    ADD CONSTRAINT FK_LINKS_LINK_PAGE FOREIGN KEY (link_page_id) REFERENCES link_pages (id);
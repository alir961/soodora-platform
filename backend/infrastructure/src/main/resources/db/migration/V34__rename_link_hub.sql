ALTER TABLE link_pages
    RENAME TO link_hubs;

ALTER TABLE links
    RENAME COLUMN link_page_id TO link_hub_id;
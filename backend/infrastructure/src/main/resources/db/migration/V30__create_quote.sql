CREATE TABLE quotes (
                        id UUID NOT NULL PRIMARY KEY,
                        slug VARCHAR(255) UNIQUE NOT NULL,
                        artist_id UUID NOT NULL,
                        name VARCHAR(255),
                        client_name VARCHAR(255),
                        client_email VARCHAR(255),
                        client_address VARCHAR(255),
                        client_phone VARCHAR(50),
                        description VARCHAR(255),
                        amount DECIMAL(19,2),
                        currency TINYINT(255),
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE quotes
    ADD CONSTRAINT FK_QUOTES_ON_ARTIST FOREIGN KEY (artist_id) REFERENCES artists(id);

CREATE TABLE quote_articles (
                                id UUID NOT NULL PRIMARY KEY,
                                slug VARCHAR(255) UNIQUE NOT NULL,
                                quote_id UUID,
                                description VARCHAR(255) NOT NULL,
                                quantity INT NOT NULL,
                                amount DECIMAL(19,2),
                                currency TINYINT(255),
                                vat_percent DECIMAL(5,2) NOT NULL,
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE quote_articles
    ADD CONSTRAINT FK_QUOTE_ARTICLES_ON_QUOTE FOREIGN KEY (quote_id) REFERENCES quotes(id);

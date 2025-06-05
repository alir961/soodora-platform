INSERT INTO roles
    (id, name)
VALUES (1, 'ROLE_ARTIST'),
       (2, 'ROLE_ARTIST_PREMIUM'),
       (3, 'ROLE_ADMIN');

SET @passwordHash = '$2a$12$VjK/1IYRaKjy00dJPZzdHukwVvCAe/wt.Pjg2O692R2ldp./m13DS';
SET @artist1Id = UUID();
SET @artist2Id = UUID();
SET @adminId = UUID();
SET @event1Id = UUID();
SET @event2Id = UUID();
SET @event2Id = UUID();
SET @product1Id = UUID();
SET @product2Id = UUID();

INSERT INTO users
(id, slug, username, email, password, firstname, lastname, created_at, updated_at, artist_id)
VALUES (UUID(), 'user1', 'User1', 'user1@example.com', @passwordHash, 'Alain', 'Térieur', NOW(), NOW(), NULL),
       (UUID(), 'user2', 'User2', 'user2@example.com', @passwordHash, 'Emma', 'Gination', NOW(), NOW(), NULL),
       (UUID(), 'user3', 'User3', 'user3@example.com', @passwordHash, 'Jean', 'Bonnot', NOW(), NOW(), NULL),
       (@artist1Id, 'artist1', 'Artist1', 'artist1@example.com', @passwordHash, 'Tagada', 'Jones', NOW(), NOW(), NULL),
       (@artist2Id, 'artist2', 'Artist2', 'artist2@example.com', @passwordHash, 'Annie', 'Versaire', NOW(), NOW(), NULL),
       (@adminId, 'admin', 'Admin', 'admin@example.com', @passwordHash, 'Guy', 'Delette', NOW(), NOW(), NULL);

INSERT INTO users_roles
    (user_id, role_id)
VALUES (@artist1Id, 1),
       (@artist2Id, 2),
       (@adminId, 3);

INSERT INTO artists
(id, slug, name, description, image_url, created_at, updated_at, user_id)
VALUES (@artist1Id, 'artist1', 'DJ Boom Boom',
        'DJ spécialiste des reprises éléctro de variété française des années 80/90',
        'https://cdn-images.dzcdn.net/images/artist/805b1a07188b0404ac8765a07877bddd/1900x1900-000000-80-0-0.jpg',
        NOW(), NOW(), @artist1Id),
       (@artist2Id, 'artist2', 'Artist2', 'Billets pour la prochaine tournée disponible dans mes liens !',
        'https://www.francetvinfo.fr/pictures/oE5A3HnRw-wl8bY2jhbi1ftcvis/1200x1200/2024/04/09/sipa-shutterstock41141126-000081-6614f5d99d4b7204128043.jpg',
        NOW(), NOW(), @artist1Id);

INSERT INTO events
(id, slug, type, title, description, url, thumbnail_url, address, date_time)
VALUES (@event1Id, 'event1', 'FESTIVAL', 'Festival de Jazz',
        'Festival spécialisé dans les musiques jazz et variété du monde',
        'https://www.offi.fr/tendances/concerts/les-festivals-de-jazz-de-lete-2024-562.html',
        'https://www.tourismwinnipeg.com/uploads/sv_images/simpleview-image-upload-c-fit-w-800-h-600-crm-winnipeg-DD3-45350-cc457c07-5056-a36a-0915e4728ccacd5f.t1637075796.jpg',
        '', NOW()),
       (@event2Id, 'event2', 'CONCERT', 'Foire au beauf',
        'Concert de variété française à la foire au beauf',
        'https://www.instagram.com/beauffestival/?hl=en',
        'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSulwXYMx1BesK5ROwJmBJekDdx9viHo-u92Q&s',
        '', NOW());

INSERT INTO artists_events
    (artist_id, event_id)
VALUES (@artist1Id, @event1Id);

INSERT INTO products
(id, slug, name, description, image_url, category, price, quantity, user_id, created_at, updated_at)
VALUES (@product1Id, 'product1', 'Casquette Annie Versaire',
        'Casquette fabriqué dans un pays en développement pour un coût inférieur à salaire minimum',
        'https://www.cdiscount.com/pdt2/8/0/7/1/700x700/auc9016110168807/rw/casquette-casquette-camouflage-donald-trump-casque.jpg',
        'Couvre-chef', 13.99, 100, @artist1Id, NOW(), NOW()),
       (@product2Id, 'product2', 'Pull Annie Versaire',
        'Pull fabriqué dans un pays en développement pour un coût inférieur à salaire minimum',
        'https://media.cdnws.com/_i/124991/609/1078/10/coucou-perenoel.jpeg',
        'Pull', 59.99, 50, @artist1Id, NOW(), NOW());

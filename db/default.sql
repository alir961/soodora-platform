INSERT INTO roles
    (id, name)
VALUES (1, 'ROLE_ARTIST'),
       (2, 'ROLE_PREMIUM'),
       (3, 'ROLE_ADMIN');

SET @passwordHash = '$2a$12$VjK/1IYRaKjy00dJPZzdHukwVvCAe/wt.Pjg2O692R2ldp./m13DS';

SET @user1Id = UUID();
SET @user2Id = UUID();
SET @user3Id = UUID();
SET @artist1Id = UUID();
SET @artist2Id = UUID();
SET @artist3Id = UUID();
SET @artist4Id = UUID();
SET @artist5Id = UUID();
SET @artist6Id = UUID();
SET @artist7Id = UUID();
SET @artist8Id = UUID();
SET @adminId = UUID();

SET @post1Id = UUID();
SET @post2Id = UUID();
SET @post3Id = UUID();
SET @post4Id = UUID();
SET @post5Id = UUID();
SET @post6Id = UUID();
SET @post7Id = UUID();
SET @post8Id = UUID();
SET @post9Id = UUID();
SET @post10Id = UUID();
SET @post11Id = UUID();
SET @post12Id = UUID();
SET @post13Id = UUID();
SET @post14Id = UUID();
SET @post15Id = UUID();
SET @post16Id = UUID();
SET @post17Id = UUID();
SET @post18Id = UUID();
SET @post19Id = UUID();
SET @post20Id = UUID();
SET @post21Id = UUID();
SET @post22Id = UUID();
SET @post23Id = UUID();
SET @post24Id = UUID();
SET @post25Id = UUID();
SET @post26Id = UUID();
SET @post27Id = UUID();
SET @post28Id = UUID();
SET @post29Id = UUID();
SET @post30Id = UUID();
SET @post31Id = UUID();
SET @post32Id = UUID();
SET @post33Id = UUID();
SET @post34Id = UUID();
SET @post35Id = UUID();
SET @post36Id = UUID();
SET @post37Id = UUID();
SET @post38Id = UUID();
SET @post39Id = UUID();
SET @post40Id = UUID();
SET @post41Id = UUID();
SET @post42Id = UUID();
SET @post43Id = UUID();
SET @post44Id = UUID();
SET @post45Id = UUID();
SET @post46Id = UUID();
SET @post47Id = UUID();
SET @post48Id = UUID();
SET @post49Id = UUID();
SET @post50Id = UUID();

SET @audioUrl = 'https://file-examples.com/storage/feaa6a7f0468517af9bc02d/2017/11/file_example_MP3_700KB.mp3';

INSERT INTO users
(id, slug, username, email, password, firstname, lastname, created_at, updated_at, artist_id, description, avatar_url)
VALUES (@user1Id, 'user1', 'oliver.smith', 'oliver.smith@example.com', @passwordHash, 'Oliver', 'Smith', NOW(), NOW(),
        NULL, '', NULL),
       (@user2Id, 'user2', 'emma.jones', 'emma.jones@example.com', @passwordHash, 'Emma', 'Jones', NOW(), NOW(),
        NULL, '', NULL),
       (@user3Id, 'user3', 'liam.wilson', 'liam.wilson@example.com', @passwordHash, 'Liam', 'Wilson', NOW(), NOW(), NULL,
        '', NULL),
       (@artist1Id, 'artist1', 'sky.leaf', 'sky.leaf@example.com', @passwordHash, 'Sky', 'Leaf', NOW(), NOW(), NULL,
        'Skyleaf blends ambient textures with organic rhythms inspired by trip-hop. Their sonic universe drifts between daydream and urban tension, shaped by a cinematic and singular identity.',
        'https://cdn-images.dzcdn.net/images/artist/805b1a07188b0404ac8765a07877bddd/1900x1900-000000-80-0-0.jpg'),
       (@artist2Id, 'artist2', 'leah.g', 'leah.g23@example.com', @passwordHash, 'Leah', 'Green', NOW(), NOW(), NULL,
        'With her smooth voice and chill, soul-infused beats, Leah G. turns intimate stories into groove-driven gems. Her music explores the soft melancholy of modern life with emotional clarity.',
        'https://www.francetvinfo.fr/pictures/oE5A3HnRw-wl8bY2jhbi1ftcvis/1200x1200/2024/04/09/sipa-shutterstock41141126-000081-6614f5d99d4b7204128043.jpg'),
       (@artist3Id, 'artist3', 'soundshaper93', 'maya.rhythm@example.com', @passwordHash, 'Maya', 'Johnson', NOW(),
        NOW(), NULL,
        'Maya crafts immersive soundscapes that blend field recordings, modular synths, and digital textures. Based in Berlin, her work explores the intersection of nature and machine, often layering hypnotic drones with subtle melodic fragments. Perfect for headphone journeys.',
        NULL),
       (@artist4Id, 'artist4', 'dubwavez', 'kevin.dubois@example.com', @passwordHash, 'Kevin', 'Dubois', NOW(), NOW(),
        NULL,
        'Coming straight outta Lyon’s underground dub scene, Kevin is a live dub operator who builds heavy riddims with analog delays, spring reverbs, and deep basslines. Known for his militant steppa grooves and custom-built sound system, he brings the fire to every session.',
        NULL),
       (@artist5Id, 'artist5', 'synthfox', 'lena.kimura@example.com', @passwordHash, 'Lena', 'Kimura', NOW(), NOW(),
        NULL,
        'Lena blends retro-futuristic synths with dreamy vocals and chill beats. A Tokyo-based bedroom producer, she creates nostalgic vibes inspired by 90s anime, arcade aesthetics, and neon cityscapes. Think glitchy love letters from a parallel timeline.',
        NULL),
       (@artist6Id, 'artist6', 'loop.nova', 'arjun.rao@example.com', @passwordHash, 'Arjun', 'Rao', NOW(), NOW(), NULL,
        'Arjun is a modular techno live performer pushing the boundaries of rhythmic structure and sonic texture. Based in Bangalore, he’s known for hypnotic club sets and a strict no-laptop policy. His tracks build like rituals—precise, meditative, and powerful.',
        NULL),
       (@artist7Id, 'artist7', 'glitchmuse', 'sara.fernandez@example.com', @passwordHash, 'Sara', 'Fernandez', NOW(),
        NOW(), NULL,
        'Sara manipulates granular samples and chaotic algorithms to craft emotionally complex electronic pieces. Classically trained and experimentally unchained, she performs in art galleries and underground raves alike. Expect fractured rhythms and haunting sound design.',
        NULL),
       (@artist8Id, 'artist8', 'bass.monarch', 'theo.schmidt@example.com', @passwordHash, 'Theo', 'Schmidt', NOW(),
        NOW(), NULL,
        'Theo brings surgical precision and raw energy to the DnB scene. Hailing from Hamburg, his neurofunk sets are relentless—full of dark atmospheres, twisted reese basslines, and glitchy break edits. A true technician of the low-end spectrum.',
        NULL),
       (@adminId, 'admin', 'adrien.f', 'adrien.f@example.com', @passwordHash, 'Adrien', 'Foster', NOW(), NOW(), NULL,
        'God of the application', NULL);

INSERT INTO users_roles
    (user_id, role_id)
VALUES (@artist1Id, 1),
       (@artist2Id, 1),
       (@artist3Id, 1),
       (@artist4Id, 1),
       (@artist5Id, 1),
       (@artist6Id, 2),
       (@artist7Id, 2),
       (@artist8Id, 2),
       (@adminId, 3);

INSERT INTO artists
(id, slug, name, description, image_url, created_at, updated_at, user_id)
VALUES (@artist1Id, 'artist1', 'sky.leaf', '', '', NOW(), NOW(), @artist1Id),
       (@artist2Id, 'artist2', 'leah.g', '', '', NOW(), NOW(), @artist2Id),
       (@artist3Id, 'artist3', 'soundshaper93', '', '', NOW(), NOW(), @artist3Id),
       (@artist4Id, 'artist4', 'dubwavez', '', '', NOW(), NOW(), @artist4Id),
       (@artist5Id, 'artist5', 'synthfox', '', '', NOW(), NOW(), @artist5Id),
       (@artist6Id, 'artist6', 'loop.nova', '', '', NOW(), NOW(), @artist6Id),
       (@artist7Id, 'artist7', 'glitchmuse', '', '', NOW(), NOW(), @artist7Id),
       (@artist8Id, 'artist8', 'bass.monarch', '', '', NOW(), NOW(), @artist8Id);

INSERT INTO posts
(id, slug, title, content, image_url, audio_url, user_id, created_at, updated_at)
VALUES (@post1Id, '7Ql1jUpT', 'Diving into Dub FX', 'This might end up on the next project. Thoughts?', '', '',
        @artist1Id, NOW(), NOW()),
       (@post2Id, 'NSqrKaZM', 'Trying Out a New Sound', 'Testing a new effect chain. Might keep it minimal.', '',
        @audioUrl,
        @artist2Id, NOW() - INTERVAL 1 HOUR, NOW()),
       (@post3Id, 'jtSvJ1CE', 'Patch of the Day', 'Feels like a soundtrack to a dream.', '', '', @artist3Id,
        NOW() - INTERVAL 2 HOUR,
        NOW()),
       (@post4Id, 'BlIXm3OQ', 'Quick Beat Sketch', 'I love the groove in this one. Simple but effective.', '',
        @audioUrl,
        @artist4Id, NOW() - INTERVAL 3 HOUR, NOW()),
       (@post5Id, 'T8O2hEfq', 'Feedback Welcome!', 'Inspired by old school soul records this week.', '', '', @artist5Id,
        NOW() - INTERVAL 4 HOUR, NOW()),
       (@post6Id, 'g56QuOyZ', 'Just Vibes', 'Still in rough form, but this could be something.', '', '', @artist6Id,
        NOW() - INTERVAL 5 HOUR, NOW()),
       (@post7Id, 'ZyoE0cyd', 'Back to the Basics',
        'You ever make something and not know if it’s good or not? That’s this.', '', '', @artist7Id,
        NOW() - INTERVAL 6 HOUR, NOW()),
       (@post8Id, '20G1DkZn', 'Just Vibes', 'Late night vibes in the studio. No sleep, just sounds.', '', @audioUrl,
        @artist8Id, NOW() - INTERVAL 7 HOUR, NOW()),
       (@post9Id, 'Dy5ZqxU0', 'Patch of the Day', 'Looped this part for hours, it\'s stuck in my head now.', '', '',
        @artist1Id, NOW() - INTERVAL 8 HOUR, NOW()),
       (@post10Id, 'JQJXAP6d', 'Feeling Inspired', 'Still in rough form, but this could be something.', '', @audioUrl,
        @artist2Id, NOW() - INTERVAL 9 HOUR, NOW()),
       (@post11Id, 'ZhiX4UXD', 'Feeling Inspired', 'Just having fun with the gear today. Unfiltered jam.', '', '',
        @artist3Id, NOW() - INTERVAL 10 HOUR, NOW()),
       (@post12Id, 'Z4Gwv7Pi', 'Late Night Mixdown', 'Testing a new effect chain. Might keep it minimal.', '', '',
        @artist4Id, NOW() - INTERVAL 11 HOUR, NOW()),
       (@post13Id, 'YAUPTT0z', 'Feedback Welcome!', 'I love the groove in this one. Simple but effective.', '',
        @audioUrl,
        @artist5Id, NOW() - INTERVAL 12 HOUR, NOW()),
       (@post14Id, '6ETA5cMU', 'Studio Session Recap', 'Feels like a soundtrack to a dream.', '', '', @artist6Id,
        NOW() - INTERVAL 13 HOUR,
        NOW()),
       (@post15Id, 'NlMDqE5L', 'Sneak Peek – Upcoming Drop', 'Not sure what genre this is but I dig it.', '', '',
        @artist7Id, NOW() - INTERVAL 14 HOUR, NOW()),
       (@post16Id, 'JpMIJA0N', 'This One Hits Different', 'Testing a new effect chain. Might keep it minimal.', '', '',
        @artist8Id, NOW() - INTERVAL 15 HOUR, NOW()),
       (@post17Id, 'vHgI47y4', 'Full Track or Nah?', 'Inspired by old school soul records this week.', '', @audioUrl,
        @artist1Id, NOW() - INTERVAL 16 HOUR, NOW()),
       (@post18Id, 'wztfVJWm', 'Analog Warmth',
        'You ever make something and not know if it’s good or not? That’s this.', '', '', @artist2Id,
        NOW() - INTERVAL 17 HOUR, NOW()),
       (@post19Id, 'DFlJTUYF', 'What Do You Think of This?', 'Late night vibes in the studio. No sleep, just sounds.',
        '', '', @artist3Id, NOW() - INTERVAL 18 HOUR, NOW()),
       (@post20Id, 'aBTOEnN1', 'Digital Chaos', 'Wasn\'t planning to record today... then this happened.', '', '',
        @artist4Id, NOW() - INTERVAL 19 HOUR, NOW()),
       (@post21Id, '4sJUh3YL', 'A Loop Becomes a Story', 'Ambient noise or actual song? You decide.', '', '',
        @artist5Id, NOW() - INTERVAL 20 HOUR, NOW()),
       (@post22Id, 'JU8w27Ur', 'What Do You Think of This?', 'Love how this texture came out, super spacey.', '', '',
        @artist6Id, NOW() - INTERVAL 21 HOUR, NOW()),
       (@post23Id, 'TNmsxdTs', 'Patch of the Day', 'Love how this texture came out, super spacey.', '', '', @artist7Id,
        NOW() - INTERVAL 22 HOUR, NOW()),
       (@post24Id, 'Y8wiLc5Y', 'New Collab Coming Soon', 'Testing a new effect chain. Might keep it minimal.', '',
        @audioUrl,
        @artist8Id, NOW() - INTERVAL 23 HOUR, NOW()),
       (@post25Id, 'y6S9BLTB', 'Patch of the Day', 'Real instruments hitting different lately.', '', '', @artist1Id,
        NOW() - INTERVAL 1 DAY, NOW()),
       (@post26Id, 'tLUv3iTs', 'Patch of the Day', 'Looped this part for hours, it\'s stuck in my head now.', '', '',
        @artist2Id, NOW() - INTERVAL 2 DAY, NOW()),
       (@post27Id, 'NCeHAd5g', 'B-side Energy', 'Real instruments hitting different lately.', '', '', @artist3Id,
        NOW() - INTERVAL 3 DAY,
        NOW()),
       (@post28Id, 'nQUQ6sav', 'Raw Vocal Take', 'I love the groove in this one. Simple but effective.', '', '',
        @artist4Id, NOW() - INTERVAL 4 DAY, NOW()),
       (@post29Id, 'L5y1rY6d', 'What Do You Think of This?', 'Still in rough form, but this could be something.',
        @audioUrl,
        '', @artist5Id, NOW() - INTERVAL 5 DAY, NOW()),
       (@post30Id, '3a12kq9D', 'Sound Experiment', 'Real instruments hitting different lately.', '', '', @artist6Id,
        NOW() - INTERVAL 6 DAY, NOW()),
       (@post31Id, 'oLLzayQp', 'New Gear = New Sound', 'Not sure what genre this is but I dig it.', '', '', @artist7Id,
        NOW() - INTERVAL 1 WEEK, NOW()),
       (@post32Id, '1r6qS3np', 'What Do You Think of This?', 'Looped this part for hours, it\'s stuck in my head now.',
        '', '', @artist8Id, NOW() - INTERVAL 2 WEEK, NOW()),
       (@post33Id, 'bKgzT7au', 'B-side Energy', 'Real instruments hitting different lately.', '', @audioUrl, @artist1Id,
        NOW() - INTERVAL 3 WEEK,
        NOW()),
       (@post34Id, 'K28Whaxh', 'Does This Even Work?', 'Inspired by old school soul records this week.', '', '',
        @artist2Id, NOW() - INTERVAL 1 MONTH, NOW()),
       (@post35Id, 'E2HLInqK', 'Groove Check', 'Feels like a soundtrack to a dream.', '', '', @artist3Id,
        NOW() - INTERVAL 2 MONTH, NOW()),
       (@post36Id, 'wfgsrHNc', 'New Gear = New Sound', 'Testing a new effect chain. Might keep it minimal.', '', '',
        @artist4Id, NOW() - INTERVAL 3 MONTH, NOW()),
       (@post37Id, 't0V6abEd', 'Sound Experiment', 'Just having fun with the gear today. Unfiltered jam.', '', '',
        @artist5Id, NOW() - INTERVAL 4 MONTH, NOW()),
       (@post38Id, 'Z8yh1hIR', 'What Genre Is This Even?', 'Love how this texture came out, super spacey.', '', '',
        @artist6Id, NOW() - INTERVAL 5 MONTH, NOW()),
       (@post39Id, 'IYfde8e4', 'New Track in Progress', 'Not sure what genre this is but I dig it.', '', '', @artist7Id,
        NOW() - INTERVAL 6 MONTH, NOW()),
       (@post40Id, 'Ue3f9zuL', 'B-side Energy', 'Trying something I’ve never done before. Feels weird in a good way.',
        '', '', @artist8Id, NOW() - INTERVAL 7 MONTH, NOW()),
       (@post41Id, 'nwLBHdS7', 'Throwback Jam', 'Trying something I’ve never done before. Feels weird in a good way.',
        '', '', @artist1Id, NOW() - INTERVAL 8 MONTH, NOW()),
       (@post42Id, '6jqfdMgy', 'Soundcheck Snippet', 'Just having fun with the gear today. Unfiltered jam.', '',
        @audioUrl,
        @artist2Id, NOW() - INTERVAL 9 MONTH, NOW()),
       (@post43Id, 'Uo68Ibdf', 'Working on Transitions', 'Do you prefer this kind of energy or more laid-back stuff?',
        '', '', @artist3Id, NOW() - INTERVAL 10 MONTH, NOW()),
       (@post44Id, '8b4EMes8', 'Dreamscape Incoming', 'Inspired by old school soul records this week.', '', '',
        @artist4Id, NOW() - INTERVAL 11 MONTH, NOW()),
       (@post45Id, 'mc4WdJuP', 'Sunset Recording Session', 'This might end up on the next project. Thoughts?', '',
        @audioUrl,
        @artist5Id, NOW() - INTERVAL 1 YEAR, NOW()),
       (@post46Id, 'eCQgvAoV', 'Just Vibes', 'Ambient noise or actual song? You decide.', '', '', @artist6Id,
        NOW() - INTERVAL 2 YEAR,
        NOW()),
       (@post47Id, 'gcrjpVme', 'This One Hits Different', 'Couldn’t sleep, so I made this instead.', '', '', @artist7Id,
        NOW() - INTERVAL 3 YEAR, NOW()),
       (@post48Id, '0aoqA2KJ', 'Dreamscape Incoming', 'Love how this texture came out, super spacey.', '', '',
        @artist8Id, NOW() - INTERVAL 4 YEAR, NOW()),
       (@post49Id, 'RSlc8ljD', 'Late Night Mixdown',
        'You ever make something and not know if it’s good or not? That’s this.', '', '', @artist1Id,
        NOW() - INTERVAL 5 YEAR, NOW()),
       (@post50Id, 'qJUvYFv9', 'Trying Out a New Sound', 'Feels like a soundtrack to a dream.', '', '', @artist2Id,
        NOW() - INTERVAL 6 YEAR, NOW());

INSERT INTO users_followers (user_id, follower_id)
VALUES (@artist1Id, @user1Id),
       (@artist2Id, @user1Id),
       (@artist3Id, @user2Id),
       (@artist4Id, @user2Id),
       (@artist5Id, @user3Id),
       (@artist6Id, @user3Id);

INSERT INTO users_followers (user_id, follower_id)
VALUES (@artist2Id, @artist1Id),
       (@artist1Id, @artist2Id),
       (@artist4Id, @artist3Id),
       (@artist3Id, @artist4Id),
       (@artist6Id, @artist5Id),
       (@artist7Id, @artist6Id),
       (@artist5Id, @artist7Id);

INSERT INTO users_followers (user_id, follower_id)
VALUES (@artist1Id, @adminId),
       (@artist2Id, @adminId),
       (@artist3Id, @adminId),
       (@artist4Id, @adminId),
       (@artist5Id, @adminId),
       (@artist6Id, @adminId),
       (@artist7Id, @adminId),
       (@artist8Id, @adminId);

INSERT INTO users_followers (user_id, follower_id)
VALUES (@adminId, @artist1Id);

INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'pV8Xd2Zc', @post3Id, @artist4Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'pBnwZJBm', @post3Id, @artist2Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'P7xqj7gF', @post3Id, @artist3Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'Dpl1SOST', @post4Id, @artist6Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'o6nEV5n9', @post4Id, @artist8Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), '2iUYxFHg', @post4Id, @user1Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'xWuQs62B', @post4Id, @artist2Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'pBnwZJBm', @post3Id, @artist2Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'P7xqj7gF', @post3Id, @artist3Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'Dpl1SOST', @post4Id, @artist6Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'o6nEV5n9', @post4Id, @artist8Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), '2iUYxFHg', @post4Id, @user1Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'xWuQs62B', @post4Id, @artist2Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'ei4apgV7', @post5Id, @user2Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), '3FHdw7xN', @post5Id, @artist3Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'JSrY7gDv', @post6Id, @artist2Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'wD81ePwI', @post6Id, @user3Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), '3pVuAl77', @post6Id, @artist7Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'WPLK8lcj', @post6Id, @artist3Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), '3hniMxrn', @post6Id, @user2Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'pC5jBb0O', @post7Id, @artist1Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), '5yZwaoSs', @post7Id, @artist3Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'Dhvst5fG', @post7Id, @user3Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), '4rDnn3rV', @post7Id, @artist5Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'm9KrlVGY', @post8Id, @artist8Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), '8ofOhvFq', @post8Id, @artist6Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), '6AixzTJE', @post8Id, @user2Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'i7vaIRoD', @post8Id, @artist4Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'y5OmyLt2', @post8Id, @artist7Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), '5ADL2zJB', @post9Id, @user3Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'USmqlhhg', @post9Id, @adminId, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'Rzp7N14s', @post9Id, @artist2Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), '3N6Dl1cJ', @post9Id, @artist7Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'EKKFVRde', @post9Id, @user2Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'C9p4TMmD', @post9Id, @artist5Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'UsIiCrle', @post10Id, @artist7Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'kCm3aykz', @post10Id, @user1Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'R51mUWO2', @post10Id, @artist2Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'AJdlhAyb', @post10Id, @artist4Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'RR1fX5OS', @post10Id, @artist1Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), '4ejMGHw7', @post10Id, @artist3Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'cUepIt4o', @post11Id, @artist3Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'S8roDjpk', @post12Id, @artist4Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'x9lrzN1p', @post12Id, @artist8Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'cCX8j0c8', @post12Id, @artist6Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'CylLJNgu', @post12Id, @adminId, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'JKKLVabb', @post13Id, @adminId, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'iIfJ1gVe', @post13Id, @artist2Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'yHwRqaIT', @post14Id, @artist8Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), '21BuKxdg', @post15Id, @user3Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'hRmX4WLX', @post15Id, @artist5Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'lVPvq6K3', @post16Id, @artist1Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'Xx5Hzskl', @post16Id, @artist3Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'dPdwvXEy', @post16Id, @artist2Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'MqPMcx6j', @post16Id, @user1Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'B0uq8t5K', @post18Id, @user1Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'uFQhiNNe', @post18Id, @adminId, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'HQHLMKJD', @post18Id, @artist8Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'nC7k5kys', @post18Id, @artist7Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'FZRQFKN6', @post18Id, @artist4Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'DXVuSa5B', @post18Id, @user3Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'rzM0SBg9', @post18Id, @artist1Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'ewZicSGo', @post18Id, @user2Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'HcZ1LNvx', @post18Id, @artist3Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'aWPUObKU', @post19Id, @artist4Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'KhREep3T', @post21Id, @artist2Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'CWr4cW9Q', @post21Id, @artist1Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'tSq3Jhg4', @post21Id, @artist5Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'avMgeJDu', @post21Id, @artist7Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'BPvkmuvQ', @post21Id, @user2Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'tBDrwVtm', @post21Id, @user1Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'YDxGRFXA', @post22Id, @artist4Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'zBNKsq4e', @post22Id, @user1Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'rBiSiAFz', @post22Id, @adminId, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'BlrZyCrM', @post22Id, @artist1Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'zVotFJ4s', @post22Id, @artist2Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'Zd2Kt6v3', @post23Id, @artist3Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'Jb04aQtX', @post23Id, @artist8Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'rTvpDwRS', @post24Id, @adminId, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'uRh07cGe', @post24Id, @user3Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'wUzpu3of', @post24Id, @artist3Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'SJQdP4Pf', @post24Id, @artist1Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'RJk8fBvH', @post25Id, @artist5Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), '3nS4D6T2', @post26Id, @artist1Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'eSQe93QV', @post26Id, @user2Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'E1YhlFKz', @post27Id, @artist4Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), '9TmgAe2G', @post28Id, @user3Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'BovmCwWu', @post29Id, @user3Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'LzpjUioD', @post30Id, @artist8Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'rEszENaE', @post30Id, @artist2Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'W4RweRPx', @post30Id, @artist1Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'vzGgIndx', @post30Id, @user3Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), '2P2pns8l', @post30Id, @artist7Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'ISmZjzdI', @post30Id, @artist4Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'oI7Euzl9', @post31Id, @artist1Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'CD4N5VYR', @post31Id, @user2Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'hGffcjWS', @post32Id, @adminId, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'xGxU1O2O', @post32Id, @artist4Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'q23jucCi', @post34Id, @artist8Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'WknvOPR5', @post34Id, @artist6Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), '3BQmg69a', @post34Id, @user1Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'geDce1Sp', @post34Id, @user2Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'hy1z56iY', @post34Id, @artist4Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'nojPwSBY', @post34Id, @artist5Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'A4HOBIQG', @post34Id, @artist7Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'seOPDhHX', @post34Id, @user3Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'tv0KTLkD', @post34Id, @artist1Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'A2EfQTMg', @post35Id, @adminId, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'MNPII8pc', @post35Id, @artist1Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'f3AThUqN', @post35Id, @artist3Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'L3EyyjCC', @post35Id, @artist7Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'UCMCkYfo', @post35Id, @user3Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'VnjrK1sH', @post35Id, @artist6Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'vulGyhLy', @post35Id, @artist5Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'DNYzbw4A', @post36Id, @artist8Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'SQ5WPLh7', @post36Id, @artist7Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'd4gExuBj', @post36Id, @artist1Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'z77aEnkX', @post36Id, @artist6Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'JfU8sufx', @post36Id, @artist4Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'aFcOUhLF', @post36Id, @user3Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'ygnmn1ft', @post36Id, @artist3Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'KgvDTo9B', @post37Id, @artist8Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'xvYCzWxY', @post38Id, @artist5Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'cRprFFWU', @post38Id, @artist3Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'TpgDyOHl', @post38Id, @artist4Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'vUJaiq0e', @post38Id, @user2Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'PkvV84lr', @post38Id, @artist6Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'BUiswRhU', @post38Id, @artist1Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'nFQlZCuf', @post38Id, @user3Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'sBCKjXqc', @post38Id, @adminId, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), '0yYcGkuu', @post38Id, @artist2Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'm1WYbtVn', @post39Id, @user2Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'RIBOshh9', @post39Id, @adminId, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'DdDENv7J', @post41Id, @user2Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'KI9nMgRU', @post41Id, @adminId, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'RsvwyqRH', @post41Id, @user1Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), '5BvBbVRA', @post41Id, @artist8Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'pGMQWDn5', @post41Id, @artist4Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), '3zv6b6r0', @post41Id, @artist7Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'NAgpjbmo', @post41Id, @artist1Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'DIHYRM89', @post41Id, @artist3Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'rpBZ1epI', @post41Id, @artist2Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 's1aKTj82', @post42Id, @artist3Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), '7gzyXY9d', @post42Id, @user2Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'PtjaWX3W', @post43Id, @user2Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 's7fZIusX', @post43Id, @user3Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'vETcIcDF', @post44Id, @artist5Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), '3heCthn7', @post45Id, @artist3Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'Vsyh0AdT', @post45Id, @user1Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), '1ALgcCpI', @post45Id, @artist2Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), '89IcLC12', @post45Id, @artist1Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'UZLVCEeO', @post46Id, @artist8Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'fbzRPfgA', @post46Id, @user2Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'WWygZPSg', @post46Id, @artist5Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'qDMWVuMj', @post46Id, @user1Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'PUeIUP46', @post46Id, @artist6Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), '0UwhMdDh', @post46Id, @user3Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'PGbgNJIP', @post47Id, @artist7Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'eKL0qQuD', @post47Id, @user3Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), '8ljQZpeR', @post47Id, @artist2Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'EKH9gxiu', @post47Id, @adminId, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'EJvi2rBY', @post47Id, @artist5Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), '1N42wG5e', @post47Id, @artist3Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'cIL2v7K5', @post47Id, @artist4Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'ZysGVXx5', @post47Id, @artist8Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), '0OjRT3pc', @post47Id, @artist1Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'yNL9tqU3', @post47Id, @user1Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'L7mc2dcy', @post48Id, @artist8Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), '1T8sZz8u', @post48Id, @artist3Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'SpcrXPcK', @post49Id, @artist6Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'gKsgznld', @post49Id, @user3Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'GDAijurW', @post49Id, @artist3Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'hLiubnWd', @post49Id, @artist5Id, NOW());
INSERT INTO likes (id, slug, post_id, user_id, liked_at)
VALUES (UUID(), 'pAj0qkIO', @post49Id, @artist1Id, NOW());

INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post3Id, @artist4Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post3Id, @artist2Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post3Id, @artist3Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post4Id, @artist6Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post4Id, @artist8Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post4Id, @user1Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post4Id, @artist2Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post3Id, @artist2Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post3Id, @artist3Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post4Id, @artist6Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post4Id, @artist8Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post4Id, @user1Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post4Id, @artist2Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post5Id, @user2Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post5Id, @artist3Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post6Id, @artist2Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post6Id, @user3Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post6Id, @artist7Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post6Id, @artist3Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post6Id, @user2Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post7Id, @artist1Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post7Id, @artist3Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post7Id, @user3Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post7Id, @artist5Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post8Id, @artist8Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post8Id, @artist6Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post8Id, @user2Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post8Id, @artist4Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post8Id, @artist7Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post9Id, @user3Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post9Id, @adminId);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post9Id, @artist2Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post9Id, @artist7Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post9Id, @user2Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post9Id, @artist5Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post10Id, @artist7Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post10Id, @user1Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post10Id, @artist2Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post10Id, @artist4Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post10Id, @artist1Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post10Id, @artist3Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post11Id, @artist3Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post12Id, @artist4Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post12Id, @artist8Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post12Id, @artist6Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post12Id, @adminId);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post13Id, @adminId);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post13Id, @artist2Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post14Id, @artist8Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post15Id, @user3Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post15Id, @artist5Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post16Id, @artist1Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post16Id, @artist3Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post16Id, @artist2Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post16Id, @user1Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post18Id, @user1Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post18Id, @adminId);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post18Id, @artist8Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post18Id, @artist7Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post18Id, @artist4Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post18Id, @user3Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post18Id, @artist1Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post18Id, @user2Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post18Id, @artist3Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post19Id, @artist4Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post21Id, @artist2Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post21Id, @artist1Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post21Id, @artist5Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post21Id, @artist7Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post21Id, @user2Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post21Id, @user1Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post22Id, @artist4Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post22Id, @user1Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post22Id, @adminId);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post22Id, @artist1Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post22Id, @artist2Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post23Id, @artist3Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post23Id, @artist8Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post24Id, @adminId);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post24Id, @user3Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post24Id, @artist3Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post24Id, @artist1Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post25Id, @artist5Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post26Id, @artist1Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post26Id, @user2Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post27Id, @artist4Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post28Id, @user3Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post29Id, @user3Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post30Id, @artist8Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post30Id, @artist2Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post30Id, @artist1Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post30Id, @user3Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post30Id, @artist7Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post30Id, @artist4Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post31Id, @artist1Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post31Id, @user2Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post32Id, @adminId);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post32Id, @artist4Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post34Id, @artist8Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post34Id, @artist6Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post34Id, @user1Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post34Id, @user2Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post34Id, @artist4Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post34Id, @artist5Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post34Id, @artist7Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post34Id, @user3Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post34Id, @artist1Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post35Id, @adminId);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post35Id, @artist1Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post35Id, @artist3Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post35Id, @artist7Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post35Id, @user3Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post35Id, @artist6Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post35Id, @artist5Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post36Id, @artist8Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post36Id, @artist7Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post36Id, @artist1Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post36Id, @artist6Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post36Id, @artist4Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post36Id, @user3Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post36Id, @artist3Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post37Id, @artist8Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post38Id, @artist5Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post38Id, @artist3Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post38Id, @artist4Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post38Id, @user2Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post38Id, @artist6Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post38Id, @artist1Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post38Id, @user3Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post38Id, @adminId);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post38Id, @artist2Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post39Id, @user2Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post39Id, @adminId);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post41Id, @user2Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post41Id, @adminId);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post41Id, @user1Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post41Id, @artist8Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post41Id, @artist7Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post41Id, @artist1Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post41Id, @artist3Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post41Id, @artist2Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post42Id, @artist3Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post42Id, @user2Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post43Id, @user3Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post44Id, @artist5Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post45Id, @artist3Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post45Id, @user1Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post45Id, @artist2Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post45Id, @artist1Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post46Id, @artist8Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post46Id, @user1Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post46Id, @artist6Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post46Id, @user3Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post47Id, @artist7Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post47Id, @user3Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post47Id, @artist2Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post47Id, @artist5Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post47Id, @artist3Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post47Id, @artist4Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post47Id, @artist8Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post47Id, @artist1Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post47Id, @user1Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post48Id, @artist8Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post48Id, @artist3Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post49Id, @artist6Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post49Id, @artist3Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post49Id, @artist5Id);
INSERT INTO posts_reposts (post_id, user_id)
VALUES (@post49Id, @artist1Id);

INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), 'FXgIaYgB', 'Not sure about the drop, but interesting!', @post3Id, @artist6Id, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), 'cjnsiuDd', 'The groove on this one is tight.', @post20Id, @user1Id, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), '95s54dMC', 'This inspired me to start a new project.', @post20Id, @artist2Id, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), 'c8PTxuSr', 'I\'m hearing some Boards of Canada influence?', @post20Id, @adminId, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), 'BYngelDi', 'Love the analog feel on this.', @post42Id, @artist5Id, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), '820t2WKP', 'Not sure about the drop, but interesting!', @post42Id, @user1Id, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), '6lrIpwu6', 'Can’t stop looping this.', @post43Id, @artist5Id, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), 'cYrGo5Uw', 'Could totally hear this in a movie.', @post24Id, @artist4Id, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), '4JVqMI17', 'Love the analog feel on this.', @post7Id, @artist4Id, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), 'g9Ul7r1z', 'Reminds me of early days in the scene.', @post48Id, @adminId, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), '7MT76G0E', 'This gave me chills.', @post18Id, @user3Id, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), 'HuL2Ts0c', 'Love this vibe 🔥', @post27Id, @user1Id, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), 'gOqhdv7a', 'More of this please 🙏', @post27Id, @artist5Id, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), 'NCau6dGe', 'The groove on this one is tight.', @post26Id, @artist3Id, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), 'i2rNkHtv', 'Super clean mix, nice work.', @post26Id, @user1Id, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), 'KAcUq4sx', 'Wild energy, I dig it.', @post28Id, @artist2Id, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), 'H6RhjQiP', 'That texture is wild!', @post28Id, @user2Id, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), 'lh1tGmrv', 'Super clean mix, nice work.', @post28Id, @artist3Id, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), 'jfpAfapz', 'Can’t stop looping this.', @post23Id, @artist6Id, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), '5k46aeUy', 'Can’t stop looping this.', @post19Id, @artist2Id, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), 'kCri8nLn', 'Reminds me of early days in the scene.', @post46Id, @user1Id, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), '4nRHbl6B', 'Could totally hear this in a movie.', @post31Id, @artist5Id, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), 'niYe2JmA', 'Can you drop the full track?', @post31Id, @adminId, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), '1OyqGKEe', 'Not sure about the drop, but interesting!', @post49Id, @artist1Id, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), 'MaKr8BdK', 'This goes hard, especially the bass.', @post33Id, @artist4Id, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), 'X1uZTvk1', 'Reminds me of early days in the scene.', @post33Id, @artist6Id, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), 'VQo17E6k', 'Love the analog feel on this.', @post33Id, @artist7Id, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), 'dKdBBcdS', 'I\'m hearing some Boards of Canada influence?', @post13Id, @artist4Id, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), 'S9ruVctL', 'Brilliant use of silence and space.', @post13Id, @artist1Id, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), 'dJoiVQiB', 'The groove on this one is tight.', @post13Id, @user3Id, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), 'D2p4EQyW', 'Super clean mix, nice work.', @post8Id, @artist5Id, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), 'dxudAceL', 'Sounds like a dream.', @post8Id, @artist5Id, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), 'i2vC9NqA', 'Kinda reminds me of Aphex Twin.', @post21Id, @artist7Id, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), 'ychU9MfR', 'That texture is wild!', @post21Id, @user3Id, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), 'hxCTBA6I', 'Wild energy, I dig it.', @post22Id, @artist8Id, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), '8eQFiOwa', 'I\'m hearing some Boards of Canada influence?', @post22Id, @artist7Id, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), 'TspUHeNn', 'Wild energy, I dig it.', @post50Id, @user2Id, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), 'ZOUNs7nS', 'The groove on this one is tight.', @post37Id, @artist6Id, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), 'gesea7go', 'Love this vibe 🔥', @post38Id, @artist4Id, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), 'AUiK9pRY', 'Brilliant use of silence and space.', @post38Id, @user2Id, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), 'xRDiXqVN', 'Love this vibe 🔥', @post38Id, @user3Id, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), '106q1tqg', 'The groove on this one is tight.', @post30Id, @artist6Id, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), 'va2cYgx0', 'Super clean mix, nice work.', @post30Id, @artist1Id, NOW());
INSERT INTO comments (id, slug, content, post_id, user_id, created_at)
VALUES (UUID(), 'nB8A7EBn', 'I\'m hearing some Boards of Canada influence?', @post30Id, @adminId, NOW());

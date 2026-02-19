-- Création de la table poster
CREATE TABLE IF NOT EXISTS poster (
    id    VARCHAR(255)  NOT NULL PRIMARY KEY,
    url   VARCHAR(1024) NOT NULL,
    titre VARCHAR(512)  NOT NULL
);

-- Données de test
INSERT IGNORE INTO poster (id, url, titre) VALUES
    ('tt0111161', 'https://m.media-amazon.com/images/M/MV5BNDE3ODcxYzMtY2YzZC00NiYy._V1_.jpg',  'The Shawshank Redemption'),
    ('tt0068646', 'https://m.media-amazon.com/images/M/MV5BM2MyNjYxNmUtYTAwNi00MTYx._V1_.jpg',  'The Godfather'),
    ('tt0071562', 'https://m.media-amazon.com/images/M/MV5BMWMwMGQzZTItY2JlNC00OWRi._V1_.jpg',  'The Godfather Part II'),
    ('tt0468569', 'https://m.media-amazon.com/images/M/MV5BMTMxNTMwODM0NF5BMl5BanBn._V1_.jpg',  'The Dark Knight'),
    ('tt0050083', 'https://m.media-amazon.com/images/M/MV5BMWU4N2FjNzYtNTVkNC00NzQ0._V1_.jpg',  '12 Angry Men');

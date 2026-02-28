// Script d'initialisation MongoDB — exécuté au premier démarrage du container
// Crée la collection "posters" et insère des données de test

db = db.getSiblingDB("posters_db");

db.posters.drop();

db.posters.insertMany([
    { _id: "tt0111161", url: "https://m.media-amazon.com/images/M/MV5BMDAyY2FhYjctNDc5OS00MDNlLThiMGUtY2UxYWVkNGY2ZjljXkEyXkFqcGc@._V1_SX300.jpg",  titre: "The Shawshank Redemption" },
    { _id: "tt0068646", url: "https://m.media-amazon.com/images/M/MV5BNGEwYjgwOGQtYjg5ZS00Njc1LTk2ZGEtM2QwZWQ2NjdhZTE5XkEyXkFqcGc@._V1_SX300.jpg",  titre: "The Godfather" },
    { _id: "tt0071562", url: "https://m.media-amazon.com/images/M/MV5BMDIxMzBlZDktZjMxNy00ZGI4LTgxNDEtYWRlNzRjMjJmOGQ1XkEyXkFqcGc@._V1_SX300.jpg",  titre: "The Godfather Part II" },
    { _id: "tt0468569", url: "https://m.media-amazon.com/images/M/MV5BMTMxNTMwODM0NF5BMl5BanBnXkFtZTcwODAyMTk2Mw@@._V1_SX300.jpg",  titre: "The Dark Knight" },
    { _id: "tt0050083", url: "https://m.media-amazon.com/images/M/MV5BYjE4NzdmOTYtYjc5Yi00YzBiLWEzNDEtNTgxZGQ2MWVkN2NiXkEyXkFqcGc@._V1_SX300.jpg",  titre: "12 Angry Men" }
]);

print("Collection posters initialisée avec " + db.posters.countDocuments() + " documents.");

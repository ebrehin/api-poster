// Script d'initialisation MongoDB — exécuté au premier démarrage du container
// Crée la collection "posters" et insère des données de test

db = db.getSiblingDB("posters_db");

db.posters.drop();

db.posters.insertMany([
    { _id: "tt0111161", url: "https://m.media-amazon.com/images/M/MV5BNDE3ODcxYzMtY2YzZC00NiYy._V1_.jpg",  titre: "The Shawshank Redemption" },
    { _id: "tt0068646", url: "https://m.media-amazon.com/images/M/MV5BM2MyNjYxNmUtYTAwNi00MTYx._V1_.jpg",  titre: "The Godfather" },
    { _id: "tt0071562", url: "https://m.media-amazon.com/images/M/MV5BMWMwMGQzZTItY2JlNC00OWRi._V1_.jpg",  titre: "The Godfather Part II" },
    { _id: "tt0468569", url: "https://m.media-amazon.com/images/M/MV5BMTMxNTMwODM0NF5BMl5BanBn._V1_.jpg",  titre: "The Dark Knight" },
    { _id: "tt0050083", url: "https://m.media-amazon.com/images/M/MV5BMWU4N2FjNzYtNTVkNC00NzQ0._V1_.jpg",  titre: "12 Angry Men" }
]);

print("Collection posters initialisée avec " + db.posters.countDocuments() + " documents.");

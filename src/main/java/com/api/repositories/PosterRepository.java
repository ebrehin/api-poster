package com.api.repositories;

import com.api.entities.Poster;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class PosterRepository {

    private final MongoClient mongoClient;
    private final MongoCollection<Document> collection;

    public PosterRepository() {
        String host = getEnv("MONGO_HOST", "mongodb");
        String port = getEnv("MONGO_PORT", "27017");
        String db   = getEnv("MONGO_DB",   "posters_db");

        this.mongoClient = MongoClients.create("mongodb://" + host + ":" + port);
        MongoDatabase database = mongoClient.getDatabase(db);
        this.collection = database.getCollection("posters");
    }

    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    public List<Poster> findAll() {
        List<Poster> posters = new ArrayList<>();
        for (Document doc : collection.find()) {
            posters.add(docToPoster(doc));
        }
        return posters;
    }

    public Poster findById(String id) {
        Document doc = collection.find(Filters.eq("_id", id)).first();
        return doc != null ? docToPoster(doc) : null;
    }

    public void save(Poster poster) {
        collection.insertOne(posterToDoc(poster));
    }

    public void update(String id, Poster poster) {
        if (poster.getUrl() != null && !poster.getUrl().isBlank()) {
            collection.updateOne(Filters.eq("_id", id), Updates.set("url", poster.getUrl()));
        }
        if (poster.getTitre() != null && !poster.getTitre().isBlank()) {
            collection.updateOne(Filters.eq("_id", id), Updates.set("titre", poster.getTitre()));
        }
    }

    public boolean delete(String id) {
        long deleted = collection.deleteOne(Filters.eq("_id", id)).getDeletedCount();
        return deleted > 0;
    }

    public boolean exists(String id) {
        return collection.find(Filters.eq("_id", id)).first() != null;
    }

    private Poster docToPoster(Document doc) {
        return new Poster(doc.getString("_id"), doc.getString("url"), doc.getString("titre"));
    }

    private Document posterToDoc(Poster poster) {
        return new Document("_id", poster.getId())
                .append("url",   poster.getUrl())
                .append("titre", poster.getTitre());
    }

    private String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }
}

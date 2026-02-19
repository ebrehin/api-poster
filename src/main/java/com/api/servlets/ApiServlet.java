package com.api.servlets;

import com.api.dto.ErrorResponse;
import com.api.entities.Poster;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Servlet principale de l'API - MongoDB.
 *
 * Routes :
 *   GET    /api/posters       -> liste tous les posters
 *   GET    /api/posters/{id}  -> recupere un poster par son id
 *   POST   /api/posters       -> cree un poster       (body JSON : {"id":"...","url":"...","titre":"..."})
 *   PUT    /api/posters/{id}  -> modifie un poster     (body JSON : {"url":"...","titre":"..."})
 *   DELETE /api/posters/{id}  -> supprime un poster
 *
 * La connexion est configuree via les variables d'environnement
 * MONGO_HOST, MONGO_PORT, MONGO_DB (definies dans docker-compose.yaml).
 */
@WebServlet("/api/*")
public class ApiServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(ApiServlet.class.getName());

    private MongoClient mongoClient;
    private MongoCollection<Document> collection;
    private ObjectMapper mapper;

    @Override
    public void init() throws ServletException {
        String host = getEnv("MONGO_HOST", "mongodb");
        String port = getEnv("MONGO_PORT", "27017");
        String db   = getEnv("MONGO_DB",   "posters_db");

        mongoClient = MongoClients.create("mongodb://" + host + ":" + port);
        MongoDatabase database = mongoClient.getDatabase(db);
        collection = database.getCollection("posters");
        mapper = new ObjectMapper();
        log.info("ApiServlet initialisee - MongoDB : " + host + ":" + port + "/" + db);
    }

    @Override
    public void destroy() {
        if (mongoClient != null) mongoClient.close();
    }

    private String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }

    // GET
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = getPath(request);

        if (path.equals("/posters")) {
            List<Poster> posters = new ArrayList<>();
            for (Document doc : collection.find()) {
                posters.add(docToPoster(doc));
            }
            sendJson(response, posters);
            return;
        }

        if (path.startsWith("/posters/")) {
            String id = path.substring("/posters/".length());
            Document doc = collection.find(Filters.eq("_id", id)).first();
            if (doc == null) {
                sendError(response, HttpServletResponse.SC_NOT_FOUND, "Poster introuvable : " + id);
                return;
            }
            sendJson(response, docToPoster(doc));
            return;
        }

        sendError(response, HttpServletResponse.SC_NOT_FOUND, "Route introuvable : " + path);
    }

    // POST
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = getPath(request);

        if (!path.equals("/posters")) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, "Route introuvable : " + path);
            return;
        }

        Poster poster = mapper.readValue(request.getInputStream(), Poster.class);

        if (poster.getId() == null || poster.getId().isBlank()
                || poster.getUrl() == null || poster.getUrl().isBlank()
                || poster.getTitre() == null || poster.getTitre().isBlank()) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Les champs id, url et titre sont obligatoires.");
            return;
        }

        if (collection.find(Filters.eq("_id", poster.getId())).first() != null) {
            sendError(response, HttpServletResponse.SC_CONFLICT, "Un poster avec l'id '" + poster.getId() + "' existe deja.");
            return;
        }

        collection.insertOne(posterToDoc(poster));
        response.setStatus(HttpServletResponse.SC_CREATED);
        sendJson(response, poster);
    }

    // PUT
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = getPath(request);

        if (!path.startsWith("/posters/")) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, "Route introuvable : " + path);
            return;
        }

        String id = path.substring("/posters/".length());
        Poster patch = mapper.readValue(request.getInputStream(), Poster.class);

        if (collection.find(Filters.eq("_id", id)).first() == null) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, "Poster introuvable : " + id);
            return;
        }

        if (patch.getUrl() != null && !patch.getUrl().isBlank()) {
            collection.updateOne(Filters.eq("_id", id), Updates.set("url", patch.getUrl()));
        }
        if (patch.getTitre() != null && !patch.getTitre().isBlank()) {
            collection.updateOne(Filters.eq("_id", id), Updates.set("titre", patch.getTitre()));
        }

        Document updated = collection.find(Filters.eq("_id", id)).first();
        sendJson(response, docToPoster(updated));
    }

    // DELETE
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = getPath(request);

        if (!path.startsWith("/posters/")) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, "Route introuvable : " + path);
            return;
        }

        String id = path.substring("/posters/".length());
        long deleted = collection.deleteOne(Filters.eq("_id", id)).getDeletedCount();

        if (deleted == 0) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, "Poster introuvable : " + id);
            return;
        }
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    // Helpers
    private String getPath(HttpServletRequest request) {
        String info = request.getPathInfo();
        if (info == null || info.isBlank()) return "/";
        return info.length() > 1 && info.endsWith("/") ? info.substring(0, info.length() - 1) : info;
    }

    private Poster docToPoster(Document doc) {
        return new Poster(doc.getString("_id"), doc.getString("url"), doc.getString("titre"));
    }

    private Document posterToDoc(Poster poster) {
        return new Document("_id", poster.getId())
                .append("url",   poster.getUrl())
                .append("titre", poster.getTitre());
    }

    private void sendJson(HttpServletResponse response, Object body) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        mapper.writeValue(response.getWriter(), body);
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        mapper.writeValue(response.getWriter(), new ErrorResponse(status, message));
    }
}

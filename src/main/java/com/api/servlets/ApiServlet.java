package com.api.servlets;

import com.api.entities.Poster;
import com.api.repositories.PosterRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
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

    private PosterRepository repository;
    private ObjectMapper mapper;

    @Override
    public void init() throws ServletException {
        repository = new PosterRepository();
        mapper = new ObjectMapper();
        log.info("ApiServlet initialisee");
    }

    @Override
    public void destroy() {
        if (repository != null) {
            repository.close();
        }
    }

    // GET
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = getPath(request);

        if (path.equals("/posters")) {
            List<Poster> posters = repository.findAll();
            sendJson(response, posters);
            return;
        }

        if (path.startsWith("/posters/")) {
            String id = path.substring("/posters/".length());
            Poster poster = repository.findById(id);
            if (poster == null) {
                sendError(response, HttpServletResponse.SC_NOT_FOUND, "Poster introuvable : " + id);
                return;
            }
            sendJson(response, poster);
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

        if (repository.exists(poster.getId())) {
            sendError(response, HttpServletResponse.SC_CONFLICT, "Un poster avec l'id '" + poster.getId() + "' existe deja.");
            return;
        }

        repository.save(poster);
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

        if (!repository.exists(id)) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, "Poster introuvable : " + id);
            return;
        }

        repository.update(id, patch);

        Poster updated = repository.findById(id);
        sendJson(response, updated);
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
        boolean deleted = repository.delete(id);

        if (!deleted) {
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

    private void sendJson(HttpServletResponse response, Object body) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        mapper.writeValue(response.getWriter(), body);
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        mapper.writeValue(response.getWriter(), new ErrorResponse(status, message));
    }

    public record ErrorResponse(int status, String message) {}
}

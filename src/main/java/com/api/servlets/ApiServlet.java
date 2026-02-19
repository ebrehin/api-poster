package com.api.servlets;

import com.api.entities.Poster;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Servlet principale de l'API.
 *
 * Routes :
 *   GET    /api/posters       → liste tous les posters
 *   GET    /api/posters/{id}  → récupère un poster par son id
 *   POST   /api/posters       → crée un poster  (body JSON : {"id":"...","url":"...","titre":"..."})
 *   DELETE /api/posters/{id}  → supprime un poster
 */
@WebServlet("/api/*")
public class ApiServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(ApiServlet.class.getName());

    private EntityManagerFactory emf;
    private ObjectMapper mapper;

    @Override
    public void init() throws ServletException {
        // L'unité "PostersPU" est définie dans META-INF/persistence.xml
        emf = Persistence.createEntityManagerFactory("PostersPU");
        mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        log.info("ApiServlet initialisée — EMF créé");
    }

    @Override
    public void destroy() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    // -------------------------------------------------------------------------
    // GET
    // -------------------------------------------------------------------------
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = getPath(request);
        EntityManager em = emf.createEntityManager();

        try {
            // GET /api/posters
            if (path.equals("/posters")) {
                sendJson(response, em.createQuery("SELECT p FROM Poster p", Poster.class).getResultList());
                return;
            }

            // GET /api/posters/{id}
            if (path.startsWith("/posters/")) {
                String id = path.substring("/posters/".length());
                Poster poster = em.find(Poster.class, id);
                if (poster == null) {
                    sendError(response, HttpServletResponse.SC_NOT_FOUND, "Poster introuvable : " + id);
                    return;
                }
                sendJson(response, poster);
                return;
            }

            sendError(response, HttpServletResponse.SC_NOT_FOUND, "Route introuvable : " + path);

        } catch (Exception e) {
            log.severe("Erreur GET " + path + " : " + e.getMessage());
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } finally {
            em.close();
        }
    }

    // -------------------------------------------------------------------------
    // POST
    // -------------------------------------------------------------------------
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = getPath(request);
        EntityManager em = emf.createEntityManager();

        try {
            // POST /api/posters
            if (path.equals("/posters")) {
                Poster poster = mapper.readValue(request.getInputStream(), Poster.class);
                em.getTransaction().begin();
                em.persist(poster);
                em.getTransaction().commit();
                response.setStatus(HttpServletResponse.SC_CREATED);
                sendJson(response, poster);
                return;
            }

            sendError(response, HttpServletResponse.SC_NOT_FOUND, "Route introuvable : " + path);

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            log.severe("Erreur POST " + path + " : " + e.getMessage());
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } finally {
            em.close();
        }
    }

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = getPath(request);
        EntityManager em = emf.createEntityManager();

        try {
            // DELETE /api/posters/{id}
            if (path.startsWith("/posters/")) {
                String id = path.substring("/posters/".length());
                em.getTransaction().begin();
                Poster poster = em.find(Poster.class, id);
                if (poster == null) {
                    em.getTransaction().rollback();
                    sendError(response, HttpServletResponse.SC_NOT_FOUND, "Poster introuvable : " + id);
                    return;
                }
                em.remove(poster);
                em.getTransaction().commit();
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                return;
            }

            sendError(response, HttpServletResponse.SC_NOT_FOUND, "Route introuvable : " + path);

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            log.severe("Erreur DELETE " + path + " : " + e.getMessage());
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } finally {
            em.close();
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /** Extrait la sous-route après /api (ex: /api/posters → /posters) */
    private String getPath(HttpServletRequest request) {
        String info = request.getPathInfo();
        return (info == null || info.isBlank()) ? "/" : info;
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

    /** Simple enveloppe pour les réponses d'erreur */
    public record ErrorResponse(int status, String message) {}
}

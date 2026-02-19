# api-poster

API REST Java déployée sur **Tomcat 10.1** avec une base de données **MariaDB**, le tout conteneurisé via **Docker Compose**.

## Stack technique

| Composant | Version |
|---|---|
| Java | 17 |
| Jakarta Servlet | 6.0 (Tomcat 10.1) |
| JDBC | MongoDB driver 5.x |
| Jackson | 2.17 |
| Build | Maven 3.9 |

## Structure du projet

```
api-poster/
├── Dockerfile                      # Build Maven + image Tomcat finale
├── docker-compose.yaml             # Lance Tomcat + MariaDB
├── pom.xml
└── src/main/
    ├── java/com/api/
    │   ├── entities/
    │   │   └── Poster.java         # POJO — id, url, titre
    │   └── servlets/
    │       └── ApiServlet.java     # Servlet unique — toutes les routes
    ├── resources/
    │   └── mongo/
    │       └── init.js             # Création de la collection + données de test
    └── webapp/
        └── WEB-INF/
            └── web.xml
```

## Lancer l'application

### Prérequis

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) installé et démarré

### Démarrage

```bash
# Depuis le dossier api-poster/
docker compose up --build
```

Cette commande :
1. Compile le projet Java avec Maven (dans un container)
2. Produit le fichier `api-posters.war`
3. Lance un container **MongoDB** (`poster-mongodb`) et y exécute `init.js`
4. Attend que MariaDB soit prêt (healthcheck)
5. Lance un container **Tomcat** (`poster-tomcat`) et y déploie le WAR

L'API est accessible sur : **http://localhost:8080**

### Arrêt

```bash
docker compose down
```

Pour supprimer aussi le volume de données MariaDB (nécessaire après une modification de `init.sql`) :

```bash
docker compose down -v
```

## Endpoints disponibles

| Méthode | Route | Description |
|---|---|---|
| GET | `/api/posters` | Liste tous les posters |
| GET | `/api/posters/{id}` | Récupère un poster par son id |
| POST | `/api/posters` | Crée un poster |
| PUT | `/api/posters/{id}` | Modifie l'url et/ou le titre d'un poster |
| DELETE | `/api/posters/{id}` | Supprime un poster |

### Exemples curl

```bash
# Lister les posters
curl http://localhost:8080/api/posters

# Récupérer un poster
curl http://localhost:8080/api/posters/tt0111161

# Créer un poster
curl -X POST http://localhost:8080/api/posters \
     -H "Content-Type: application/json" \
     -d '{"id": "tt0111161", "url": "https://example.com/shawshank.jpg", "titre": "The Shawshank Redemption"}'

# Modifier un poster (un seul champ suffit)
curl -X PUT http://localhost:8080/api/posters/tt0111161 \
     -H "Content-Type: application/json" \
     -d '{"titre": "The Shawshank Redemption (1994)"}'

# Supprimer un poster
curl -X DELETE http://localhost:8080/api/posters/tt0111161
```

## Configuration de la base de données

Les paramètres de connexion sont définis via les variables d'environnement dans `docker-compose.yaml` et lus au démarrage par le servlet :

| Variable | Valeur par défaut |
|---|---|
| `MONGO_HOST` | `mongodb` |
| `MONGO_PORT` | `27017` |
| `MONGO_DB` | `posters_db` |
## Développement sans Docker

Il est possible de tester localement en pointant `persistence.xml` vers une instance MariaDB locale, puis en déployant le WAR sur un Tomcat 10.1 installé sur la machine. Dans ce cas, le dossier `Servers/` retrouve son utilité si tu utilises Eclipse.

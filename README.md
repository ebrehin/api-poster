# api-poster

API REST Java déployée sur **Tomcat 10.1** avec une base de données **MongoDB**, le tout conteneurisé via **Docker Compose**.

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
├── 📁 src
│   └── 📁 main
│       ├── 📁 java
│       │   └── 📁 com
│       │       └── 📁 api
│       │           ├── 📁 entities
│       │           │   └── ☕ Poster.java          # POJO — id, url, titre
│       │           ├── 📁 repositories
│       │           └── 📁 servlets
│       │               └── ☕ ApiServlet.java      # Servlet unique — toutes les routes
│       ├── 📁 resources
│       │   ├── 📁 META-INF
│       │   ├── 📁 mongo
│       │   │   └── 📄 init.js                      # Création de la collection + données de test
│       │
│       └── 📁 webapp
│           ├── 📁 META-INF
│           │   └── 📄 MANIFEST.MF
│           ├── 📁 WEB-INF
│           │    ├── 📁 lib
│           │    └── ⚙️ web.xml
│           └── 📁 swagger-ui                       # Configurations swagger
│               ├── 🌐 index.html
│               ├── ⚙️ openapi.yaml
│               └── 📄 swagger-initializer.js
├── ⚙️ .gitignore
├── 📝 README.md
├── 🐳 Dockerfile                                   # Build Maven + image Tomcat finale
├── ⚙️ docker-compose.yaml                          # Lance Tomcat + MongoDB
└── ⚙️ pom.xml
```

## Lancer l'application

### Prérequis

- Docker Desktop installé et démarré

### Démarrage

```bash
docker compose up --build
```

Cette commande :
1. Compile le projet Java avec Maven (dans un container)
2. Produit le fichier `api-posters.war`
3. Lance un container **MongoDB** (`poster-mongodb`) et y exécute `init.js`
4. Attend que MongoDB soit prêt (healthcheck)
5. Lance un container **Tomcat** (`poster-tomcat`) et y déploie le WAR

L'API est accessible sur : **http://localhost:8080**
La documentation Swagger UI est accessible sur : **http://localhost:8080/swagger-ui/**

### Arrêt

```bash
docker compose down
```

Pour supprimer aussi le volume de données MongoDB (nécessaire après une modification de `init.sql`) :

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

## Sécurité JWT

Toutes les routes `/api/*` exigent un token JWT via l'en-tête :

```
Authorization: Bearer <token>
```

La validation est stateless (signature + expiration). La configuration est dans `src/main/resources/application.properties` :

```
security.jwt.secret=CHANGE_ME_TO_A_LONG_RANDOM_SECRET_KEY_32CHARS_MIN
security.jwt.expiration=3h
```

### Exemples curl

```bash
# Lister les posters
curl http://localhost:8080/api/posters \
     -H "Authorization: Bearer <token>"

# Récupérer un poster
curl http://localhost:8080/api/posters/tt0111161 \
     -H "Authorization: Bearer <token>"

# Créer un poster
curl -X POST http://localhost:8080/api/posters \
     -H "Authorization: Bearer <token>" \
     -H "Content-Type: application/json" \
     -d '{"id": "tt0111161", "url": "https://example.com/shawshank.jpg", "titre": "The Shawshank Redemption"}'

# Modifier un poster (un seul champ suffit)
curl -X PUT http://localhost:8080/api/posters/tt0111161 \
     -H "Authorization: Bearer <token>" \
     -H "Content-Type: application/json" \
     -d '{"titre": "The Shawshank Redemption (1994)"}'

# Supprimer un poster
curl -X DELETE http://localhost:8080/api/posters/tt0111161 \
     -H "Authorization: Bearer <token>"
```

### Exemples de requêtes (Powershell)

```ps
# Lister les posters
Invoke-WebRequest -Uri "http://localhost:8080/api/posters/" -Method GET -Headers @{ Authorization = "Bearer <token>" } -UseBasicParsing

# Récupérer un poster
Invoke-WebRequest -Uri "http://localhost:8080/api/posters/tt0111161" -Method GET -Headers @{ Authorization = "Bearer <token>" } -UseBasicParsing

# Créer un poster
Invoke-WebRequest -Uri "http://localhost:8080/api/posters" -Method POST -Headers @{ Authorization = "Bearer <token>" } -ContentType "application/json" -Body '{"id":"tt0111161","url":"https://example.com/shawshank.jpg","titre":"The Shawshank Redemption"}' -UseBasicParsing

# Modifier un poster (un seul champ suffit)
Invoke-WebRequest -Uri "http://localhost:8080/api/posters/tt0111161" -Method PUT -Headers @{ Authorization = "Bearer <token>" } -ContentType "application/json" -Body '{"titre": "The Shawshank Redemption (1994)"}' -UseBasicParsing

# Supprimer un poster
Invoke-WebRequest -Uri "http://localhost:8080/api/posters/tt0111161" -Method DELETE -Headers @{ Authorization = "Bearer <token>" } -UseBasicParsing
```

## Configuration de la base de données

Les paramètres de connexion sont définis via les variables d'environnement dans `docker-compose.yaml` et lus au démarrage par le servlet :

| Variable | Valeur par défaut |
|---|---|
| `MONGO_HOST` | `mongodb` |
| `MONGO_PORT` | `27017` |
| `MONGO_DB` | `posters_db` |
## Développement sans Docker

Il est possible de tester localement en pointant `persistence.xml` vers une instance MongoDB locale, puis en déployant le WAR sur un Tomcat 10.1 installé sur la machine.

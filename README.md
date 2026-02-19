# api-posters

API REST Java déployée sur **Tomcat 10.1** avec une base de données **MariaDB**, le tout conteneurisé via **Docker Compose**.

## Stack technique

| Composant | Version |
|---|---|
| Java | 17 |
| Jakarta Servlet | 6.0 (Tomcat 10.1) |
| JPA / Hibernate | 6.4 |
| MariaDB driver | 3.x |
| Jackson | 2.17 |
| Build | Maven 3.9 |

## Structure du projet

```
Servlet_JSP_Ajax/
├── Dockerfile                         # Build Maven + image Tomcat finale
├── docker-compose.yaml                # Lance Tomcat + MariaDB
├── pom.xml
└── src/main/
    ├── java/com/api/
    ├── entities/                  # Entités JPA (Poster)
    │   ├── repositories/              # (prévu) couche d'accès aux données
    │   └── servlets/
    │       └── ApiServlet.java        # Servlet unique — point d'entrée de l'API
    ├── resources/
    │   └── META-INF/
    │       └── persistence.xml        # Configuration JPA / connexion BDD
    └── webapp/
        └── WEB-INF/
            └── web.xml
```

## Lancer l'application

### Prérequis

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) installé et démarré

### Démarrage

```bash
# Depuis le dossier Servlet_JSP_Ajax/
docker compose up --build
```

Cette commande :
1. Compile le projet Java avec Maven (dans un container)
2. Produit le fichier `api-posters.war`
3. Lance un container **MariaDB** (`poster-mariadb`)
4. Attend que MariaDB soit prêt (healthcheck)
5. Lance un container **Tomcat** (`poster-tomcat`) et y déploie le WAR

L'API est accessible sur : **http://localhost:8080**

### Arrêt

```bash
docker compose down
```

Pour supprimer aussi le volume de données MariaDB :

```bash
docker compose down -v
```

## Endpoints disponibles

| Méthode | Route | Description |
|---|---|---|
| GET | `/api/posters` | Liste tous les posters |
| GET | `/api/posters/{id}` | Récupère un poster par son id |
| POST | `/api/posters` | Crée un poster |
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

# Supprimer un poster
curl -X DELETE http://localhost:8080/api/posters/tt0111161
```

## Configuration de la base de données

Les paramètres de connexion sont définis dans deux endroits qui doivent rester cohérents :

- `docker-compose.yaml` → variables d'environnement `MARIADB_*`
- `src/main/resources/META-INF/persistence.xml` → propriétés `jakarta.persistence.jdbc.*`

Par défaut :

| Paramètre | Valeur |
|---|---|
| Hôte (dans Docker) | `mariadb` |
| Port | `3306` |
| Base | `posters_db` |
| Utilisateur | `posters_user` |
| Mot de passe | `posters_password` |

> Hibernate est configuré avec `hbm2ddl.auto=update` : le schéma est créé/mis à jour automatiquement au démarrage.

## Note sur le dossier `Servers/`

Ce dossier contient la configuration Eclipse WTP d'un Tomcat local. Il n'est **pas utilisé** par Docker et peut être ignoré ou supprimé.

## Développement sans Docker

Il est possible de tester localement en pointant `persistence.xml` vers une instance MariaDB locale, puis en déployant le WAR sur un Tomcat 10.1 installé sur la machine. Dans ce cas, le dossier `Servers/` retrouve son utilité si tu utilises Eclipse.

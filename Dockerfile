# ─────────────────────────────────────────────────────────────────────────────
# Étape 1 : build Maven — produit le fichier api-posters.war
# ─────────────────────────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app
COPY pom.xml .
# Télécharge les dépendances en cache séparé (optimise la reconstruction de l'image)
RUN mvn dependency:go-offline -q

COPY src ./src
RUN mvn package -DskipTests -q

# ─────────────────────────────────────────────────────────────────────────────
# Étape 2 : image Tomcat finale — déploie le WAR dans webapps/
# ─────────────────────────────────────────────────────────────────────────────
FROM tomcat:10.1-jdk17-temurin

# Supprime les webapps par défaut de Tomcat (ROOT, docs, examples, etc.)
RUN rm -rf /usr/local/tomcat/webapps/*

# Copie notre WAR et le déploie à la racine (ROOT.war → accessible sur /)
COPY --from=build /app/target/api-posters.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]

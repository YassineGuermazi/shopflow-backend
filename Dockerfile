# Étape 1 : Construction (Build) avec Maven
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Compile le code et crée le fichier .jar en ignorant les tests pour aller plus vite
RUN mvn clean package -DskipTests

# Étape 2 : Exécution (Run)
FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
# Copie le fichier .jar depuis l'étape 1
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

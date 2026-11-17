# ---------- Etapa 1: compila o projeto ----------
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Copia primeiro o Maven Wrapper e o pom (aproveita cache das dependências)
COPY mvnw pom.xml ./
COPY .mvn .mvn
# Corrige quebras de linha do Windows e dá permissão de execução
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw && ./mvnw -q dependency:go-offline

COPY src src
RUN ./mvnw -q package -DskipTests

# ---------- Etapa 2: imagem final, só com o Java e o .jar ----------
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

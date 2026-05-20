FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-noble

WORKDIR /app

COPY --from=builder /app/target/be-2048-spring-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8909

ENTRYPOINT ["java", "-jar", "app.jar"]

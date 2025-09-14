FROM eclipse-temurin:21-jdk AS builder

RUN groupadd -r appuser && useradd -r -g appuser appuser

WORKDIR /build

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src src

RUN chown -R appuser:appuser /build

USER appuser

RUN ./gradlew --version

RUN ./gradlew bootJar

FROM eclipse-temurin:21-jre

RUN groupadd -r appuser && useradd -r -g appuser appuser

WORKDIR /app

COPY --from=builder --chown=appuser:appuser /build/build/libs/app-0.0.1-SNAPSHOT.jar app.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=production"]
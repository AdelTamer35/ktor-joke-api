# 1. Build Stage
FROM gradle:8.5-jdk21 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN chmod +x gradlew
RUN ./gradlew buildFatJar --no-daemon

# 2. Run Stage
FROM eclipse-temurin:21-jre
EXPOSE 8080
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/joke-ktor.jar
ENTRYPOINT ["java", "-jar", "/app/joke-ktor.jar"]

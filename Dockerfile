# 1. Build Stage
FROM gradle:8.5-jdk21 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN ./gradlew buildFatJar --no-daemon

# 2. Run Stage
FROM openjdk:21-slim
EXPOSE 8080
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/joke-ktor.jar
ENTRYPOINT ["java", "-jar", "/app/joke-ktor.jar"]

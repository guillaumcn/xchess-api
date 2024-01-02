FROM eclipse-temurin:17-jammy
EXPOSE 8080
ARG JAR_FILE=target/engine.api-1.0.2.jar
ADD ${JAR_FILE} app.jar
RUN apt update && apt install -y stockfish && rm -rf /var/lib/apt/lists/*
ENV PATH="${PATH}:/usr/games"
ENTRYPOINT ["java","-jar","/app.jar"]
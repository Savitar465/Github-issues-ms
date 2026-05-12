FROM eclipse-temurin:17-jdk-alpine
LABEL org.opencontainers.image.title="Github-issues-ms" \
	org.opencontainers.image.description="Microservicio de issues, labels y comentarios" \
	org.opencontainers.image.vendor="Githubx" \
	org.opencontainers.image.url="https://github.com/dtarqui/Github-issues-ms" \
	org.opencontainers.image.source="https://github.com/dtarqui/Github-issues-ms" \
	org.opencontainers.image.documentation="https://github.com/dtarqui/Github-issues-ms/blob/main/README.md" \
	org.opencontainers.image.authors="dtarqui"
EXPOSE 8080
USER root

COPY target/Github-issues-ms-0.0.1-SNAPSHOT.jar Github-issues-ms.jar
ENTRYPOINT ["java","-jar","/Github-issues-ms.jar"]

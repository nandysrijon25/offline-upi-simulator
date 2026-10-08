FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/offline-upi-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]

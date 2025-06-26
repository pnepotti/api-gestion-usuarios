FROM openjdk:17-jdk-alpine

RUN apk add --no-cache bash

# Copia el script wait-for-it.sh al contenedor
COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Espera a que MySQL esté listo antes de arrancar la app
ENTRYPOINT ["/wait-for-it.sh", "mysql:3306", "--", "java", "-jar", "/app.jar"]
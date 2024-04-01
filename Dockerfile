FROM openjdk:1.8
WORKDIR /app
COPY target/calc-0.0.1-SNAPSHOT.jar calc-0.0.1-SNAPSHOT.jar
EXPOSE 8080
CMD [ "java", "-jar", "calc-0.0.1-SNAPSHOT.jar" ]

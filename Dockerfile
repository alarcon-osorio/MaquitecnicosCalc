FROM maven:3.6.3-jdk-8 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:8-jdk-alpine
COPY --from=build /target/calc-0.0.1-SNAPSHOT.jar calculator.jar
EXPOSE 8080
CMD [ "java", "-jar", "calculator.jar" ]

ENV JAVA_OPTS="-Xms512m -Xmx1g"

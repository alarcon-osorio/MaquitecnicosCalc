FROM maven:3.6.3-openjdk:8 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:8
COPY --from=build /target/calc-0.0.1-SNAPSHOT.jar calculator.jar
EXPOSE 8080
CMD [ "java", "-jar", "calculator.jar" ]
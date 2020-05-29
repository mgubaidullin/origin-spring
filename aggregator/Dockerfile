## Stage 1 : build with maven builder image
FROM maven:3.6.3-jdk-11 AS build
COPY . /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package

## Stage 2 : create the docker final image for aggregator service
FROM gcr.io/distroless/java:11 as aggregator
COPY --from=build /usr/src/app/target/aggregator-1.0-SNAPSHOT.jar /app/app.jar
WORKDIR /app
CMD ["app.jar"]
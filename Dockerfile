## Stage 1 : build with maven builder image
#FROM quay.io/quarkus/centos-quarkus-maven:20.1.0-java11 AS build
FROM maven:3.6.3-jdk-11 AS build
COPY . /usr/src/app
#USER root
#RUN chown -R quarkus /usr/src/app
#USER quarkus
RUN mvn -f /usr/src/app/pom.xml clean package

## Stage 2 : create the docker final image for rest service
FROM gcr.io/distroless/java:11 as rest
COPY --from=build /usr/src/app/rest/target/rest-1.0-SNAPSHOT.jar /app/app.jar
WORKDIR /app
CMD ["app.jar"]


## Stage 3 : create the docker final image for aggregate service
FROM gcr.io/distroless/java:11 as aggregator
COPY --from=build /usr/src/app/aggregator/target/aggregator-1.0-SNAPSHOT.jar /app/app.jar
WORKDIR /app
CMD ["app.jar"]

## Stage 4 : create the docker final image for storage service
FROM gcr.io/distroless/java:11 as storage
COPY --from=build /usr/src/app/storage/target/storage-1.0-SNAPSHOT.jar /app/app.jar
WORKDIR /app
CMD ["app.jar"]
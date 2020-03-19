FROM openjdk:8-jdk-alpine

COPY target/user-api-1.0-SNAPSHOT.jar user-api-1.0-SNAPSHOT.jar
COPY target/libs/ libs/

EXPOSE 8585

ENTRYPOINT ["java", "-jar", "user-api-1.0-SNAPSHOT.jar", "-cp", "libs/*" ]

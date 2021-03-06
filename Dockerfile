FROM openjdk:8-jdk-alpine

COPY target/user-api-1.0-SNAPSHOT.jar user-api-1.0-SNAPSHOT.jar
COPY target/libs/ libs/
COPY config/ config/
COPY ssl/ ssl/

EXPOSE 8585

# "--ssl-keystore", "ssl/localhost.keystore", "--ssl-password", "password",
ENTRYPOINT ["java", "-jar", "user-api-1.0-SNAPSHOT.jar", "--auth-htpasswd-path", "config/.htpasswd", "--config", "config/application.conf", "-cp", "libs/*" ]

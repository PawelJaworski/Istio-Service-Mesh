FROM openjdk:8-jdk-slim
VOLUME /tmp
ARG JAR_FILE
ADD ${JAR_FILE} loan-app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/loan-app.jar"]

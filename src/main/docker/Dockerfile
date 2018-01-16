FROM openjdk:8-jdk-alpine
VOLUME /tmp

ADD demo-server.jar /demo-server/demo-server.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/demo-server/demo-server.jar"]

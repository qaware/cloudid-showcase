FROM openjdk:8-jdk-alpine
VOLUME /tmp

ENV WORKDIR /deployment
ADD build/libs/demo-server.jar $WORKDIR/demo-server.jar
WORKDIR $WORKDIR

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "demo-server.jar"]

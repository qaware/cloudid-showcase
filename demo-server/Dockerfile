# Using openjdk:8-jdk to have a common, Debian-based base image with SPIRE.
FROM openjdk:8-jdk
VOLUME /tmp

ENV WORKDIR /deployment
ADD build/libs/demo-server.jar $WORKDIR/demo-server.jar
WORKDIR $WORKDIR

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "demo-server.jar"]

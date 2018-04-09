# Using openjdk:10-jdk-slim to have a common, Debian-based base image with SPIRE.
FROM openjdk:10-jdk-slim
VOLUME /tmp

ENV WORKDIR /deployment
ADD build/libs/cloudid-demo.jar $WORKDIR/cloudid-demo.jar
WORKDIR $WORKDIR

# Use non-blocking secure random to prevent lock-ups
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "cloudid-demo.jar"]

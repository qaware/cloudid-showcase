# Image is based on Debian Sid slim to speed up showcase deployment
# Sid slim is the common Debian base for this image and the Java parts which are based on openjdk:10-jdk
FROM debian:sid-slim

MAINTAINER Andreas Zitzelsberger<andreas.zitzelsberger@qaware.de>

ARG SPIRE_VERSION=0.4
ARG SPIRE_TGZ=https://github.com/spiffe/spire/releases/download/${SPIRE_VERSION}/spire-${SPIRE_VERSION}-linux-x86_64-glibc.tar.gz

# Install required packages for SPIRE installation
RUN apt-get update \
  && apt-get install -y --no-install-recommends ca-certificates curl tar gzip \
  && apt-get autoremove -y \
  && rm -rf /var/lib/apt/lists/*

# Install SPIRE
RUN curl --silent --location $SPIRE_TGZ | tar --directory /opt -xzf - \
    && mv /opt/spire-${SPIRE_VERSION} /opt/spire

# Install the agent start script
COPY start-agent.sh /opt/spire

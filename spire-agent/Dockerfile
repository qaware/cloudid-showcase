FROM debian:stable-slim
MAINTAINER Christian Fritz<christian.fritz@qaware.de>

RUN apt-get update \
  && apt-get install -y --no-install-recommends ca-certificates curl tar gzip \
  && apt-get autoremove -y \
  && rm -rf /var/lib/apt/lists/*

ARG SPIRE_VERSION=0.4
ENV SPIRE_TGZ=https://github.com/spiffe/spire/releases/download/${SPIRE_VERSION}/spire-${SPIRE_VERSION}-linux-x86_64-glibc.tar.gz

EXPOSE 8080
VOLUME /spire/agent/socket

RUN curl --silent --location $SPIRE_TGZ \
    | tar --directory /opt -xzf - spire-${SPIRE_VERSION}/spire-agent spire-${SPIRE_VERSION}/spire-server spire-${SPIRE_VERSION}/plugin/agent \
    && mv /opt/spire-${SPIRE_VERSION} /opt/spire

COPY start.sh /opt/

CMD /opt/start.sh

FROM debian:stable-slim
MAINTAINER Christian Fritz<christian.fritz@qaware.de>

RUN apt-get update \
  && apt-get install -y --no-install-recommends ca-certificates curl tar gzip \
  && apt-get autoremove -y \
  && rm -rf /var/lib/apt/lists/*

EXPOSE 8080 8081

ARG SPIRE_VERSION=0.4
ENV SPIRE_TGZ=https://github.com/spiffe/spire/releases/download/${SPIRE_VERSION}/spire-${SPIRE_VERSION}-linux-x86_64-glibc.tar.gz

RUN curl --silent --location $SPIRE_TGZ \
    | tar --directory /opt -xzf - spire-${SPIRE_VERSION}/spire-server spire-${SPIRE_VERSION}/plugin/server \
    && mv /opt/spire-${SPIRE_VERSION} /opt/spire

CMD /opt/spire/spire-server

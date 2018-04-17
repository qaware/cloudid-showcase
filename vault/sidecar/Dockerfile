# Image is based on Debian Sid slim to speed up showcase deployment
FROM debian:sid-slim

ARG HELPER_VERSION=0.2
ARG HELPER_TGZ=https://github.com/spiffe/spiffe-helper/releases/download/${HELPER_VERSION}/sidecar_${HELPER_VERSION}_linux_amd64.tar.gz

# Install required packages for SPIFFE Helper installation
RUN apt-get update \
  && apt-get install -y --no-install-recommends ca-certificates curl tar gzip \
  && apt-get autoremove -y \
  && rm -rf /var/lib/apt/lists/*

RUN mkdir -p /sidecar

# Install helper
RUN curl --silent --location $HELPER_TGZ | tar --directory /opt -xzf - \
    && mv /opt/sidecar /sidecar

WORKDIR /sidecar

COPY start-helper.sh start-helper.sh
COPY helper.conf helper.conf
COPY helper-cmd.sh helper-cmd.sh

RUN chmod +x start-helper.sh helper-cmd.sh

CMD ["./start-helper.sh"]
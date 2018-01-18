#FROM alpine:3.7
FROM spire-agent

MAINTAINER Christian Fritz<christian.fritz@qaware.de>

#RUN apk add --no-cache curl tar && \
RUN apt-get update \
  && apt-get install -y --no-install-recommends socat \
  && apt-get autoremove -y \
  && rm -rf /var/lib/apt/lists/*

VOLUME /spire/socket

CMD socat -d -d TCP-L:8082,fork UNIX:/spire/socket/agent.sock
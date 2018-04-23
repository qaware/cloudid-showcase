#!/bin/bash

if [ "$#" -ne 3 ];
then
    echo "Usage: $0 [CONFIG_FILE] [TRUST_DOMAIN] [SPIRE_SERVER]"
    exit 1
fi

CONFIG_FILE=$1
TRUST_DOMAIN=$2
SPIRE_SERVER=$3

BACKOFF_S=4

# Agent terminates if no server is available
# This may lead to long wait times if K8s determines to backoff from recreating the pod
while true
do
    # Get the node name from the K8S API
    NODE_NAME=$(curl -Gs http://localhost:10255/pods/ | grep -o '"nodeName":"[^"]*"' | head -n 1 | cut -d : -f 2- | tr -d '"')
    # Register this node at the SPIRE server
    JOIN_TOKEN=$(/opt/spire/spire-server token generate -spiffeID spiffe://${TRUST_DOMAIN}/k8s/node/${NODE_NAME} -serverAddr ${SPIRE_SERVER} | cut -d : -f 2- | tr -d ' ')

    echo /opt/spire/spire-agent run -config ${CONFIG_FILE} joinToken ${JOIN_TOKEN}

    /opt/spire/spire-agent run -config ${CONFIG_FILE} -joinToken ${JOIN_TOKEN}

    echo "SPIRE agent terminated, backing off for ${BACKOFF_S}s"
    sleep ${BACKOFF_S}
done

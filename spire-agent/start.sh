#!/bin/bash

if [ "$#" -ne 3 ];
then
    echo "Usage: start.sh [CONFIG_FILE] [TRUST_DOMAIN] [SPIRE_SERVER]"
    exit 1
fi

CONFIG_FILE=$1
TRUST_DOMAIN=$2
SPIRE_SERVER=$3

# Get the node name from the K8S API
NODE_NAME=$(curl -Gs http://localhost:10255/pods/ | grep -o '"nodeName":"[^"]*"' | head -n 1 | cut -d : -f 2- | tr -d '"')

# Shortcut for Minikube: Create a join token using the local spire server.
# Proper deployment should use another node attestor or generate the tokens centrally for each node, then provision them
# using K8S secrets.
JOIN_TOKEN=$(/opt/spire/spire-server token generate -spiffeID spiffe://${TRUST_DOMAIN}/k8s/node/${NODE_NAME} -serverAddr ${SPIRE_SERVER} | cut -d : -f 2- | tr -d ' ')

/opt/spire/spire-agent run -config /spire/config/agent.conf -joinToken ${JOIN_TOKEN}

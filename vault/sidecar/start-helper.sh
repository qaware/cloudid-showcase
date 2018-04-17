#!/bin/bash

echo "Starting SPIFFE Helper / Sidecar script"
echo "Waiting until token for configuring Vault is available..."
# Wait until root token has been added to the vault-secrets secret and has been refreshed by kubernetes
while [ ! -f /shared/token.txt ]
do
  sleep 1
done

# Directory which will be used by SPIFFE helper to temporarily save the fetched certs
mkdir -p certs

# Run SPIFFE Helper

echo "Trying to start SPIFFE helper..."

attempt=0
max_attempts=30

# Try it until the socket is ready
while [[ ${attempt} < ${max_attempts} ]]; do
# || true to ignore exit value of sidecar
# Sidecar fails initially if either the socket is not available yet or zero bundles were returned
./sidecar -config helper.conf || true
# Simple backoff
sleep $(( attempt * 2 + 1 ))
attempt=$(( attempt + 1 ))
done

# Pod will restart entirely if this point is reached
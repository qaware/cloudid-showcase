#!/bin/bash

VAULT_HOST="localhost"
VAULT_PORT="8300"
VAULT_API_URL="http://${VAULT_HOST}:${VAULT_PORT}/v1"

echo "Waiting until Vault is ready..."
# Waiting until Vault exit code = 501 -> meaning that Vault is ready for initialization
while [[ "$(curl -s ${VAULT_API_URL}/sys/health -I 2>/dev/null | head -n 1 | cut -d$' ' -f2)" != "501" ]]; do
sleep 2
done

echo "Initializing Vault..."

# Init Vault
curl -s ${VAULT_API_URL}/sys/init --request PUT --data '{"secret_shares":1,"secret_threshold":1}' > /vault/init/vault-init-response.json
ROOT_TOKEN=$(sed -E 's/.*"root_token":"([^"]*).*/\1/' /vault/init/vault-init-response.json)

# Unseal vault
curl -s ${VAULT_API_URL}/sys/unseal --request PUT --data "{\"key\": \"$(sed -E 's/.*"keys":\["([^"]*).*/\1/' /vault/init/vault-init-response.json)\"}"
# Enabling cert auth
curl -s ${VAULT_API_URL}/sys/auth/cert -H "X-Vault-Token: ${ROOT_TOKEN}" --request POST --data "{\"type\": \"cert\",\"description\": \"Verification via the TLS Certificate Auth Method\"}"
# Adding read policy for acl secret
curl -s ${VAULT_API_URL}/sys/policy/acl -H "X-Vault-Token: ${ROOT_TOKEN}" --request PUT --data '{"policy": "path \"secret/acl\" {capabilities = [\"read\"]}"}'
# Writing the acl secret
curl -s ${VAULT_API_URL}/secret/acl -H "X-Vault-Token: ${ROOT_TOKEN}" --request PUT --data @/vault/secrets/acl_payload.json

# Save (root) token for sidecar
echo ${ROOT_TOKEN} > /shared/token.txt

echo "Done!"
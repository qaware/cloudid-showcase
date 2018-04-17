#!/bin/bash

TOKEN=$(</shared/token.txt)
VAULT_API_URL=http://localhost:8300/v1

echo "Adding SPIRE cert to Vault"
curl -s ${VAULT_API_URL}/auth/cert/certs/spire -H "X-Vault-Token: ${TOKEN}" --request PUT --data "{\"certificate\": \"$(awk 1 ORS='\\n' certs/svid_bundle.pem)\", \"display_name\": \"SPIRE Certificate\", \"policies\": \"acl\"}"
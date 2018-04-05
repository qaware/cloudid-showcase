PROJECT_NAME = vault
DEPLOYMENTS =  k8s/configmap.yaml k8s/deployment.yaml k8s/service.yaml

VAULT_HOST = localhost
VAULT_PORT = 8300
VAULT_API_URL = http://$(VAULT_HOST):$(VAULT_PORT)/v1


.PHONY: deploy-only
deploy-only:
	sed "s/{{VAULT_KEY}}/$$(base64 secrets/vault-key.pem | tr -d '\r\n')/; \
		 s/{{VAULT_CERT}}/$$(base64 secrets/vault.pem | tr -d '\r\n')/" \
		 k8s/secrets.yaml | kubectl apply -f -
	$(foreach f, $(DEPLOYMENTS), kubectl apply -f $f;)

.PHONY: deploy
deploy: deploy-only unseal-vault


.PHONY: delete
delete:
	-$(foreach f, $(DEPLOYMENTS), kubectl delete -f $f;)
	kubectl delete secret vault-secrets
	rm -f vault-init-response.json


.PHONY: unseal-vault
unseal-vault:
	# Wait for Vault to become running
	while ! kubectl get pod  | grep 'vault.*Running' > /dev/null; do sleep 1; done
	kubectl port-forward $$(kubectl get pod -o name | grep -o 'vault.*$$') $(VAULT_PORT) & echo $$! > port-forward.pid

	# Wait for the port-forward to become active
	while ! nc -z $(VAULT_HOST) $(VAULT_PORT); do sleep 1; done

	# Init Vault only if there is no response file
	-[ ! -f "vault-init-response.json" ] && curl $(VAULT_API_URL)/sys/init --request PUT --data '{"secret_shares":1,"secret_threshold":1}' > vault-init-response.json

	# Unseal vault
	-curl $(VAULT_API_URL)/sys/unseal --request PUT --data "{\"key\": \"$$(sed -E 's/.*"keys":\["([^"]*).*/\1/' vault-init-response.json)\"}"
	# Enabling cert auth (error can be ignored if already enabled)
	-curl $(VAULT_API_URL)/sys/auth/cert -H "X-Vault-Token: $$(sed -E 's/.*"root_token":"([^"]*).*/\1/' vault-init-response.json)" --request POST --data "{\"type\": \"cert\",\"description\": \"Verfication via the TLS Certificate Auth Method\"}"
	# Adding read policy for acl secret
	-curl $(VAULT_API_URL)/sys/policy/acl -H "X-Vault-Token: $$(sed -E 's/.*"root_token":"([^"]*).*/\1/' vault-init-response.json)" --request PUT --data '{"policy": "path \"secret/acl\" {capabilities = [\"read\"]}"}'
	# Writing the acl secret
	-curl $(VAULT_API_URL)/secret/acl -H "X-Vault-Token: $$(sed -E 's/.*"root_token":"([^"]*).*/\1/' vault-init-response.json)" --request PUT --data '{"acl": "spiffe://salm.qaware.de/cloudid/front -> spiffe://salm.qaware.de/cloudid/back\nspiffe://salm.qaware.de/cloudid/proxy -> spiffe://salm.qaware.de/cloudid/front"}'
	# Add the upstream cert to cert auth
	-curl $(VAULT_API_URL)/auth/cert/certs/upstream -H "X-Vault-Token: $$(sed -E 's/.*"root_token":"([^"]*).*/\1/' vault-init-response.json)" --request PUT --data "{\"certificate\": \"$$(awk 1 ORS='\\n' ../upstream-ca/ca.pem)\", \"display_name\": \"upstream ca\", \"policies\": \"acl\"}"
	# Cleanup
	kill -9 $$(cat port-forward.pid)
	rm -f port-forward.pid

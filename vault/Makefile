PROJECT_NAME = vault
DEPLOYMENTS =  k8s/configmap.yaml k8s/deployment.yaml k8s/service.yaml

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
	kubectl port-forward $$(kubectl get pod -o name | grep -o 'vault.*$$') 8300 & echo $$! > port-forward.pid
	# Wait for the port-forward to become active
	while ! nc -z localhost 8300; do sleep 1; done
	# Init Vault only if there is no response file
	-[ ! -f "vault-init-response.json" ] && curl http://localhost:8300/v1/sys/init --request PUT --data '{"secret_shares":1,"secret_threshold":1}' > vault-init-response.json
	# Unseal vault
	-curl http://localhost:8300/v1/sys/unseal --request PUT --data "{\"key\": \"$$(cat vault-init-response.json | sed -E 's/.*"keys":\["([^"]*).*/\1/')\"}"
	# Cleanup
	kill -9 $$(cat port-forward.pid)
	rm -f port-forward.pid


PROJECT_NAME = vault

K8S_NAMESPACE = vault

VAULT_HOST = localhost
VAULT_PORT = 8300
VAULT_API_URL = http://$(VAULT_HOST):$(VAULT_PORT)/v1

DOCKER_QUALIFIED_NAME_VAULT = cloudid-vault:0.1-SNAPSHOT
DOCKER_QUALIFIED_NAME_SIDECAR = cloudid-vault-sidecar:0.1-SNAPSHOT


.PHONY: deploy
deploy: deploy-only container-build


.PHONY: container-build
container-build:
	docker build -t "$(DOCKER_QUALIFIED_NAME_VAULT)" .
	docker build -t "$(DOCKER_QUALIFIED_NAME_SIDECAR)" sidecar

.PHONY: deploy-only
deploy-only:
	kubectl create namespace $(K8S_NAMESPACE)

	$(foreach f, $(wildcard k8s/*.yaml), \
		sed "s/{{VAULT_KEY}}/$$(base64 secrets/vault-key.pem | tr -d '\r\n')/; \
        		 s/{{UPSTREAM_CERT}}/$$(base64 ../upstream-ca/ca.pem | tr -d '\r\n')/; \
        		 s/{{ACL_PAYLOAD}}/$$(echo "{\"acl\":\"$$(awk 'BEGIN{ORS="\\\\n"}{print}' acl.txt)\"}" | base64 | tr -d '\r\n')/; \
        		 s/{{VAULT_CERT}}/$$(base64 secrets/vault.pem | tr -d '\r\n')/" \
        		 $f | kubectl apply -n $(K8S_NAMESPACE) -f -;)


.PHONY: delete
delete:
	-kubectl delete namespace $(K8S_NAMESPACE)

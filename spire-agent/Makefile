PROJECT_NAME = spire-agent
DEPLOYMENTS =  k8s/daemonset.yaml k8s/configmap.yaml

.PHONY: deploy
deploy: container-build
	sed "s/{{UPSTREAM_CA}}/$$(base64 ../upstream-ca/ca.pem | tr -d '\r\n')/" \
		 k8s/secrets.yaml | kubectl apply -f -
	$(foreach f, $(DEPLOYMENTS), kubectl apply -f $f;)

.PHONY: container-build
container-build:
	docker build -t $(PROJECT_NAME) .

.PHONY: delete
delete:
	-kubectl delete secret spire-agent-secrets
	-$(foreach f, $(DEPLOYMENTS), kubectl delete -f $f;)

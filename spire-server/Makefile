PROJECT_NAME = spire-server
DEPLOYMENTS =  k8s/configmap.yaml k8s/deployment.yaml k8s/service.yaml

.PHONY: deploy
deploy: container-build
	sed "s/{{UPSTREAM_CA_KEY}}/$$(base64 ../upstream-ca/ca-key.pem | tr -d '\r\n')/; \
		 s/{{UPSTREAM_CA}}/$$(base64 ../upstream-ca/ca.pem | tr -d '\r\n')/" \
		 k8s/secrets.yaml | kubectl apply -f -
	$(foreach f, $(DEPLOYMENTS), kubectl apply -f $f;)

.PHONY: container-build
container-build:
	$(MAKE) -C ../spire

.PHONY: delete
delete:
	-kubectl delete secret spire-server-secrets
	-$(foreach f, $(DEPLOYMENTS), kubectl delete -f $f;)

clean:
	rm -rf build

PROJECT_NAME = spire-server
DEPLOYMENTS =  k8s/configmap.yaml k8s/deployment.yaml k8s/service.yaml

.PHONY: deploy
deploy: container-build
	sed "s/{{TLS.KEY}}/$$(base64 ca/tls.key | tr -d '\r\n')/; \
		 s/{{TLS.CRT}}/$$(base64 ca/tls.crt | tr -d '\r\n')/" \
		 k8s/secrets.yaml | kubectl apply -f -
	$(foreach f, $(DEPLOYMENTS), kubectl apply -f $f;)

.PHONY: container-build
container-build:
	docker build -t $(PROJECT_NAME) .

.PHONY: delete
delete:
	-$(foreach f, $(DEPLOYMENTS), kubectl delete -f $f;)

clean:
	rm -rf build

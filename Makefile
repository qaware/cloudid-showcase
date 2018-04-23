BUILDS = cloudid
CONTAINERS = spire-agent spire-server cloudid
DEPLOYMENTS = spire-agent spire-server vault cloudid

.PHONY: deploy
deploy:
	$(foreach c, $(DEPLOYMENTS), $(MAKE) -C $c deploy;)

.PHONY: container-build
container-build:
	$(foreach c, $(CONTAINERS), $(MAKE) -C $c container-build;)

.PHONY: build
build:
	$(foreach c, $(BUILDS), $(MAKE) -C $c build;)

.PHONY: delete
delete:
	-$(foreach c, $(DEPLOYMENTS), $(MAKE) -C $c delete;)

.PHONY: spire-register
spire-register:
	# Wait for the SPIRE server to become "Running"
	while ! kubectl get pod  | grep 'spire-server.*Running' > /dev/null; do sleep 2; done

	# HTTP Proxy
	kubectl exec $$(kubectl get pod | grep -Eo 'spire-server\S*') -- \
		/opt/spire/spire-server entry create \
		-parentID spiffe://cloudid.qaware.de/k8s/node/minikube \
		-spiffeID spiffe://cloudid.qaware.de/http-proxy \
		-selector k8s:ns:outer

	# HTTPs Proxy
	kubectl exec $$(kubectl get pod | grep -Eo 'spire-server\S*') -- \
		/opt/spire/spire-server entry create \
		-parentID spiffe://cloudid.qaware.de/k8s/node/minikube \
		-spiffeID spiffe://cloudid.qaware.de/proxy \
		-selector k8s:ns:middle

	# Demo Server
	kubectl exec $$(kubectl get pod | grep -Eo 'spire-server\S*') -- \
		/opt/spire/spire-server entry create \
		-parentID spiffe://cloudid.qaware.de/k8s/node/minikube \
		-spiffeID spiffe://cloudid.qaware.de/server \
		-selector k8s:ns:inner

	# Vault
	kubectl exec $$(kubectl get pod | grep -Eo 'spire-server\S*') -- \
		/opt/spire/spire-server entry create \
		-parentID spiffe://cloudid.qaware.de/k8s/node/minikube \
		-spiffeID spiffe://cloudid.qaware.de/vault \
		-selector k8s:ns:vault

.PHONY: minikube-container-build
minikube-container-build:
	@eval $$(minikube docker-env); $(MAKE) container-build

.PHONY: minikube-deploy
minikube-deploy:
	@eval $$(minikube docker-env); $(MAKE) deploy

.PHONY: minikube-deploy-and-register
minikube-deploy-and-register: minikube-deploy spire-register

.PHONY: minikube-test-service
minikube-test-service:
	curl http://$$(minikube ip)/

.PHONY: minikube-test-service-url
minikube-test-service-url:
	@echo http://$$(minikube ip)/

.PHONY: delete-cloudid-pods
delete-cloudid-pods:
	kubectl -n back delete pod $$(kubectl -n back get pod -o name | grep -o 'cloudid.*$$')
	kubectl delete pod $$(kubectl get pod -o name | grep -o 'cloudid.*$$')

.PHONY: minikube-start
minikube-start:
	minikube start --memory 6144 --cpus 2

.PHONY: minikube-stop
minikube-stop:
	minikube stop

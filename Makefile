BUILDS = demo-server
CONTAINERS = spire-agent spire-server demo-server
DEPLOYMENTS = spire-agent spire-server vault demo-server

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

.PHONY: minikube-container-build
minikube-container-build:
	@eval $$(minikube docker-env); $(MAKE) container-build

.PHONY: minikube-deploy
minikube-deploy:
	@eval $$(minikube docker-env); $(MAKE) deploy
	kubectl exec $$(kubectl get pod -o name | grep -o 'spire-server.*$$') -- /opt/spire/spire-server register -parentID spiffe://salm.qaware.de/k8s/node/minikube -spiffeID spiffe://salm.qaware.de/host/workload -selector k8s:ns:default

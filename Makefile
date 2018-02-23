BUILDS = demo-server
CONTAINERS = spire-agent spire-server demo-server
DEPLOYMENTS = spire-agent spire-server vault demo-server

.PHONY: deploy
deploy: container-build
	$(foreach c, $(DEPLOYMENTS), $(MAKE) -C $c deploy;)

.PHONY: container-build
container-build: build
	$(foreach c, $(CONTAINERS), $(MAKE) -C $c container-build;)

.PHONY: build
build:
	$(foreach c, $(BUILDS), $(MAKE) -C $c build;)

.PHONY: delete
delete:
	-$(foreach c, $(DEPLOYMENTS), $(MAKE) -C $c delete;)

.PHONY: container-build-minikube
container-build-minikube: start-minikube
	@eval $$(minikube docker-env); $(MAKE) container-build

.PHONY: deploy-minikube
deploy-minikube: start-minikube
	@eval $$(minikube docker-env); $(MAKE) deploy

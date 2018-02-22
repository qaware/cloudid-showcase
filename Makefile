CONTAINERS = spire-agent spire-server demo-server
DEPLOYMENTS = spire-agent spire-server vault demo-server

.PHONY:
deploy: container-build
	$(foreach c, $(DEPLOYMENTS), $(MAKE) -C $c deploy;)

.PHONY:
container-build: build
	$(foreach c, $(CONTAINERS), $(MAKE) -C $c container-build;)

.PHONY:
build:
	$(MAKE) -C demo-server build

.PHONY:
delete:
	-$(foreach c, $(DEPLOYMENTS), $(MAKE) -C $c delete;)

.ONESHELL:
container-build-minikube:
	@eval $$(minikube docker-env); $(MAKE) container-build

.ONESHELL:
deploy-minikube:
	@eval $$(minikube docker-env); $(MAKE) deploy

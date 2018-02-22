CONTAINERS = spire-agent spire-server demo-server
DEPLOYMENTS = spire-agent spire-server vault demo-server

.PHONY:
deploy: containerize
	$(foreach c, $(DEPLOYMENTS), $(MAKE) -C $c deploy;)

.PHONY:
containerize: build
	$(foreach c, $(CONTAINERS), $(MAKE) -C $c containerize;)

.PHONY:
build:
	$(MAKE) -C demo-server build

.PHONY:
delete:
	-$(foreach c, $(DEPLOYMENTS), $(MAKE) -C $c delete;)

.ONESHELL:
containerize-minikube:
	@eval $$(minikube docker-env); $(MAKE) containerize

.ONESHELL:
deploy-minikube:
	@eval $$(minikube docker-env); $(MAKE) deploy

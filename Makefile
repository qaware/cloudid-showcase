BUILDS = cloudid
CONTAINERS = spire vault cloudid
DEPLOYMENTS = spire vault cloudid

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

.PHONY: minikube-test-service
minikube-test-service:
	curl http://$$(minikube ip)/

.PHONY: minikube-test-service-url
minikube-test-service-url:
	@echo http://$$(minikube ip)/

.PHONY: minikube-start
minikube-start:
	minikube start --memory 6144 --cpus 2

.PHONY: minikube-stop
minikube-stop:
	minikube stop

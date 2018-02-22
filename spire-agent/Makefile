PROJECT_NAME = spire-agent
DEPLOYMENTS =  $(wildcard k8s/*.yaml)

.PHONY:
deploy: container-build
	$(foreach f, $(DEPLOYMENTS), kubectl apply -f $f;)

.PHONY:
container-build:
	docker build -t $(PROJECT_NAME) .

.PHONY:
delete:
	-$(foreach f, $(DEPLOYMENTS), kubectl delete -f $f;)

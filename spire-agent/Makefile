PROJECT_NAME = spire-agent
DEPLOYMENTS =  $(wildcard k8s/*.yaml)

.PHONY:
deploy: containerize
	$(foreach f, $(DEPLOYMENTS), kubectl apply -f $f;)

.PHONY:
containerize:
	docker build -t $(PROJECT_NAME) .

.PHONY:
delete:
	-$(foreach f, $(DEPLOYMENTS), kubectl delete -f $f;)

PROJECT_NAME = demo-server
DEPLOYMENTS =  $(wildcard k8s/*.yaml)

.PHONY:
deploy: containerize
	$(foreach f, $(DEPLOYMENTS), kubectl apply -f $f;)

.PHONY:
containerize: build
	docker build -t $(PROJECT_NAME) .

.PHONY:
build:
	./gradlew build

.PHONY:
delete:
	-$(foreach f, $(DEPLOYMENTS), kubectl delete -f $f;)

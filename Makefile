PROJECT_NAME = demo-server

DEPLOYMENTS =  $(wildcard k8s/*.yaml)
BRANCH = $(shell git rev-parse --abbrev-ref HEAD)

DOCKER_REGISTRY_URL = artifactory.qaware.de
DOCKER_REGISTRY_REPO = qaware-internal-docker
DOCKER_IMAGE_NAME = $(PROJECT_NAME)

ifeq ($(BRANCH), master)
DOCKER_TAG_NAME = latest
else
DOCKER_TAG_NAME = $(BRANCH)
endif

DOCKER_QUALIFIED_NAME = $(DOCKER_REGISTRY_URL)/$(DOCKER_REGISTRY_REPO)/$(DOCKER_IMAGE_NAME):$(DOCKER_TAG_NAME)

ifneq ($(BRANCH), master)
SONAR_BRANCH_NAME = -Dsonar.branch.name=$(BRANCH)
endif

test:
	echo $(X)

.PHONY: deploy
deploy: container-build
	$(foreach f, $(DEPLOYMENTS), kubectl apply -f $f;)

.PHONY: container-build
container-build: build
	docker build -t "$(DOCKER_QUALIFIED_NAME)" .

.PHONY: container-push
container-push: container-build
	@echo docker login $(DOCKER_REGISTRY_USER)@$(DOCKER_REGISTRY_URL)
	@docker login -u "$(DOCKER_REGISTRY_USER)" -p "$(DOCKER_REGISTRY_PASS)" $(DOCKER_REGISTRY_URL)
	docker push "$(DOCKER_QUALIFIED_NAME)"

.PHONY: build
build:
	./gradlew $(GRADLE_ARGS) build

.PHONY: assemble
assemble:
	./gradlew $(GRADLE_ARGS) assemble

.PHONY: sonar
sonar:
	./gradlew $(GRADLE_ARGS) sonarqube "$(SONAR_BRANCH_NAME)" "-Dsonar.host.url=$(SONAR_URL)" "-Dsonar.login=$(SONAR_TOKEN)"

.PHONY: delete
delete:
	-$(foreach f, $(DEPLOYMENTS), kubectl delete -f $f;)

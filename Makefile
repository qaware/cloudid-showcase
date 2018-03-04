SHELL = /bin/bash

PROJECT_NAME = demo-server

DEPLOYMENTS =  $(wildcard k8s/*.yaml)

# Deployments in the default namespace
DEPLOYMENTS_DEFAULT = $(wildcard k8s/default/*.yaml)
# Deployments in the back namespace
DEPLOYMENTS_BACK = $(wildcard k8s/back/*.yaml)

ifdef CI
# Branch and tag names from GitLab
BRANCH_NAME=$(CI_COMMIT_REF_NAME)
TAG_NAME=$(CI_COMMIT_TAG)
else
# Branch name from Git
BRANCH_NAME = $(shell git rev-parse --abbrev-ref HEAD)
# Tag name from Git, if the current commit is tagged, stripping a leading "v" from v1.0-style commits
TAG_NAME = $(shell git describe --exact-match HEAD 2> /dev/null\
	| grep '^v[0-9][0-9a-zA-Z_\.]*$$' | sed 's/^v//' \
	|| echo '' )
endif


# Docker tag name
# if the current branch is "master" and the current commit is a tag -> tag name
# if the current branch is "master" -> "latest"
# any other branch -> branch name
DOCKER_TAG_NAME = $(shell \
	[ "$(BRANCH_NAME)" = "master" ] \
	&& ( [ -n "$(TAG_NAME)" ] && echo "$(TAG_NAME)" || echo "latest" ) \
	|| echo "$(BRANCH_NAME)")

DOCKER_REGISTRY_URL = artifactory.qaware.de
DOCKER_REGISTRY_REPO = qaware-internal-docker
DOCKER_IMAGE_NAME = $(PROJECT_NAME)
DOCKER_QUALIFIED_NAME = $(DOCKER_REGISTRY_URL)/$(DOCKER_REGISTRY_REPO)/$(DOCKER_IMAGE_NAME):$(DOCKER_TAG_NAME)

# Sonar branch name, empty on master
ifneq ($(BRANCH_NAME), master)
SONAR_BRANCH_NAME = -Dsonar.branch.name=$(BRANCH_NAME)
endif


.PHONY: deploy
deploy: container-build
	# Create back namespace
	kubectl apply -f k8s/namespace-back.yaml

	# Deploy in back namespace
	$(foreach f, $(DEPLOYMENTS_BACK), \
		sed 's/$(DOCKER_IMAGE_NAME):latest/$(DOCKER_IMAGE_NAME):$(DOCKER_TAG_NAME)/' $f \
		| kubectl apply --namespace=back -f -;)

	# Deploy in default namespace
	# Update versioned references to the docker image to the current tag / branch
	$(foreach f, $(DEPLOYMENTS_DEFAULT), \
		sed 's/$(DOCKER_IMAGE_NAME):latest/$(DOCKER_IMAGE_NAME):$(DOCKER_TAG_NAME)/' $f \
		| kubectl apply -f -;)


.PHONY: container-build
container-build: build
	docker build -t "$(DOCKER_QUALIFIED_NAME)" .

.PHONY: container-push
container-push: container-build
	@echo docker login $(DOCKER_REGISTRY_USER)@$(DOCKER_REGISTRY_URL)
	@docker login -u "$(DOCKER_REGISTRY_USER)" -p "$(DOCKER_REGISTRY_PASS)" $(DOCKER_REGISTRY_URL)
	docker push "$(DOCKER_QUALIFIED_NAME)"

.PHONY: build
build: assemble

.PHONY: assemble
assemble:
	./gradlew $(GRADLE_ARGS) assemble

.PHONY: sonar
sonar:
	./gradlew $(GRADLE_ARGS) sonarqube $(SONAR_BRANCH_NAME) "-Dsonar.host.url=$(SONAR_URL)" "-Dsonar.login=$(SONAR_TOKEN)"

.PHONY: delete
delete:
	-$(foreach f, $(DEPLOYMENTS_DEFAULT), kubectl delete -f $f;)
	-$(foreach f, $(DEPLOYMENTS_BACK), kubectl delete -f $f;)
	-kubectl delete -f k8s/namespace-back.yaml

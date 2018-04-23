SHELL = /bin/bash

PROJECT_NAME = cloudid

ifdef CYGWIN_VERSION
GRADLEW=./gradlew.bat
else
GRADLEW=./gradlew
endif

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


ifndef DOCKER_REGISTRY_URL
DOCKER_REGISTRY_URL = artifactory.qaware.de
endif

ifndef DOCKER_REGISTRY_REPO
DOCKER_REGISTRY_REPO = qaware-qalabs-docker
endif

ifndef DOCKER_IMAGE_NAME
DOCKER_IMAGE_NAME = $(PROJECT_NAME)-proxy
endif

ifndef DOCKER_IMAGE_NAME_DEMO
DOCKER_IMAGE_NAME_DEMO = $(PROJECT_NAME)-demo
endif

DOCKER_QUALIFIED_NAME = $(DOCKER_REGISTRY_URL)/$(DOCKER_REGISTRY_REPO)/$(DOCKER_IMAGE_NAME):$(DOCKER_TAG_NAME)
DOCKER_QUALIFIED_NAME_DEMO = $(DOCKER_REGISTRY_URL)/$(DOCKER_REGISTRY_REPO)/$(DOCKER_IMAGE_NAME_DEMO):$(DOCKER_TAG_NAME)

# Sonar branch name, empty on master
ifneq ($(BRANCH_NAME), master)
SONAR_BRANCH_NAME = -Dsonar.branch.name=$(BRANCH_NAME)
endif

.PHONY: deploy-only
deploy-only:
	# Create namespaces
	kubectl create namespace inner
	kubectl create namespace middle
	kubectl create namespace outer

	# Deployments in the "inner" namespace
	$(foreach f, $(wildcard k8s/inner/*.yaml), \
		sed 's|{{DOCKER_QUALIFIED_NAME}}|$(DOCKER_QUALIFIED_NAME_DEMO)|' $f | \
		sed "s|{{ADDITIONAL_TRUSTSTORE}}|$$(base64 secrets/additional_truststore.jks | tr -d '\r\n')|" | \
		kubectl apply --namespace=inner -f -;)

	# Deployments in the "middle" namespace
	$(foreach f, $(wildcard k8s/middle/*.yaml), \
		sed 's|{{DOCKER_QUALIFIED_NAME}}|$(DOCKER_QUALIFIED_NAME)|' $f | \
		sed "s|{{ADDITIONAL_TRUSTSTORE}}|$$(base64 secrets/additional_truststore.jks | tr -d '\r\n')|" | \
		kubectl apply --namespace=middle -f -;)

	# Deployments in the "outer" namespace
	$(foreach f, $(wildcard k8s/outer/*.yaml), \
		sed 's|{{DOCKER_QUALIFIED_NAME}}|$(DOCKER_QUALIFIED_NAME)|' $f | \
		sed "s|{{ADDITIONAL_TRUSTSTORE}}|$$(base64 secrets/additional_truststore.jks | tr -d '\r\n')|" | \
		kubectl apply --namespace=outer -f -;)


.PHONY: deploy
deploy: container-build deploy-only


.PHONY: container-build
container-build: build
	docker build -t "$(DOCKER_QUALIFIED_NAME)" cloudid-proxy
	docker build -t "$(DOCKER_QUALIFIED_NAME_DEMO)" cloudid-demo

.PHONY: container-push
container-push: container-build
	@echo docker login $(DOCKER_REGISTRY_USER)@$(DOCKER_REGISTRY_URL)
	@docker login -u "$(DOCKER_REGISTRY_USER)" -p "$(DOCKER_REGISTRY_PASS)" $(DOCKER_REGISTRY_URL)
	docker push "$(DOCKER_QUALIFIED_NAME)"
	docker push "$(DOCKER_QUALIFIED_NAME_DEMO)"

.PHONY: build
build: assemble

.PHONY: assemble
assemble:
	$(GRADLEW) $(GRADLE_ARGS) assemble

.PHONY: sonar
sonar:
	$(GRADLEW) $(GRADLE_ARGS) sonarqube $(SONAR_BRANCH_NAME) "-Dsonar.host.url=$(SONAR_URL)" "-Dsonar.login=$(SONAR_TOKEN)"

.PHONY: delete
delete:
	-kubectl delete namespace inner
	-kubectl delete namespace middle
	-kubectl delete namespace outer
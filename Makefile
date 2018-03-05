BUILDS = demo-server
CONTAINERS = spire-agent spire-server demo-server
DEPLOYMENTS = spire-agent spire-server vault demo-server

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

.PHONY: spire-register
spire-register:
	# Wait for the SPIRE server to become "Running"
	while ! kubectl get pod  | grep 'spire-server.*Running' > /dev/null; do sleep 1; done

	# Front demo server
	kubectl exec $$(kubectl get pod | grep -Eo 'spire-server\S*') -- \
		/opt/spire/spire-server register \
		-parentID spiffe://salm.qaware.de/k8s/node/minikube \
		-spiffeID spiffe://salm.qaware.de/demo-server \
		-selector k8s:ns:default

	# Back demo server
	kubectl exec $$(kubectl get pod | grep -Eo 'spire-server\S*') -- \
		/opt/spire/spire-server register \
		-parentID spiffe://salm.qaware.de/k8s/node/minikube \
		-spiffeID spiffe://salm.qaware.de/demo-server \
		-selector k8s:ns:back

.PHONY: minikube-container-build
minikube-container-build:
	@eval $$(minikube docker-env); $(MAKE) container-build

.PHONY: minikube-deploy
minikube-deploy:
	# Enable ingress addon as it is not enabled by default
	minikube addons enable ingress
	@eval $$(minikube docker-env); $(MAKE) deploy

.PHONY: minikube-deploy-and-register
minikube-deploy-and-register: minikube-deploy spire-register

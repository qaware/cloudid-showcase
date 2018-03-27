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
		/opt/spire/spire-server entry create \
		-parentID spiffe://salm.qaware.de/k8s/node/minikube \
		-spiffeID spiffe://salm.qaware.de/demo-server \
		-selector k8s:ns:default

	# Back demo server
	kubectl exec $$(kubectl get pod | grep -Eo 'spire-server\S*') -- \
		/opt/spire/spire-server entry create \
		-parentID spiffe://salm.qaware.de/k8s/node/minikube \
		-spiffeID spiffe://salm.qaware.de/demo-server \
		-selector k8s:ns:back

.PHONY: minikube-container-build
minikube-container-build:
	@eval $$(minikube docker-env); $(MAKE) container-build

.PHONY: minikube-deploy
minikube-deploy:
	@eval $$(minikube docker-env); $(MAKE) deploy

.PHONY: minikube-deploy-and-register
minikube-deploy-and-register: minikube-deploy spire-register

.PHONY: minikube-show-service
minikube-show-service:
	minikube service --url --https demo-server-service-front

.PHONY: minikube-test-service
minikube-test-service:
	curl -k $$(minikube service --url --https demo-server-service-front)/123

.PHONY: delete-demo-server-pods
delete-demo-server-pods:
	kubectl -n back delete pod $$(kubectl -n back get pod -o name | grep -o 'demo-server.*$$')
	kubectl delete pod $$(kubectl get pod -o name | grep -o 'demo-server.*$$')

.PHONY: minikube-start
minikube-start:
	minikube start --memory 6144 --cpus 2

.PHONY: minikube-stop
minikube-stop:
	minikube stop

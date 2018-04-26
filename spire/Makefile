PROJECT_NAME = spire

K8S_NAMESPACE = spire


.PHONY: deploy
deploy: container-build deploy-only register


.PHONY: container-build
container-build:
	docker build -t $(PROJECT_NAME) .


.PHONY: deploy-only
deploy-only:
	# Create namespace
	kubectl create namespace $(K8S_NAMESPACE)

	# Deploy server
	$(foreach f, $(wildcard k8s/server/*.yaml), \
		sed "s/{{UPSTREAM_CA_KEY}}/$$(base64 ../upstream-ca/ca-key.pem | tr -d '\r\n')/; \
			s/{{UPSTREAM_CA}}/$$(base64 ../upstream-ca/ca.pem | tr -d '\r\n')/" \
		$f | kubectl apply -n $(K8S_NAMESPACE) -f -;)

	# Deploy agent
	$(foreach f, $(wildcard k8s/agent/*.yaml), \
		sed "s/{{UPSTREAM_CA_KEY}}/$$(base64 ../upstream-ca/ca-key.pem | tr -d '\r\n')/; \
			s/{{UPSTREAM_CA}}/$$(base64 ../upstream-ca/ca.pem | tr -d '\r\n')/" \
		$f | kubectl apply -n $(K8S_NAMESPACE) -f -;)

.PHONY: register
register:
	# Wait for the SPIRE server to become "Running"
	while ! kubectl -n $(K8S_NAMESPACE) get pod  | grep 'spire-server.*Running' > /dev/null; do sleep 2; done

	# Register SPIFFE Ids
	awk '/^[^#]/ {print "-parentID", $$1, "-spiffeID", $$2, "-selector", $$3}' < spiffe-ids.txt | \
		xargs -n6 kubectl -n $(K8S_NAMESPACE) exec $$(kubectl -n $(K8S_NAMESPACE) get pod | grep -Eo 'spire-server\S*') -- \
			/opt/spire/spire-server entry create


.PHONY: delete
delete:
	-kubectl delete namespace $(K8S_NAMESPACE)

.PHONY: create-spiffe-id
create-spiffe-id:
	kubectl -n spire exec $$(kubectl -n spire get pod | grep -Eo 'spire-server\S*') -- \
		/opt/spire/spire-server entry create \
		-parentID spiffe://cloudid.qaware.de/k8s/node/minikube \
		-spiffeID spiffe://cloudid.qaware.de/rogue \
		-selector k8s:ns:spire

.PHONY: dump-certs
dump-certs:
	kubectl -n spire exec $$(kubectl -n spire get pod -o name | grep -o 'spire-agent.*$$') -- /opt/spire/spire-agent api fetch -socketPath /spire/socket/agent.sock -write /root && kubectl -n spire cp $$(kubectl -n spire get pod -o name | grep -o 'spire-agent.*$$'):/root .

.PHONY: try-request
try-request:
	curl -v --cacert ../upstream-ca/ca.pem --key ./svid.0.key --cert ./svid.0.pem $$(minikube service -n inner cloudid-server --https --url)

.PHONY: show-error
show-error:
	kubectl logs -n inner $$(kubectl -n inner get pod -o name | grep -o 'cloudid-server.*$$') | fgrep ERROR

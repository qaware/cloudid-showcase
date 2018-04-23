# Attack showcase

This showcase shows how lateral movement by an attacker is prevented.
It assumes that a workload has been compromised and an attacker has access to a valid SVID. 
The attacker is unable to access any services because there is no ACL entry for his SVID.


## Preconditions
- The cluster must be up and running (run `make deploy` on the root project)

## Attack
- `make create-spiffe-id` registers a SPIFFE Id at SPIRE for the attack
- `make dump-certs` writes the SVID for the SPIFFE Id to disk
- `make try-request` attempts to access the server in the "inner" namespace. Curl should output the line
    `error:14094416:SSL routines:SSL3_READ_BYTES:sslv3 alert certificate unknown`, showing that the server did not 
    accept the certificate
- `make show-error` greps the logs from the server, showing that while the certificate was accepted cryptographically,
    access was denied because there was no ACL entry for the SPIFFE Id.
`

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
# SPIRE on Kubernetes

This repository contains the docker builds and kubernetes configurations to run the SPIFFE's Runtime, SPIRE, on Kubernetes.

It contains the following parts:

## Spire-Server
The Spire-Server runs as regular [Deployment](spire-server/k8s/deployment.yaml).
It is exposed as Kubernetes [Service](spire-server/k8s/service.yaml).
This allows a generic host name within the cluster. 

### Configuring the Spire-Server
The Spire-Server is configured using a Kuberetes [ConfigMap](spire-server/k8s/configmap.yaml).
Additionally it requires a [Secret](spire-server/k8s/secrets.yaml) to store the certificates for the Upstream CA.

### Register the workload
```bash
kubectl exec $(kubectl get pod -o name | grep -o 'spire-server.*$') -- /opt/spire/spire-server register -parentID spiffe://salm.qaware.de/k8s/node/minikube -spiffeID spiffe://salm.qaware.de/host/workload -selector k8s:ns:default
```

### Fetches the certificates within the workload 
```bash
kubectl exec $(kubectl get pod -o name | grep -o 'spire-server.*$') -- /opt/spire/spire-agent api fetch -socketPath /spire/socket/agent.sock -write /root
```


## Spire-Agent
The Spire-Agent must run on every node which should schedule SPIRE secured pods.
To start the agent it automatically retrieves a join token from the Spire-Server.

### Configuring the Spire-Agent
As the Spire-Server, the spire agent is also be configured using the Kubernetes [ConfigMap](spire-agent/k8s/configmap.yaml).
If you change the Spire-Server hostname and/or the trust domain you have to change it also within the arguments of the Spire-Agent [DaemonSet](spire-agent/k8s/daemonSet.yaml).

## Changing the TrustDomain
When changing the trust domain it is necessary to adjust them at the following places:

 1. Spire-Server ConfigMap
 2. Spire-Agent ConfigMap
 3. Spire-Agent DaemonSet Startup Command

#!/usr/bin/env bash
eval $(minikube docker-env)

# build docker images
docker build -t spire-server spire-server
docker build -t spire-agent spire-agent
docker build -t spire-proxy spire-proxy

# deploy server to k8s
kubectl apply -f spire-server/k8s/configmap.yaml
kubectl apply -f spire-server/k8s/secrets.yaml
kubectl apply -f spire-server/k8s/service.yaml
kubectl apply -f spire-server/k8s/deploymenet.yaml

# deploy agent to k8s
kubectl apply -f spire-agent/k8s/configmap.yaml
kubectl apply -f spire-agent/k8s/daemonSet.yaml

# deploy proxy to k8s
kubectl apply -f spire-agent/k8s/service.yaml
kubectl apply -f spire-agent/k8s/deployment.yaml

# Register the workload: (replace the pod id with the real one of the spire server)
# kubectl exec spire-server-PODID /opt/spire/spire-server register -parentID spiffe://example.org/k8s/node/minikube -spiffeID spiffe://example.org/host/workload -selector k8s:ns:default

# Fetches the certificates within the workload (replace the pod id with the real one of the spire proxy)
# kubectl exec spire-proxy-PODID /opt/spire/spire-agent api fetch -socketPath /spire/socket/agent.sock -write /root

# now the current certs are within the /root dir.

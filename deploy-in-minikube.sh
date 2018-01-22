#!/usr/bin/env bash
eval $(minikube docker-env)

# OR: find . -type f -name '*.yaml' | xargs -n1 kubectl apply -f

# build docker images
docker build -t spire-server spire-server
docker build -t spire-agent spire-agent
docker build -t spire-proxy spire-proxy

# deploy server to k8s
kubectl apply -f spire-server/k8s/configmap.yaml
kubectl apply -f spire-server/k8s/secrets.yaml
kubectl apply -f spire-server/k8s/service.yaml
kubectl apply -f spire-server/k8s/deployment.yaml

# deploy agent to k8s
kubectl apply -f spire-agent/k8s/configmap.yaml
kubectl apply -f spire-agent/k8s/daemonSet.yaml

# kubectl apply -f spire-agent/k8s/service.yaml
# kubectl apply -f spire-agent/k8s/deployment.yaml


# now the current certs are within the /root dir.

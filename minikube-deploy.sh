#!/usr/bin/env bash

indent() { sed 's/^/  /'; }
run-cmd() { echo "\$ $1"; $1 | indent; echo; }

if ! minikube status > /dev/null
then
    echo "Error: Minikube must be running"
    minikube status | indent
    exit $?
fi


# Setup docker environment
eval $(minikube docker-env)

# Build docker images
for NAME in $(find . -type f -name Dockerfile | awk 'BEGIN{FS="/"}{print $2}')
do
    run-cmd "docker build -t $NAME $NAME"
done


# Deploy to K8s
for CONFIG_FILE in $(find . -path '**/k8s/*.yaml')
do
    run-cmd "kubectl apply -f $CONFIG_FILE"
done


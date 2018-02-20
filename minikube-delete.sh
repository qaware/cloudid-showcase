#!/usr/bin/env bash

indent() { sed 's/^/  /'; }
run-cmd() { echo "\$ $1"; $1 | indent; echo; }


for CONFIG_FILE in $(find . -path '**/k8s/*.yaml')
do
    run-cmd "kubectl delete -f $CONFIG_FILE"
done
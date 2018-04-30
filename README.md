# Cloud Native Identity Management Showcase 

Provides a showcase for cloud native identity management using SPIFFE, SPIRE and Vault on Kubernetes.

## Components
- [SPIRE](spire)
- [Fixed Upstream CA for SPIRE](upstream-ca)
- [Vault](vault)
- [Java showcase](cloudid)
- [Lateral attack showcase](attack)


## Build & Development

### Prerequisites
- LINUX or macOS is recommended
    - Windows does not support Domain Sockets. Build & development is possible on Windows with limited local testability.
    - [Homebrew](https://brew.sh/) is recommended on macOS for installing the prerequisites
- [JDK 8 or 9](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
- BASH shell ([Babun](https://babun.github.io/) is recommended on Windows. pre-installed on macOS) for the Makefiles
- GNU make (Pre-installed with Babun on Windows, pre-installed on macOS)
- [Docker CLI](https://docs.docker.com/docker-cloud/installing-cli/)
- [kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl/)
- [minikube](https://kubernetes.io/docs/tasks/tools/install-minikube/)
- [VirtualBox](https://www.virtualbox.org/) (or another Hypervisor supported by minikube)
- ZSH with [oh-my-zsh](https://github.com/robbyrussell/oh-my-zsh) and the kubectl and Docker plugins is recommended as development shell
- [IntelliJ IDEA](https://www.jetbrains.com/idea/) is recommended as IDE    
    - Recommended plugins:
        - Kubernetes and Openshift Resource Support Plugin
        - Makefile Support
        - .ignore
        - Lombok Plugin
        - Spock Framework Enhancements
        - Protobuf Support
        - Bash Support
    - Don't forget to activate annotation processing to make Lombok work in IntelliJ IDEA

### Minikube

Deployment:
```bash
make minikube-deploy
```

Accessing the demo with a browser:
- Use `make minikube-test-service-url` to get the URL of the HTTP ingress
- Access the URL in the browser


### Kubernetes

Fetch certificates from the SPIRE agent:
```bash
kubectl -n spire exec $(kubectl -n spire get pod -o name | grep -o 'spire-agent.*$') -- /opt/spire/spire-agent api fetch -socketPath /spire/socket/agent.sock -write /root && kubectl -n spire cp $(kubectl -n spire get pod -o name | grep -o 'spire-agent.*$'):/root .
```

Register workloads:
```bash
kubectl exec $(kubectl get pod -o name | grep -o 'spire-server.*$') -- /opt/spire/spire-server register -parentID spiffe://cloudid.qaware.de/k8s/node/minikube -spiffeID spiffe://cloudid.qaware.de/host/workload -selector k8s:ns:default
```

## Known Issues
- Ingress does not work on Minikube with TLS-protected backends on Minikube up
- Minikube API server hickups on Minikube 0.26.1 (0.25 works fine)


## Copyright

The showcase is © 2018 QAware, published under the Apache License Version 2.0, with the following exceptions:
- [SPIFFE](static/img/spiffe_logo.png) and [SPIRE](static/img/spire_logo.png) logos © 2017 The SPIFFE Project & Scytale, Inc. See https://github.com/spiffe.
- [Cat image](static/img/cat-2483826_1280.jpg), [source: pixabay.com](https://pixabay.com/de/katze-lustig-doof-gesicht-tier-2483826/), subject to the [Creative Commons CC0](https://creativecommons.org/publicdomain/zero/1.0/deed.de)
- [Demo site template](templates/demo.html), [source: html5webtemplates.co.uk](https://www.html5webtemplates.co.uk/templates/colour_orange/index.html), subject to the [Creative Commons CC BY 3.0](https://creativecommons.org/licenses/by/3.0/)

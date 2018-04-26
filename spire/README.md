# SPIRE 

Provides:
- A [Dockerfile](Dockerfile) for both the SPIRE agent and server.
- The [SPIRE server K8s deployment](k8s/server)
- The [SPIRE agent K8s deployment](k8s/agent)

The SPIRE agent is deployed as a privileged Daemon Set, ensuring that exactly one instance of the SPIRE agent runs
on each node. The agent creates a Unix Domain Socket to be used for identity attestation by workloads.

When changing the trust domain it is necessary to adjust them at the following places:

1. Spire-Server ConfigMap
2. Spire-Agent ConfigMap
3. Spire-Agent DaemonSet Startup Command

See https://github.com/spiffe/spire for more information on SPIRE.

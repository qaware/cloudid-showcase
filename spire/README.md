# SPIRE 


## Dockerfile
Provides a Dockerfile for SPIRE that works for both the SPIRE agent and server.

## SPIRE server deployment

Provides the Kubernetes deployment for the SPIRE server.

See https://github.com/spiffe/spire for more information.

## SPIRE agent deployment

Provides the Kubernetes deployment for the SPIRE agent.

The SPIRE agent is deployed as a privileged Daemon Set, ensuring that exactly one instance of the SPIRE agent runs
on each node. The agent creates a Unix Domain Socket to be used for identity attestation by workloads.

See https://github.com/spiffe/spire for more information. 
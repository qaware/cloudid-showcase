# Vault deployment

Provides a Vault Kubernetes deployment.

Vault is automatically initialized and unsealed after being deployed and runs in production mode. This deployment
has no persistence meaning that a restart resets everything.

Don't use such a setup in production without thinking about the consequences. This setup, while convenient for a 
showcase, essentially renders the unseal process useless.

Vault can be accessed from anywhere in the cluster using `https://vault-service.default:8200`. In the pod Vault can 
also be accessed without TLS using `http://localhost:8300` for configuration purposes.

A Vault pod consists of two containers: Vault itself with initialization scripts and a sidecar for rotating the 
SPIRE CA by the SPIRE server. This rotated certificate is being used by Vault for the TLS Certificates Auth Method.

Vault is started by the [start-vault.sh](start-vault.sh) script and then setup by the [setup-vault.sh](setup-vault.sh) 
script. The sidecar [start-helper.sh](sidecar/start-helper.sh) script starts simultaneously and waits until Vault and 
the SPIRE Agent are ready. This setup requires some time to start.

Vault uses the provided vault.pem and vault-key.pem for TLS. To verify client certificates the SPIRE upstream CA is
provided using a secret.


See https://github.com/hashicorp/vault for more information about Vault.


#!/bin/bash

/vault/init/setup-vault.sh &

# Start vault in the foreground
vault server -config /vault/config/vault.conf
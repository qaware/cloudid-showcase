FROM vault:0.10.0

RUN apk add --no-cache bash curl

COPY start-vault.sh /vault/init/start-vault.sh
COPY setup-vault.sh /vault/init/setup-vault.sh

RUN chmod +x /vault/init/start-vault.sh /vault/init/setup-vault.sh

ENTRYPOINT ["/vault/init/start-vault.sh"]
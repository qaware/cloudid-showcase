# Cloud Id Library

Provides the custom Java Security API (JSA) implementation with support for rotating SPIRE certificates.

## How does it work?

The Cloud Id library builds on the java.security API and implements it's own Provider, KeyManager, KeyStore,
TrustManager and TrustStore. These implementations are backed by a workload identity continuously updated from SPIRE
and an ACL from Vault. 

A Java application can use rotating SPIRE certificates just by installing the java.security provider.

## Using the library

For example usage see [Cloud Id Proxy](../cloudid-proxy) and [Cloud Id Demo](../cloudid-demo).

### Basic Setup

1. Install provider at startup: `CloudIdProvider.install();`. Alternatively, the provider can be installed JVM-global
    without any code changes using Java security properties.
2. Make your use the custom KeyManager and TrustManager if it does not pick up the Java defaults automatically.

Unfortunately a lot of frameworks and tools use java.security in slightly different ways. 
This means that additional configuration is required depending on the framework. 
Otherwise it'll simply ignore the custom JSA classes of this library. One such example is Apache HttpClient, 
which will always create a custom SSL context, disregarding the default one. 
Some examples on how to do that additional setup can be found below..

### Additional setup for frameworks and tools

The following examples might give you an idea how the additional setup could look like. 

Some implementations require you to provide an existing key store file which they use to get their own
KeyManager/TrustManager instances. However it's sometimes possible to directly pass KeyStore/TrustStore instances. If
passing a `SSLContext (java.net.ssl)` is possible,
[CloudIdContextFactory](src/main/java/de/qaware/cloudid/lib/jsa/CloudIdContextFactory.java) can be used to obtain a
SSLContext.

#### Apache HTTPClient

```java
HttpClients.custom()
    .setSSLContext(Cloud IdContextFactory.get())
    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
    .build();
```

`.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)` prevents the HTTPClient from interpreting the SPIFFE URI SANs
as hostnames.


#### Vault Java Driver by BetterCloud

```java
VaultConfig config = new VaultConfig()
    .address(Config.VAULT_ADDRESS.get())
    .sslConfig(new SslConfig()
            .keyStore(getKeyStore(), "")
            .trustStore(getTrustStore())
            .build())
    .build();
Vault vaultConnection = new Vault(config);
```

With `getKeyStore()` and `getTrustStore()` being:
```java
private static KeyStore getKeyStore() {
        return loadKeyStore(Cloud Id.ALGORITHM);
}

private static KeyStore getTrustStore() {
    return loadKeyStore(Cloud Id.TRUST_STORE_ALGORITHM);
}

private static KeyStore loadKeyStore(String algorithm) {
    try {
        KeyStore keyStore = KeyStore.getInstance(algorithm);
        keyStore.load(null, "".toCharArray());
        return keyStore;
    } catch (GeneralSecurityException | IOException e) {
        throw new IllegalStateException(e);
    }
}
```

### Additional TrustStore

An additional TrustStore can be set with `javax.net.ssl.trustStore` JVM settings which is being
used when accessing a server without a SPIRE certificate like https://kubernetes.io. If not set the Java default
TrustStore will be used that by default contains public root CA certificates.

## Troubleshooting

Setting `-Djavax.net.debug=all` enables default java.security logging. Although it outputs a lot, its errors can be
 misleading. In most cases the configuration is wrong.

It might not be possible to access the demo using HTTPs from your browser. Chrome for instance does not like the 
combination of a self-signed root CA and signed certificates.

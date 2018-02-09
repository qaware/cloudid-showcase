package de.qaware.cloud.id.spire.impl;

import org.junit.Test;

import javax.net.ssl.X509KeyManager;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class KeyManagementTest {

    @Test
    public void test() throws Exception {
        SpiffeKeyManagerFactorySpi keyManagerFactorySpi = new SpiffeKeyManagerFactorySpi();
        keyManagerFactorySpi.engineInit(null);

        X509KeyManager keyManager = (X509KeyManager) keyManagerFactorySpi.engineGetKeyManagers()[0];

        // Wait for the updater thread to do it's work
        Thread.sleep(2_000);

        PrivateKey privateKey = keyManager.getPrivateKey("");
        X509Certificate[] certificateChain = keyManager.getCertificateChain("");
        String[] clientAliases = keyManager.getClientAliases("", new Principal[0]);

        assertThat(privateKey, is(notNullValue()));
        assertThat(certificateChain.length, is(greaterThan(0)));
        assertThat(clientAliases, is(equalTo(new String[]{"spiffe://example.org/host/workload"})));
    }

}
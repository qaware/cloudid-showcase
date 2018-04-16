package de.qaware.cloudid.lib.jsa;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyStoreSpi;
import java.security.cert.Certificate;


/**
 * Base class for read-only virtual key stores.
 */
@Slf4j
abstract class ROVirtualKeyStoreSPI extends KeyStoreSpi {

    @Override
    public final void engineLoad(InputStream stream, char[] password) {
        LOGGER.trace("engineLoad({}, ...)", stream);
    }

    @Override
    public final void engineSetKeyEntry(String alias, Key key, char[] password, Certificate[] chain) {
        LOGGER.warn("engineSetKeyEntry({}, {}, ..., {})", alias, key, chain);
    }

    @Override
    public final void engineSetKeyEntry(String alias, byte[] key, Certificate[] chain) {
        LOGGER.warn("engineSetKeyEntry({}, {}, {})", alias, key, chain);
    }

    @Override
    public final void engineSetCertificateEntry(String alias, Certificate cert) {
        LOGGER.warn("engineSetCertificateEntry({}, {})", alias, cert);
    }

    @Override
    public final void engineDeleteEntry(String alias) {
        LOGGER.warn("engineDeleteEntry({}, {})", alias);
    }

    @Override
    public final void engineStore(OutputStream stream, char[] password) {
        LOGGER.warn("engineStore({}, ...)", stream);
    }

}

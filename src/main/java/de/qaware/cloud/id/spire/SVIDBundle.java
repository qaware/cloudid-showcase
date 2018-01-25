package de.qaware.cloud.id.spire;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.security.KeyPair;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * A data transfer object that contains the parsed informations about a spiffe workload id.
 */
public class SVIDBundle {
    private final String svId;
    private final X509Certificate certificate;
    private final KeyPair keyPair;
    private final List<? extends Certificate> caCertificates;

    public SVIDBundle(String svId, X509Certificate certificate, KeyPair keyPair, List<? extends Certificate> caCertificates) {
        this.certificate = certificate;
        this.keyPair = keyPair;
        this.caCertificates = caCertificates;
        this.svId = svId;
    }

    public X509Certificate getCertificate() {
        return certificate;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public List<? extends Certificate> getCaCertificates() {
        return caCertificates;
    }

    public String getSvId() {
        return svId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SVIDBundle that = (SVIDBundle) o;

        return new EqualsBuilder()
                .append(getCertificate(), that.getCertificate())
                .append(getKeyPair(), that.getKeyPair())
                .append(getCaCertificates(), that.getCaCertificates())
                .append(getSvId(), that.getSvId())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getCertificate())
                .append(getKeyPair())
                .append(getCaCertificates())
                .append(getSvId())
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("svId", svId)
                .toString();
    }
}

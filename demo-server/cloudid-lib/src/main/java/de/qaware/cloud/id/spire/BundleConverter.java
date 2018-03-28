package de.qaware.cloud.id.spire;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.sec.ECPrivateKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import spire.api.workload.WorkloadOuterClass.WorkloadEntry;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Converts a workload entry to a SVID bundle.
 */
@Slf4j
class BundleConverter implements Function<WorkloadEntry, Bundle> {

    private final CertificateFactory certFactory;

    /**
     * Constructor.
     */
    public BundleConverter() {
        try {
            certFactory = CertificateFactory.getInstance("x509");
        } catch (CertificateException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Bundle apply(WorkloadEntry workloadEntry) {
        try {
            return convert(workloadEntry);
        } catch (GeneralSecurityException | IOException e) {
            LOGGER.warn("Can not extract certificates for SPIFFE Id: " + workloadEntry.getSpiffeId(), e);
            return null;
        }
    }

    /**
     * Converts a workload entry to a SVID bundle.
     *
     * @param workloadEntry workload entry
     * @return SVID bundle
     * @throws IOException          something claiming to be a certificate is not
     * @throws CertificateException one of the certificates is invalid
     */
    public Bundle convert(WorkloadEntry workloadEntry) throws IOException, CertificateException {
        List<X509Certificate> certPath = getCertPath2(workloadEntry);

        return new Bundle(
                workloadEntry.getSpiffeId(),
                certPath.get(0),
                getKeyPair(workloadEntry),
                certPath);
    }


    private List<X509Certificate> getCertPath2(WorkloadEntry workloadEntry) throws IOException, CertificateException {
        try (InputStream bundleInputStream = workloadEntry.getSvidBundle().newInput();
             InputStream svidInputStream = workloadEntry.getSvid().newInput()) {
            return Stream.concat(
                    Stream.of(certFactory.generateCertificate(svidInputStream)),
                    certFactory.generateCertificates(bundleInputStream).stream())
                    .map(c -> (X509Certificate) c)
                    .collect(toList());
        }
    }

    private static KeyPair getKeyPair(WorkloadEntry workloadEntry) throws IOException {
        ASN1Sequence seq = ASN1Sequence.getInstance(workloadEntry.getSvidPrivateKey().toByteArray());

        ECPrivateKey pKey = ECPrivateKey.getInstance(seq);
        AlgorithmIdentifier algId = new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, pKey.getParameters());
        PrivateKeyInfo privInfo = new PrivateKeyInfo(algId, pKey);
        SubjectPublicKeyInfo pubInfo = new SubjectPublicKeyInfo(algId, pKey.getPublicKey().getBytes());

        return new JcaPEMKeyConverter().getKeyPair(new PEMKeyPair(pubInfo, privInfo));
    }

}

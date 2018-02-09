package de.qaware.cloud.id.spire.impl;

import de.qaware.cloud.id.spire.ChannelFactory;
import de.qaware.cloud.id.spire.SVIDBundle;
import io.grpc.ManagedChannel;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.sec.ECPrivateKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spire.api.workload.WorkloadGrpc;
import spire.api.workload.WorkloadOuterClass;
import spire.api.workload.WorkloadOuterClass.Bundles;
import spire.api.workload.WorkloadOuterClass.WorkloadEntry;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;

/**
 * Fetches  SPIFFE workload bundles from the SPIRE agent.
 */
public class RawBundlesSupplier implements Supplier<List<SVIDBundle>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RawBundlesSupplier.class);
    private final ChannelFactory<?> channelFactory;

    static {
        // TODO: Move this to a better placeKKK
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Initializes the bundle fetcher. Uses the given {@link ChannelFactory} to connect to the spire agent.
     *
     * @param channelFactory The channel factory to create the connection.
     */
    public RawBundlesSupplier(ChannelFactory<?> channelFactory) {
        this.channelFactory = channelFactory;
    }

    /**
     * Fetches all bundles that are valid for the current workload.
     *
     * @return A list of {@link SVIDBundle}s which contains the certificates and the private key. The list may be empty.
     */
    @Override
    public List<SVIDBundle> get() {
        List<SVIDBundle> bundleList = new ArrayList<>();

        ManagedChannel channel = channelFactory.createChannel().build();
        WorkloadGrpc.WorkloadBlockingStub workload = WorkloadGrpc.newBlockingStub(channel);
        Bundles bundles = workload.fetchAllBundles(WorkloadOuterClass.Empty.newBuilder().build());

        for (WorkloadEntry workloadEntry : bundles.getBundlesList()) {
            try {
                bundleList.add(getBundle(workloadEntry));
            }
            catch (GeneralSecurityException | IOException e) {
                LOGGER.warn("Can not extract certificates for SPIFFE ID: " + workloadEntry.getSpiffeId(), e);
            }
        }

        return bundleList;
    }

    /**
     * Extracts the certificate, keypair and parent ca's from the {@link WorkloadEntry}.
     *
     * @param workloadEntry The workload entry to extract the information from.
     * @return A {@link SVIDBundle} object that contains the extracted information.
     * @throws GeneralSecurityException In case of the certificate could not be extracted or converted.
     * @throws IOException              In case of the private key were unable to read.
     */
    SVIDBundle getBundle(WorkloadEntry workloadEntry) throws GeneralSecurityException, IOException {
        CertificateFactory certFactory = CertificateFactory.getInstance("x509");

        X509Certificate svidCertificate = (X509Certificate) certFactory.generateCertificate(workloadEntry.getSvid().newInput());

        // Assume the cert path to be all X.509 certificates as anything else doesn't make sense here
        List<X509Certificate> certPath = certFactory
                .generateCertificates(workloadEntry.getSvidBundle().newInput())
                .stream()
                .map(c -> (X509Certificate) c)
                .collect(toList());

        // Smoke test the certificate
        svidCertificate.verify(certPath.get(0).getPublicKey());


        KeyPair keyPair = getKeyPair(workloadEntry);
        if (!Objects.equals(svidCertificate.getPublicKey(), keyPair.getPublic())) {
            throw new IllegalStateException("Certificates public key and the delivered private key did not match.");
        }

        return new SVIDBundle(workloadEntry.getSpiffeId(), svidCertificate, keyPair, certPath);
    }

    /**
     * Extracts the key pair from a {@link WorkloadEntry}.
     * <p>
     * The returned object contains the private and public keys. This method assumes that the workload contains an
     * elliptic curve private key.
     *
     * @param workloadEntry The workload entry to extract the key pair from.
     * @return A key pair that contains the private and public key for the workload entry.
     * @throws IOException In case of the key pair could not extracted.
     */
    private static KeyPair getKeyPair(WorkloadEntry workloadEntry) throws IOException {
        ASN1Sequence seq = ASN1Sequence.getInstance(workloadEntry.getSvidPrivateKey().toByteArray());

        ECPrivateKey pKey = ECPrivateKey.getInstance(seq);
        AlgorithmIdentifier algId = new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, pKey.getParameters());
        PrivateKeyInfo privInfo = new PrivateKeyInfo(algId, pKey);
        SubjectPublicKeyInfo pubInfo = new SubjectPublicKeyInfo(algId, pKey.getPublicKey().getBytes());

        return new JcaPEMKeyConverter().getKeyPair(new PEMKeyPair(pubInfo, privInfo));
    }
}

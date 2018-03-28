package org.aerogear.mobile.core.configuration.https;

/**
 * Class to represent an entry in certificatePins of the https configuration for mobile core.
 */
public class CertificatePinningEntry {
    private final static String SHA256_PREFIX = "sha256/";

    private final String hostName;
    private final String certificateHash;

    public CertificatePinningEntry(final String hostName, final String certificateHash) {
        this.hostName = hostName;
        this.certificateHash = certificateHash;
    }

    /**
     * Retrieve the host name of the pin.
     *
     * @return Host of the pin.
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * Retrieve the hash of the pin.
     *
     * @return Hash of the pin.
     */
    public String getCertificateHash() {
        return SHA256_PREFIX + certificateHash;
    }
}

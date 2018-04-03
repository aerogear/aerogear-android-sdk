package org.aerogear.mobile.core.configuration.https;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;


/**
 * This represents a parsed Https configuration from JSON configuration
 */
public class HttpsConfiguration {
    public static final String CERT_PINNING_KEY = "certificatePins";

    private final List<CertificatePinningEntry> certPinningConfig;

    private HttpsConfiguration(final List<CertificatePinningEntry> certPinningConfig) {
        this.certPinningConfig = nonNull(certPinningConfig,"certPinningConfig");
    }

    public static class Builder {
        private List<CertificatePinningEntry> certPinningConfig = new ArrayList<>();

        public Builder setCertPinningConfig(final List<CertificatePinningEntry> pinningConfig) {
            this.certPinningConfig = new ArrayList<>(pinningConfig);
            return this;
        }

        public Builder addCertPinningEntry(final CertificatePinningEntry pinningEntry) {
            this.certPinningConfig.add(nonNull(pinningEntry, "pinningEntry"));
            return this;
        }

        public HttpsConfiguration build() {
            return new HttpsConfiguration(this.certPinningConfig);
        }
    }

    /**
     * Retrieve certificate pinning entries
     *
     * @return List of {@link CertificatePinningEntry}
     */
    public List<CertificatePinningEntry> getCertPinningConfig() {
        return Collections.unmodifiableList(certPinningConfig);
    }

    /**
     * Create a new {@link HttpsConfiguration.Builder}
     *
     * @return {@link HttpsConfiguration.Builder}
     */
    public static Builder newBuilder() {
        return new Builder();
    }
}

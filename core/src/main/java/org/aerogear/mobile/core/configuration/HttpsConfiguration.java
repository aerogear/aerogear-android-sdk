package org.aerogear.mobile.core.configuration;

/**
 * This represents a parsed Https configuration from JSON configuration
 */

public class HttpsConfiguration {
    private final String hostName;
    private final String certificateHash;

    private HttpsConfiguration(final String hostName, final String certificateHash) {
        this.hostName = hostName;
        this.certificateHash = certificateHash;
    }

    public static class Builder {
        private String hostName;
        private String certificateHash;

        public Builder setHostName(final String hostName) {
            this.hostName = hostName;
            return this;
        }

        public Builder setCertificateHash(final String certificateHash) {
            this.certificateHash = certificateHash;
            return this;
        }

        public HttpsConfiguration build() {
            return new HttpsConfiguration(this.hostName, this.certificateHash);
        }
    }

    public String getHostName() {
        return hostName;
    }

    public String getCertificateHash() {
        return certificateHash;
    }

    public static Builder newHashConfiguration() {
        return new Builder();
    }
}

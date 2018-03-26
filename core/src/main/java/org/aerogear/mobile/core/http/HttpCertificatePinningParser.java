package org.aerogear.mobile.core.http;


import okhttp3.CertificatePinner;

/**
 * Class for parsing raw https configurations and returning a usable pinning configuration.
 */
public interface HttpCertificatePinningParser {

    /**
     * Creates a new {@link CertificatePinner} and pins hosts from mobile-service.json file
     *
     * @return a new {@link CertificatePinner} object
     */
    CertificatePinner parse();
}

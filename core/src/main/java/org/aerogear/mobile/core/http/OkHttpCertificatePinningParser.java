package org.aerogear.mobile.core.http;

import java.util.List;

import org.aerogear.mobile.core.configuration.https.CertificatePinningEntry;

import okhttp3.CertificatePinner;

public class OkHttpCertificatePinningParser implements HttpCertificatePinningParser {

    private final List<CertificatePinningEntry> pinningConfig;

    public OkHttpCertificatePinningParser(final List<CertificatePinningEntry> pinningConfig) {
        this.pinningConfig = pinningConfig;
    }

    @Override
    public CertificatePinner parse() {
        CertificatePinner.Builder certPinnerBuilder = new CertificatePinner.Builder();
        for (CertificatePinningEntry pinningEntry : pinningConfig) {
            String fullPinningHash = "sha256/" + pinningEntry.getCertificateHash();
            certPinnerBuilder.add(pinningEntry.getHostName(), fullPinningHash);
        }
        return certPinnerBuilder.build();
    }
}

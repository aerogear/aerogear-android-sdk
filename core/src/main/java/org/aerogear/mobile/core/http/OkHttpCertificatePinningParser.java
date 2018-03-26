package org.aerogear.mobile.core.http;

import java.util.Map;

import okhttp3.CertificatePinner;

public class OkHttpCertificatePinningParser implements HttpCertificatePinningParser {

    private final Map<String, String> httpsConfig;

    public OkHttpCertificatePinningParser(final Map<String, String> httpsConfig) {
        this.httpsConfig = httpsConfig;
    }

    @Override
    public CertificatePinner parse() {
        CertificatePinner.Builder certPinnerBuilder = new CertificatePinner.Builder();
        for (Map.Entry<String, String> https : httpsConfig.entrySet()) {
            certPinnerBuilder.add(https.getKey(), "sha256/" + https.getValue());
        }
        return certPinnerBuilder.build();
    }
}

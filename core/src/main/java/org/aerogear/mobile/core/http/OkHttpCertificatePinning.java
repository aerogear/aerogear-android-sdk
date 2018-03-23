package org.aerogear.mobile.core.http;

import java.util.Map;

import okhttp3.CertificatePinner;

public class OkHttpCertificatePinning implements HttpCertificatePinning {

    private final Map<String, String> httpsConfig;

    public OkHttpCertificatePinning(final Map<String, String> httpsConfig){
        this.httpsConfig = httpsConfig;
    }

    @Override
    public CertificatePinner pinCertificates() {
        CertificatePinner.Builder certPinnerBuilder = new CertificatePinner.Builder();
        for(Map.Entry<String, String> https : httpsConfig.entrySet()){
            certPinnerBuilder.add(https.getKey(), "sha256/" + https.getValue());
        }
        return certPinnerBuilder.build();
    }
}

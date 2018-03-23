package org.aerogear.mobile.core.http;


import okhttp3.CertificatePinner;

public interface HttpCertificatePinning {

    /**
     * Creates a new CertificatePinner and pins hosts from mobile-service.json file
     * 
     * @return a new CertificatePinner object
     */
    CertificatePinner pinCertificates();
}

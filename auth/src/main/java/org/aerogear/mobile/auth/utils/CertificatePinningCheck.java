package org.aerogear.mobile.auth.utils;

import org.aerogear.mobile.core.http.HttpRequest;
import org.aerogear.mobile.core.http.HttpResponse;
import org.aerogear.mobile.core.http.HttpServiceModule;


public class CertificatePinningCheck {
    private final HttpServiceModule httpModule;
    private boolean isComplete = false;
    private Exception error;

    public CertificatePinningCheck(final HttpServiceModule httpModule){
        this.httpModule = httpModule;
    }

    public void check(final String url){
        HttpRequest request = httpModule.newRequest();
        request.get(url);
        HttpResponse response = request.execute();
        response.onError(() -> this.error = response.getError());
        response.onComplete(() -> this.isComplete = true);
    }

    public boolean isComplete() {
        return this.isComplete;
    }

    public Exception getError() {
        return this.error;
    }
}

package org.aerogear.mobile.core.http.pinning;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import android.support.annotation.NonNull;

import org.aerogear.mobile.core.http.HttpRequest;
import org.aerogear.mobile.core.http.HttpResponse;
import org.aerogear.mobile.core.http.HttpServiceModule;
import org.aerogear.mobile.core.reactive.Responder;


/**
 * Class for performing certificate pinning checks.
 *
 * This consists of a HTTP GET request using a provided {@link HttpServiceModule}. If the request is
 * successful then a success listener will be invoked. If unsuccessful then a failure listener will
 * be invoked.
 *
 * If a listener is attached after the request is already completed then the listener will be
 * invoked immediately with the result of the request being provided.
 */
public class CertificatePinningCheck {
    private static final CertificatePinningCheckListener DEFAULT_LISTENER =
                    new CertificatePinningCheckListener() {
                        public void onSuccess() {}

                        public void onFailure() {}
                    };

    private final HttpServiceModule httpModule;
    private CertificatePinningCheckListener listener;
    private Exception error;
    private boolean isSuccess = false;
    private boolean isComplete = false;

    public CertificatePinningCheck(final HttpServiceModule httpModule) {
        this.httpModule = nonNull(httpModule, "httpModule");
        this.listener = DEFAULT_LISTENER;
    }

    /**
     * Retrieve the error, if any, from the check.
     *
     * @return The error from making the checks HTTP call, or null.
     */
    public Exception getError() {
        return this.error;
    }

    /**
     * Attach a new {@link CertificatePinningCheckListener}. This will override the current listener
     * if there is one.
     *
     * @param listener The listener to be invoked on completion of the check.
     */
    public void attachListener(@NonNull final CertificatePinningCheckListener listener) {
        this.listener = nonNull(listener, "listener");

        // Invoke the attached listener immediately if we've already completed the the check.
        if (this.isComplete) {
            if (this.isSuccess) {
                this.listener.onSuccess();
                return;
            }
            this.listener.onFailure();
        }
    }

    /**
     * Remove the currently attached listener, if there is one attached.
     */
    public void detachListener() {
        this.listener = DEFAULT_LISTENER;
    }

    /**
     * Perform a check and invoke a listener on response.
     *
     * @param url to which a request will be made
     */
    public void execute(@NonNull final String url) {
        HttpRequest request = this.httpModule.newRequest();
        request.get(url).respondWith(new Responder<HttpResponse>() {
            @Override
            public void onResult(HttpResponse httpResponse) {
                CertificatePinningCheck.this.isSuccess = true;
                if (CertificatePinningCheck.this.listener != null) {
                    CertificatePinningCheck.this.listener.onSuccess();
                }
                CertificatePinningCheck.this.isComplete = true;
            }

            @Override
            public void onException(Exception exception) {
                CertificatePinningCheck.this.listener.onFailure();

            }
        });
    }
}

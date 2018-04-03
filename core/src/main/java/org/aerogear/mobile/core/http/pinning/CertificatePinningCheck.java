package org.aerogear.mobile.core.http.pinning;

import android.support.annotation.NonNull;

import org.aerogear.mobile.core.http.HttpRequest;
import org.aerogear.mobile.core.http.HttpResponse;
import org.aerogear.mobile.core.http.HttpServiceModule;
import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;


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
    private final HttpServiceModule httpModule;
    private CertificatePinningCheckListener listener;
    private Exception error;
    private boolean isSuccess = false;
    private boolean isComplete = false;

    public CertificatePinningCheck(final HttpServiceModule httpModule) {
        this.httpModule = nonNull(httpModule, "httpModule");
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
        this.listener = listener;

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
        this.listener = null;
    }

    /**
     * Perform a check and invoke a listener on response.
     */
    public void execute(@NonNull final String url) {
        HttpRequest request = this.httpModule.newRequest();
        request.get(url);
        HttpResponse httpResponse = request.execute();

        // Handle the success and error responses and set isComplete to allow for immediate
        // invocation if a new listener is attached.
        httpResponse.onSuccess(() -> {
            this.isSuccess = true;
            if (this.listener != null) {
                this.listener.onSuccess();
            }
        });
        httpResponse.onError(() -> {
            this.error = httpResponse.getError();
            this.listener.onFailure();
        });
        httpResponse.onComplete(() -> this.isComplete = true);
    }
}

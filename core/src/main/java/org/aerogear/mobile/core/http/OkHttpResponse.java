package org.aerogear.mobile.core.http;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLPeerUnverifiedException;

import org.aerogear.mobile.core.executor.AppExecutors;

import okhttp3.Call;
import okhttp3.Response;

class OkHttpResponse implements HttpResponse {

    private static final long DEFAULT_TIMEOUT = 30;
    private final CountDownLatch requestCompleteLatch = new CountDownLatch(1);
    private Response response;
    private Runnable completionHandler;
    private Runnable errorHandler;
    private Runnable successHandler;
    private Exception error;
    private boolean closed = false;

    public OkHttpResponse(final Call okHttpCall, AppExecutors appExecutors) {
        appExecutors.networkThread().execute(() -> {
            try {
                response = okHttpCall.execute();
                requestCompleteLatch.countDown();
                runSuccessHandler();
            } catch (SSLPeerUnverifiedException e) {
                error = e;
                requestCompleteLatch.countDown();
                runErrorHandler();
            } catch (IOException e) {
                error = e;
                requestCompleteLatch.countDown();
                runErrorHandler();
            } finally {
                runCompletionHandler();
            }
        });
    }

    /**
     * We have multiple checks to make sure that the completion handler is run. This means that this
     * method may be called from multiple threads, using a synchronized and a null check makes sure
     * that completion handler is only called once and then cleared.
     */
    private synchronized void runCompletionHandler() {
        if (completionHandler != null) {
            completionHandler.run();
            completionHandler = null;
        }
    }

    private synchronized void runErrorHandler() {
        if (errorHandler != null) {
            errorHandler.run();
            errorHandler = null;
        }
    }

    private synchronized void runSuccessHandler() {
        if (successHandler != null) {
            successHandler.run();
            successHandler = null;
        }
    }

    @Override
    public HttpResponse onComplete(Runnable completionHandler) {
        this.completionHandler = completionHandler;
        if (response != null) {
            runCompletionHandler();
        }
        return this;
    }

    @Override
    public HttpResponse onError(Runnable errorHandler) {
        this.errorHandler = errorHandler;
        // An exception occurred during the request
        if (error != null) {
            runErrorHandler();
        }
        return this;
    }

    @Override
    public HttpResponse onSuccess(Runnable successHandler) {
        this.successHandler = successHandler;
        // If there is _any_ response the success handler should run
        if (response != null) {
            runSuccessHandler();
        }
        return this;
    }

    @Override
    public int getStatus() {
        return response.code();
    }

    @Override
    public void waitForCompletionAndClose() {
        try {
            requestCompleteLatch.await(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            // If a success Handler was set before this wait then we need to make
            // sure that gets called before we free these resources.
            if (error == null) {
                runSuccessHandler();
            } else {
                runErrorHandler();
            }
        } catch (InterruptedException interruptedException) {
            /*
             * If we have an error the work of the thread is already done and the thread was exiting
             * anyway. If we don't have en error then the thread may have exited improperly and the
             * calling code should be able to inspect it.
             */
            if (error != null) {
                error = interruptedException;
            }
        } finally {
            if (response != null && !closed) {
                closed = true;
                response.close();
            }

            // Run the completion handler last so that even if an exception occurs
            // the response will be closed
            runCompletionHandler();
        }
    }

    @Override
    public String stringBody() {
        if (response != null) {
            try {
                return response.body().string();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                /**
                 * OKHttp body.string closes the request so we need to track this because you can't
                 * close a request twice.
                 */
                closed = true;
            }
        } else {
            return "";
        }
    }

    @Override
    public Exception getError() {
        return this.error;
    }
}

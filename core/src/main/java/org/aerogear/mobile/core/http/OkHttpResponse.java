package org.aerogear.mobile.core.http;

import org.aerogear.mobile.core.executor.AppExecutors;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Response;

class OkHttpResponse implements HttpResponse {

    private static final long DEFAULT_TIMEOUT = 30;
    private Response response;
    private Runnable completionHandler;
    private Exception requestError;
    private CountDownLatch requestCompleteLatch = new CountDownLatch(1);
    private boolean closed = false;

    public OkHttpResponse(final Call okHttpCall, AppExecutors appExecutors) {
        appExecutors.networkThread().execute(()-> {
            try {
                response = okHttpCall.execute();
                requestCompleteLatch.countDown();
                if (completionHandler != null) {
                    runCompletionHandler();
                }
            } catch (IOException e) {
                requestError = e;
                requestCompleteLatch.countDown();
            }
        });
    }

    /**
     * We have multiple checks to make sure that the completion handler is run.
     * This means that this method may be called from multiple threads, using
     * a synchronized and a null check makes sure that completion handler
     * is only called once and then cleared.
     */
    private synchronized void runCompletionHandler() {
        if (completionHandler != null) {
            completionHandler.run();
            completionHandler = null;
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
    public int getStatus() {
        return response.code();
    }

    @Override
    public void waitForCompletionAndClose() {
        try {
            requestCompleteLatch.await(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            //If a completion Handler was set before this wait then we need to make sure that
            // gets called before we free these resources.
            if (completionHandler != null) {
                runCompletionHandler();
            }
        } catch (InterruptedException interruptedException) {
            /*If we have an error the work of the thread is already done and the thread was exiting
              anyway.  If we don't have en error then the thread may have exited improperly and
              the calling code should be able to inspect it.
            */
            if (requestError != null) {
                requestError = interruptedException;
            }
        } finally {
            if (response != null && !closed) {

                closed = true;
                response.close();
            }
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
                 * OKHttp body.string closes the request so we need to track this
                 * because you can't close a request twice.
                 */
                closed = true;
            }
        } else {
            return "";
        }
    }
}

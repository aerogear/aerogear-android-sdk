package org.aerogear.mobile.core.http;

import org.aerogear.mobile.core.executor.AppExecutors;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by summers on 1/23/18.
 */

class OkHttpResponse implements HttpResponse {

    private static final long DEFAULT_TIMEOUT = 30;
    private final AppExecutors appExecutors;
    private final Call okHttpCall;
    private Response response;
    private Runnable completionHandler;
    private Exception requestError;
    private CountDownLatch requestCompleteLatch = new CountDownLatch(1);

    public OkHttpResponse(final Call okHttpCall, AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
        this.okHttpCall = okHttpCall;
        appExecutors.networkThread().execute(()-> {
            try {
                response = okHttpCall.execute();
                requestCompleteLatch.countDown();
                if (completionHandler != null) {
                    completionHandler.run();
                }
            } catch (IOException e) {
                requestError = e;
                requestCompleteLatch.countDown();
            }
        });
    }

    @Override
    public HttpResponse onComplete(Runnable completionHandler) {
        if (response != null) {
            completionHandler.run();
        } else {
            this.completionHandler = completionHandler;
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
        } catch (InterruptedException interruptedException) {
            /*If we have an error the work of the thread is already done and the thread was exiting
              anyway.  If we don't have en error then the thread may have exited improperly and
              the calling code should be able to inspect it.
            */
            if (requestError != null) {
                requestError = interruptedException;
            }
        } finally {
            if (response != null) {
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
            }
        } else {
            return "";
        }
    }
}

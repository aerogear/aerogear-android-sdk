package org.aerogear.mobile.core.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLPeerUnverifiedException;

import org.aerogear.mobile.core.exception.HttpException;
import org.aerogear.mobile.core.executor.AppExecutors;

import okhttp3.Call;
import okhttp3.Response;

public class OkHttpResponse implements HttpResponse {

    private static final long DEFAULT_TIMEOUT = 30;
    private Exception error;
    private Response response;
    private boolean closed = false;

    /**
     * Basic constructor which will wrap a okHttp {@link Call} object
     *
     * @param okHttpCall the OKHTTP call to use to make this request
     */

    public OkHttpResponse(Call okHttpCall) throws IOException {
        try {
            // this call will throw an exception only when a connection problem occurs
            // even when there is a 400 or 500 no exception thrown
            response = okHttpCall.execute();
            
            if (!(response.isSuccessful() || response.isRedirect())) {
                // status 400 or 500
                throw new HttpException(response.code(), response.message());
            }
        } catch (Exception exception) {
            this.error = exception;
            throw new RuntimeException(exception);
        }
    }

    @Override
    public int getStatus() {
        return response.code();
    }

    @Override
    public void waitForCompletionAndClose() {
        if (response != null && !closed) {
            closed = true;
            response.close();
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
    public InputStream streamBody() {
        if (response != null) {
            return response.body().byteStream();
        } else {
            return new ByteArrayInputStream(new byte[0]);
        }
    }

    @Override
    public Exception getError() {
        return this.error;
    }


    public boolean isClosed() {
        return closed;
    }

}

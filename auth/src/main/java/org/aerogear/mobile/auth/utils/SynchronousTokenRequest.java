package org.aerogear.mobile.auth.utils;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.ClientAuthentication;
import net.openid.appauth.TokenRequest;
import net.openid.appauth.TokenResponse;
import net.openid.appauth.internal.Logger;
import net.openid.appauth.internal.UriUtil;

/**
 * This is a fork of net.openid.appauth.AuthorizationService.TokenRequestTask which is a synchronous
 * task on the calling thread.
 */
public class SynchronousTokenRequest {
    private TokenRequest mRequest;
    private ClientAuthentication mClientAuthentication;

    private final AppAuthConfiguration mClientConfiguration =
                    new AppAuthConfiguration.Builder().build();

    public SynchronousTokenRequest(TokenRequest request,
                    @NonNull ClientAuthentication clientAuthentication) {
        mRequest = request;
        mClientAuthentication = clientAuthentication;
    }


    public TokenResponse request() throws AuthorizationException {
        InputStream is = null;
        JSONObject json;
        try {
            HttpURLConnection conn = mClientConfiguration.getConnectionBuilder()
                            .openConnection(mRequest.configuration.tokenEndpoint);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            addJsonToAcceptHeader(conn);
            conn.setDoOutput(true);

            Map<String, String> headers =
                            mClientAuthentication.getRequestHeaders(mRequest.clientId);
            if (headers != null) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    conn.setRequestProperty(header.getKey(), header.getValue());
                }
            }

            Map<String, String> parameters = mRequest.getRequestParameters();
            Map<String, String> clientAuthParams =
                            mClientAuthentication.getRequestParameters(mRequest.clientId);
            if (clientAuthParams != null) {
                parameters.putAll(clientAuthParams);
            }

            String queryData = UriUtil.formUrlEncode(parameters);
            conn.setRequestProperty("Content-Length", String.valueOf(queryData.length()));
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write(queryData);
            wr.flush();

            if (conn.getResponseCode() >= HttpURLConnection.HTTP_OK
                            && conn.getResponseCode() < HttpURLConnection.HTTP_MULT_CHOICE) {
                is = conn.getInputStream();
            } else {
                is = conn.getErrorStream();
            }
            String response = readInputStream(is);
            json = new JSONObject(response);
        } catch (IOException ex) {
            Logger.debugWithStack(ex, "Failed to complete exchange request");
            throw AuthorizationException
                            .fromTemplate(AuthorizationException.GeneralErrors.NETWORK_ERROR, ex);
        } catch (JSONException ex) {
            Logger.debugWithStack(ex, "Failed to complete exchange request");
            throw AuthorizationException.fromTemplate(
                            AuthorizationException.GeneralErrors.JSON_DESERIALIZATION_ERROR, ex);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception ignore) {
                    /* ignore */}
            }
        }
        if (json.has(AuthorizationException.PARAM_ERROR)) {
            AuthorizationException ex;
            try {
                String error = json.getString(AuthorizationException.PARAM_ERROR);
                ex = AuthorizationException.fromOAuthTemplate(
                                AuthorizationException.TokenRequestErrors.byString(error), error,
                                json.optString(AuthorizationException.PARAM_ERROR_DESCRIPTION,
                                                null),
                                UriUtil.parseUriIfAvailable(json.optString(
                                                AuthorizationException.PARAM_ERROR_URI)));
            } catch (JSONException jsonEx) {
                ex = AuthorizationException.fromTemplate(
                                AuthorizationException.GeneralErrors.JSON_DESERIALIZATION_ERROR,
                                jsonEx);
            }
            throw ex;
        }

        TokenResponse response;
        try {
            response = new TokenResponse.Builder(mRequest).fromResponseJson(json).build();
        } catch (JSONException jsonEx) {
            throw AuthorizationException.fromTemplate(
                            AuthorizationException.GeneralErrors.JSON_DESERIALIZATION_ERROR,
                            jsonEx);

        }

        Logger.debug("Token exchange with %s completed", mRequest.configuration.tokenEndpoint);
        return response;
    }


    private String readInputStream(InputStream is) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }

        return outputStream.toString("UTF-8");
    }


    /**
     * GitHub will only return a spec-compliant response if JSON is explicitly defined as an
     * acceptable response type. As this is essentially harmless for all other spec-compliant IDPs,
     * we add this header if no existing Accept header has been set by the connection builder.
     */
    private void addJsonToAcceptHeader(URLConnection conn) {
        if (TextUtils.isEmpty(conn.getRequestProperty("Accept"))) {
            conn.setRequestProperty("Accept", "application/json");
        }
    }
}

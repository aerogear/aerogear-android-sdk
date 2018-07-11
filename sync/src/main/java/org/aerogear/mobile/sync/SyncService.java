package org.aerogear.mobile.sync;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import javax.annotation.Nonnull;

import com.apollographql.apollo.ApolloClient;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;

import okhttp3.OkHttpClient;

public final class SyncService {

    public static final String TYPE = "sync";

    private static SyncService instance;

    private final ApolloClient apolloClient;

    public SyncService(@Nonnull OkHttpClient okHttpClient, @Nonnull String serverUrl) {
        apolloClient = ApolloClient.builder().serverUrl(nonNull(serverUrl, "serverUrl"))
                        .okHttpClient(nonNull(okHttpClient, "okHttpClient")).build();
    }

    public static SyncService getInstance() {
        if (instance == null) {
            MobileCore mobileCore = MobileCore.getInstance();
            ServiceConfiguration configuration = mobileCore.getServiceConfigurationByType(TYPE);
            String serverUrl = configuration.getUrl();
            OkHttpClient okHttpClient = mobileCore.getHttpLayer().getClient();
            SyncService.instance = new SyncService(okHttpClient, serverUrl);
        }
        return instance;
    }

    public ApolloClient getApolloClient() {
        return apolloClient;
    }

}

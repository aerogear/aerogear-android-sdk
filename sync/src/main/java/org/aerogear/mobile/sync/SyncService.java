package org.aerogear.mobile.sync;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import javax.annotation.Nonnull;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Mutation;
import com.apollographql.apollo.api.Operation;
import com.apollographql.apollo.api.Query;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.configuration.ServiceConfiguration;
import org.aerogear.mobile.core.executor.AppExecutors;
import org.aerogear.mobile.core.reactive.Request;
import org.aerogear.mobile.core.reactive.Requester;
import org.aerogear.mobile.core.reactive.Responder;

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

    public SyncQuery query(Query query) {
        return new SyncQuery(this.apolloClient, query);
    }

    public SyncMutation mutation(Mutation mutation) {
        return new SyncMutation(this.apolloClient, mutation);
    }

    public static class SyncQuery {

        private final ApolloClient apolloClient;
        private final Query query;

        SyncQuery(ApolloClient apolloClient, Query query) {
            this.apolloClient = apolloClient;
            this.query = query;
        }

        public <T extends Operation.Data> Request<Response<T>> execute(Class<T> responseDataClass) {

            return Requester.call((Responder<Response<T>> requestCallback) -> apolloClient
                            .query(query).enqueue(new ApolloCall.Callback<T>() {
                                @Override
                                public void onResponse(@Nonnull Response<T> response) {
                                    requestCallback.onResult(response);
                                }

                                @Override
                                public void onFailure(@Nonnull ApolloException e) {
                                    requestCallback.onException(e);
                                }
                            })).respondOn(new AppExecutors().networkThread());

        }

    }

    public static class SyncMutation {

        private final ApolloClient apolloClient;
        private final Mutation mutation;

        SyncMutation(ApolloClient apolloClient, Mutation mutation) {
            this.apolloClient = apolloClient;
            this.mutation = mutation;
        }

        public <T extends Operation.Data> Request<Response<T>> execute(Class<T> responseDataClass) {

            return Requester.call((Responder<Response<T>> requestCallback) -> apolloClient
                            .mutate(mutation).enqueue(new ApolloCall.Callback<T>() {
                                @Override
                                public void onResponse(@Nonnull Response<T> response) {
                                    requestCallback.onResult(response);
                                }

                                @Override
                                public void onFailure(@Nonnull ApolloException e) {
                                    requestCallback.onException(e);
                                }
                            })).respondOn(new AppExecutors().networkThread());

        }
    }

}

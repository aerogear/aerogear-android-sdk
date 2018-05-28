package org.aerogear.mobile.core.bootstrap;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.aerogear.mobile.core.MobileCore;
import org.aerogear.mobile.core.exception.ConfigurationNotFoundException;
import org.aerogear.mobile.core.executor.AppExecutors;
import org.aerogear.mobile.core.metrics.MetricsService;
import org.aerogear.mobile.core.reactive.Responder;

public class AeroGearBootstrap extends ContentProvider {

    @Override
    public boolean onCreate() {
        MobileCore.init(getContext());

        try {
            new MetricsService().sendAppAndDeviceMetrics()
                            .requestOn(new AppExecutors().mainThread())
                            .respondWith(new Responder<Boolean>() {
                                @Override
                                public void onResult(Boolean value) {
                                    MobileCore.getLogger().info("Default metrics sent");
                                }

                                @Override
                                public void onException(Exception exception) {
                                    MobileCore.getLogger().error(exception.getMessage(), exception);
                                }
                            });
        } catch (ConfigurationNotFoundException e) {
            MobileCore.getLogger().debug("Metrics SDK is not enabled");
        }

        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                    @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                    @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                    @Nullable String[] selectionArgs) {
        return 0;
    }

}

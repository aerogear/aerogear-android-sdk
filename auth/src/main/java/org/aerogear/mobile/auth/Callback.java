package org.aerogear.mobile.auth;

public interface Callback<T extends Object> {

    void onSuccess(T models);

    void onError(Throwable error);
}

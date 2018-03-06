package org.aerogear.mobile.auth;

public interface Callback<T extends Object> {
    default void onSuccess() {}
    default void onSuccess(T models) {onSuccess();}
    void onError(Throwable error);
}

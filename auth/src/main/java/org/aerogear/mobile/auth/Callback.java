package org.aerogear.mobile.auth;

public interface Callback<T extends Object> {
    public void onSuccess(T models);
    public void onError(Throwable error);
}

package org.aerogear.mobile.core.helper;

import java.util.concurrent.CountDownLatch;

import org.aerogear.mobile.core.reactive.Responder;

public class TestResponder<T> implements Responder<T> {
    private final CountDownLatch latch;

    public boolean passed = false;
    public T resultValue = null;
    public boolean failed;
    public String errorMessage = "";
    public String name = "";

    public TestResponder() {
        this.latch = null;
    }

    public TestResponder(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onResult(T value) {
        passed = true;
        resultValue = value;
        if (latch != null) {
            latch.countDown();
        }
    }

    @Override
    public void onException(Exception e) {
        failed = true;
        errorMessage = e.getMessage();
        if (latch != null) {
            latch.countDown();
        }
    }

    @Override
    public String toString() {
        return "TestResponder{" + "name='" + name + '\'' + '}';
    }
}

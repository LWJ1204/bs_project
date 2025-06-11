package com.lwj.FinalServer.web.net.service;


import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;

public interface CustomRetryListener {
    public <E extends Throwable, T> void lastRetry(RetryContext context, RetryCallback<T,E> callback, Throwable throwable);
    public <E extends Throwable, T> void onRetry(RetryContext context, RetryCallback<T,E> callback, Throwable throwable);
}

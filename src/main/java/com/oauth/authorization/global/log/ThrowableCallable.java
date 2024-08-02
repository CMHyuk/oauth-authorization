package com.oauth.authorization.global.log;

@FunctionalInterface
public interface ThrowableCallable<V> {

    V call() throws Throwable;
}

package com.oauth.authorization.global.log;

@FunctionalInterface
public interface ThrowableRunnable {

    void run() throws Throwable;
}

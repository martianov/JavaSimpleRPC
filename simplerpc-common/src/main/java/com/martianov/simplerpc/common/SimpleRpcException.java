package com.martianov.simplerpc.common;

/**
 * Base simple rpc exception.
 *
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class SimpleRpcException extends Exception {
    public SimpleRpcException() {
        super();
    }

    public SimpleRpcException(String message) {
        super(message);
    }

    public SimpleRpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public SimpleRpcException(Throwable cause) {
        super(cause);
    }

    protected SimpleRpcException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

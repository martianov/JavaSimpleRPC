package com.martianov.simplerpc.common.connection;

import com.martianov.simplerpc.common.SimpleRpcException;

/**
 * Simple RPC connection exception.
 *
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class ConnectionException extends SimpleRpcException {
    public ConnectionException() {
    }

    public ConnectionException(String message) {
        super(message);
    }

    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectionException(Throwable cause) {
        super(cause);
    }

    public ConnectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

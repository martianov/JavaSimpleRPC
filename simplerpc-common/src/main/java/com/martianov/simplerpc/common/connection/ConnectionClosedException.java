package com.martianov.simplerpc.common.connection;

/**
 * Connection closed.
 *
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class ConnectionClosedException extends ConnectionException {
    public ConnectionClosedException() {
    }

    public ConnectionClosedException(String message) {
        super(message);
    }

    public ConnectionClosedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectionClosedException(Throwable cause) {
        super(cause);
    }

    public ConnectionClosedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

package com.martianov.simplerpc.client;

import com.martianov.simplerpc.common.SimpleRpcException;

/**
 * Error during execution of method on remote side.
 *
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class RemoteExecutionException extends SimpleRpcException {
    public RemoteExecutionException() {
    }

    public RemoteExecutionException(String message) {
        super(message);
    }

    public RemoteExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemoteExecutionException(Throwable cause) {
        super(cause);
    }

    public RemoteExecutionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

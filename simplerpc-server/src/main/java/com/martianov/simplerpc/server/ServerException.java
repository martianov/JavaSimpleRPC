package com.martianov.simplerpc.server;

import com.martianov.simplerpc.common.SimpleRpcException;

/**
 * SimpleRpc Server exception.
 *
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class ServerException extends SimpleRpcException {
    public ServerException() {
        super();
    }

    public ServerException(String message) {
        super(message);
    }

    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerException(Throwable cause) {
        super(cause);
    }

    protected ServerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

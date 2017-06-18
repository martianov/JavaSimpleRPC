package com.martianov.simplerpc.server.services;

import com.martianov.simplerpc.common.SimpleRpcException;

/**
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class ServiceException extends SimpleRpcException {
    public ServiceException() {
        super();
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }

    protected ServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

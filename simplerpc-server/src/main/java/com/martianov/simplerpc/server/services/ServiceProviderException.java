package com.martianov.simplerpc.server.services;

import com.martianov.simplerpc.server.ServerException;

/**
 * Service provider error.
 *
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class ServiceProviderException extends ServerException {
    public ServiceProviderException() {
        super();
    }

    public ServiceProviderException(String message) {
        super(message);
    }

    public ServiceProviderException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceProviderException(Throwable cause) {
        super(cause);
    }

    protected ServiceProviderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

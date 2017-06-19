package com.martianov.simplerpc.common.junk;

import com.martianov.simplerpc.common.SimpleRpcException;

/**
 * Serializer exception.
 *
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class SerializerException extends SimpleRpcException {
    public SerializerException() {
        super();
    }

    public SerializerException(String message) {
        super(message);
    }

    public SerializerException(String message, Throwable cause) {
        super(message, cause);
    }

    public SerializerException(Throwable cause) {
        super(cause);
    }

    protected SerializerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

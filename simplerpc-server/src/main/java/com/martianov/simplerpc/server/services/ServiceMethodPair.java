package com.martianov.simplerpc.server.services;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class ServiceMethodPair {
    private final Object service;
    private final Method method;

    public ServiceMethodPair(Object service, Method method) {
        this.service = service;
        this.method = method;
    }

    public boolean isVoid() {
        return method.getReturnType().equals(Void.TYPE);
    }

    public Object invoke(Object[] args) throws ServiceException {
        try {
            return method.invoke(service, args);
        } catch (InvocationTargetException e) {
            throw new ServiceException(e.getCause().getMessage(), e);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }
}

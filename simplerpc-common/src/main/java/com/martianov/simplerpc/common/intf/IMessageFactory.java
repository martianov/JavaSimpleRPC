package com.martianov.simplerpc.common.intf;

/**
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public interface IMessageFactory {
    IRequest createRequest(String serviceName, String methodName, Object[] arguments);
    IResult createResult(Object result);
    IError createError(String message);
    IVoidResult createVoidResult();
}

package com.martianov.simplerpc.common.message;

/**
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public interface IMessageFactory {
    IRequest createRequest(long callID, String serviceName, String methodName, Object[] arguments);
    IResult createResult(long callID, Object result);
    IError createError(long callID, String message);
    IVoidResult createVoidResult(long callID);
}

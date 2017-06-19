package com.martianov.simplerpc.common.message.impl.basic;

import com.martianov.simplerpc.common.message.*;

/**
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class BasicMessageFactory implements IMessageFactory {
    @Override
    public IRequest createRequest(long callID, String serviceName, String methodName, Object[] arguments) {
        BasicRequest mesage = new BasicRequest();

        mesage.setCallID(callID);
        mesage.setServiceName(serviceName);
        mesage.setMethodName(methodName);
        mesage.setArguments(arguments);

        return mesage;
    }

    @Override
    public IResult createResult(long callID, Object result) {
        BasicResult mesage = new BasicResult();

        mesage.setCallID(callID);
        mesage.setResult(result);

        return mesage;
    }

    @Override
    public IError createError(long callID, String message) {
        BasicError mesage = new BasicError();

        mesage.setCallID(callID);
        mesage.setMessage(message);

        return mesage;
    }

    @Override
    public IVoidResult createVoidResult(long callID) {
        BasicVoidResult mesage = new BasicVoidResult();

        mesage.setCallID(callID);

        return mesage;
    }
}

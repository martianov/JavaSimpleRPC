package com.martianov.simplerpc.common.impl.basic;

import com.martianov.simplerpc.common.intf.*;

/**
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class BasicMessageFactory implements IMessageFactory {
    @Override
    public IRequest createRequest(String serviceName, String methodName, Object[] arguments) {
        BasicRequest request = new BasicRequest();

        request.setServiceName(serviceName);
        request.setMethodName(methodName);
        request.setArguments(arguments);

        return request;
    }

    @Override
    public IResult createResult(Object result) {
        BasicResult res = new BasicResult();

        res.setResult(result);

        return res;
    }

    @Override
    public IError createError(String message) {
        BasicError err = new BasicError();

        err.setMessage(message);

        return err;
    }

    @Override
    public IVoidResult createVoidResult() {
        BasicVoidResult res = new BasicVoidResult();

        return res;
    }
}

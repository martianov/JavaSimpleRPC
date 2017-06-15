package com.martianov.simplerpc.common.impl.basic;

import com.martianov.simplerpc.common.intf.IRequest;

/**
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class BasicRequest extends BasicMessage implements IRequest {
    private String serviceName;
    private String methodName;
    private Object[] arguments;

    public BasicRequest() {
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    @Override
    public String getServiceName() {
        return serviceName;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public Object[] getArguments() {
        return arguments;
    }
}

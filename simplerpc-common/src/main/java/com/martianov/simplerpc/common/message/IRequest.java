package com.martianov.simplerpc.common.message;

/**
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public interface IRequest extends IMessage {
    String getServiceName();
    String getMethodName();
    Object[] getArguments();
}

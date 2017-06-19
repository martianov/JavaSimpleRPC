package com.martianov.simplerpc.common.message.impl.basic;

import com.martianov.simplerpc.common.message.IResponse;

/**
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class BasicResponse extends BasicMessage implements IResponse {
    @Override
    public String toString() {
        return "BasicResponse{" +
                "callID=" + getCallID() +
                "}";
    }
}

package com.martianov.simplerpc.common.impl.basic;

import com.martianov.simplerpc.common.intf.IResponse;

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

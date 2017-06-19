package com.martianov.simplerpc.common.message.impl.basic;

import com.martianov.simplerpc.common.message.IVoidResult;

/**
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class BasicVoidResult extends BasicResponse implements IVoidResult {
    @Override
    public String toString() {
        return "BasicVoidResult{" +
                "callID=" + getCallID() +
                "}";
    }
}

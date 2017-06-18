package com.martianov.simplerpc.common.impl.basic;

import com.martianov.simplerpc.common.intf.IVoidResult;

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

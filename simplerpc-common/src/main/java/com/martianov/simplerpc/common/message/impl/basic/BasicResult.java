package com.martianov.simplerpc.common.message.impl.basic;

import com.martianov.simplerpc.common.message.IResult;

/**
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class BasicResult extends BasicResponse implements IResult {
    private Object result;

    public BasicResult() {
    }

    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public Object getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "BasicResult{" +
                "callID=" + getCallID() +
                ", result=" + result +
                '}';
    }
}

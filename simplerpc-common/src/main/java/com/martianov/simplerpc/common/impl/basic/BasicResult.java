package com.martianov.simplerpc.common.impl.basic;

import com.martianov.simplerpc.common.intf.IResult;

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
}

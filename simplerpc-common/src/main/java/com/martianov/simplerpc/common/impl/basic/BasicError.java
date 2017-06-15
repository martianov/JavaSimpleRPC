package com.martianov.simplerpc.common.impl.basic;

import com.martianov.simplerpc.common.intf.IError;

/**
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class BasicError extends BasicResponse implements IError {
    private String message;

    public BasicError() {
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return null;
    }
}

package com.martianov.simplerpc.common.impl.basic;

import com.martianov.simplerpc.common.intf.IMessage;

import java.io.Serializable;

/**
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class BasicMessage implements IMessage, Serializable {
    private long callID;

    public BasicMessage() {
    }

    public void setCallID(long callID) {
        this.callID = callID;
    }

    @Override
    public long getCallID() {
        return 0;
    }
}

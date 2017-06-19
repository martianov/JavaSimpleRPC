package com.martianov.simplerpc.common.message.impl.basic;

import com.martianov.simplerpc.common.message.IMessage;

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
        return callID;
    }

    @Override
    public String toString() {
        return "BasicMessage{" +
                "callID=" + callID +
                '}';
    }
}

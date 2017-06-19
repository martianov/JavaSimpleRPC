package com.martianov.simplerpc.common.message;

/**
 * Base interface for rpc requests and responses.
 *
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public interface IMessage {
    /**
     * Returns call ID of message. Request and response of the same method call must have the same call ID.
     * Messages correspond to different calls must hva different call ID.
     *
     * @returns call ID of message.
     * */
    long getCallID();
}

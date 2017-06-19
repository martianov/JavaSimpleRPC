package com.martianov.simplerpc.common.connection;

import com.martianov.simplerpc.common.message.IMessage;

/**
 * Connection between Simple RPC Client and Server.
 *
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public interface IConnection {
    /**
     * Send message.
     * @param message message to send.
     *
     * @throws ConnectionClosedException connection is closed.
     * @throws ConnectionException I/O error.
     * */
    void send(IMessage message) throws ConnectionException;

    /**
     * Receive message from connection. Blocking operation.
     *
     * @return received message.
     * @throws ConnectionClosedException connection is closed.
     * @throws ConnectionException I/O error.
     * */
    IMessage receive() throws ConnectionException;
}

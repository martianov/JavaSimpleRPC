package com.martianov.simplerpc.client;

import com.martianov.simplerpc.common.connection.ConnectionException;
import com.martianov.simplerpc.common.connection.IConnection;
import com.martianov.simplerpc.common.connection.impl.BasicSocketConnection;
import com.martianov.simplerpc.common.message.IMessageFactory;
import com.martianov.simplerpc.common.message.impl.basic.BasicMessageFactory;

/**
 * Simple RPC client through TCP.
 *
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class Client extends AbstractClient {
    /**
     * Creates TCP client.
     *
     * @param host server host
     * @param port server port
     * @throws ConnectionException failed to establish connection.
     */
    public static Client create(String host, int port) throws ConnectionException {
        IConnection conn = BasicSocketConnection.connect(host, port);
        return new Client(new BasicMessageFactory(), conn);
    }

    private Client(IMessageFactory messageFactory, IConnection conn) {
        super(new BasicMessageFactory(), conn);
    }
}

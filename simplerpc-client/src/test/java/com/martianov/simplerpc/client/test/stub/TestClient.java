package com.martianov.simplerpc.client.test.stub;

import com.martianov.simplerpc.client.AbstractClient;
import com.martianov.simplerpc.common.connection.IConnection;
import com.martianov.simplerpc.common.message.IMessageFactory;

/**
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class TestClient extends AbstractClient {
    public TestClient(IMessageFactory messageFactory, IConnection conn) {
        super(messageFactory, conn);
    }
}

package com.martianov.simplerpc.client.test.stub;

import com.martianov.simplerpc.client.AbstractClient;
import com.martianov.simplerpc.common.connection.IConnection;
import com.martianov.simplerpc.common.message.IMessageFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class TestClient extends AbstractClient {
    public AtomicBoolean closed = new AtomicBoolean(false);

    public TestClient(IMessageFactory messageFactory, IConnection conn) {
        super(messageFactory, conn);
    }

    @Override
    public void close() {
        closed.set(true);
        super.close();
    }

    public boolean isClosed() {
        return closed.get();
    }
}

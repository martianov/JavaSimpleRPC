package com.martianov.simplerpc.common.test;

import com.martianov.simplerpc.common.connection.impl.BasicPipedConnection;
import com.martianov.simplerpc.common.message.impl.basic.BasicMessageFactory;
import com.martianov.simplerpc.common.connection.IConnection;
import com.martianov.simplerpc.common.message.IMessageFactory;

/**
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class BasicPipedConnectionTest extends AbstractConnectionTest {
    BasicPipedConnection conn;

    @Override
    protected IConnection getClientConnection() {
        return conn;
    }

    @Override
    protected IConnection getServerConnection() {
        return conn.getOtherSide();
    }

    @Override
    protected IMessageFactory getMessageFactory() {
        return new BasicMessageFactory();
    }

    @Override
    protected void doInit() throws Exception {
        conn = BasicPipedConnection.create();
    }

    @Override
    protected void doShutdown() throws Exception {
    }
}

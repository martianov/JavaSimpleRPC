package com.martianov.simplerpc.common.test;

import com.martianov.simplerpc.common.connection.ConnectionException;
import com.martianov.simplerpc.common.connection.IConnection;
import com.martianov.simplerpc.common.message.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public abstract class AbstractConnectionTest {
    private IConnection clientConnection = null;
    private IConnection serverConnection = null;
    private IMessageFactory messageFactory = null;

    protected abstract IConnection getClientConnection();
    protected abstract IConnection getServerConnection();
    protected abstract IMessageFactory getMessageFactory();

    protected abstract void doInit() throws Exception;

    @Before
    public void init() throws Exception {
        doInit();

        clientConnection = getClientConnection();
        serverConnection = getServerConnection();
        messageFactory = getMessageFactory();
    }

    protected abstract void doShutdown() throws Exception;

    @After
    public void shutdown() throws Exception {
        doShutdown();
    }

    private IMessage roundtrip(IMessage message) throws ConnectionException, ExecutionException, InterruptedException {
        final CompletableFuture<IMessage> fut = new CompletableFuture<>();
        Thread receiver = new Thread(() -> {
            try {
                IMessage received = serverConnection.receive();
                fut.complete(received);
            } catch (ConnectionException e) {
                fut.completeExceptionally(e);
            }
        });
        receiver.start();

        clientConnection.send(message);

        return fut.get();
    }

    @Test
    public void testRequest() throws Exception {
        IRequest reqA = messageFactory.createRequest(0,"service", "method", new Object[0]);
        IRequest reqB = (IRequest) roundtrip(reqA);

        Assert.assertEquals(reqA.getCallID(), reqB.getCallID());
        Assert.assertEquals(reqA.getServiceName(), reqB.getServiceName());
        Assert.assertEquals(reqA.getMethodName(), reqB.getMethodName());
        Assert.assertArrayEquals(reqA.getArguments(), reqB.getArguments());
    }

    @Test
    public void testResult() throws Exception {
        IResult resA = messageFactory.createResult(0, new Integer(100));
        IResult resB = (IResult) roundtrip(resA);

        Assert.assertEquals(resA.getCallID(), resB.getCallID());
        Assert.assertEquals(resA.getResult(), resB.getResult());
    }


    @Test
    public void testVoidResult() throws Exception {
        IVoidResult resA = messageFactory.createVoidResult(0);
        IVoidResult resB = (IVoidResult) roundtrip(resA);

        Assert.assertEquals(resA.getCallID(), resB.getCallID());
    }


    @Test
    public void testError() throws Exception {
        IError errA = messageFactory.createError(0, "Error message");
        IError errB = (IError) roundtrip(errA);

        Assert.assertEquals(errA.getCallID(), errB.getCallID());
        Assert.assertEquals(errA.getMessage(), errB.getMessage());
    }
}

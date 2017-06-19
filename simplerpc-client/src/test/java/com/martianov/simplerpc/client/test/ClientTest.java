package com.martianov.simplerpc.client.test;

import com.martianov.simplerpc.client.RemoteExecutionException;
import com.martianov.simplerpc.common.connection.ConnectionException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Test for basic client functionality.
 *
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class ClientTest extends AbstractClientTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Override
    protected void registerResponses() {
        registerResponse("testConcurrentCallsA", messageFactory().createResult(1, new Integer(100)), 3000);
        registerResponse("testConcurrentCallsB", messageFactory().createResult(2, new Integer(101)), 2000);
        registerResponse("testConcurrentCallsC", messageFactory().createResult(2, new Integer(102)), 4000);
        registerResponse("testConcurrentCallsD", messageFactory().createResult(2, new Integer(103)), 1000);
        registerResponse("testConcurrentCallsE", messageFactory().createResult(2, new Integer(104)), 5000);

        registerResponse("testThrowException", messageFactory().createError(3, "testThrowException"), 50);

        registerResponse("testVoidResult", messageFactory().createVoidResult(4), 50);
    }

    @Test
    public void testConcurrentCalls() throws ExecutionException, InterruptedException {
        CompletableFuture<Object> a = remoteCall("testConcurrentCallsA", "any", new Object[] {});
        CompletableFuture<Object> b = remoteCall("testConcurrentCallsB", "any", new Object[] {});
        CompletableFuture<Object> c = remoteCall("testConcurrentCallsC", "any", new Object[] {});
        CompletableFuture<Object> d = remoteCall("testConcurrentCallsD", "any", new Object[] {});
        CompletableFuture<Object> e = remoteCall("testConcurrentCallsE", "any", new Object[] {});

        Integer aVal = (Integer) (a.get());
        Integer bVal = (Integer) (b.get());
        Integer cVal = (Integer) (c.get());
        Integer dVal = (Integer) (d.get());
        Integer eVal = (Integer) (e.get());

        Assert.assertEquals(new Integer(100), aVal);
        Assert.assertEquals(new Integer(101), bVal);
        Assert.assertEquals(new Integer(102), cVal);
        Assert.assertEquals(new Integer(103), dVal);
        Assert.assertEquals(new Integer(104), eVal);
    }

    @Test
    public void testThrowException() throws ConnectionException, RemoteExecutionException {
        exception.expect(RemoteExecutionException.class);
        client().remoteCall("testThrowException", "any", new Object[] {});
    }

    @Test
    public void testVoidResult() throws ConnectionException, RemoteExecutionException {
        Object result = client().remoteCall("testVoidResult", "any", new Object[] {});
        Assert.assertNull(result);
    }
}

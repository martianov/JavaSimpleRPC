package com.martianov.simplerpc.client.test;

import com.martianov.simplerpc.client.ClientException;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class ClientTest extends AbstractClientTest {
    @Override
    protected void registerResponses() {
        registerResponse("a", messageFactory().createResult(1, new Integer(100)), 200);
        registerResponse("b", messageFactory().createResult(2, new Integer(101)), 100);
    }

    @Test
    public void test() throws ClientException, ExecutionException, InterruptedException {
        CompletableFuture<Object> a = remoteCall("a", "b", new Object[] {});
        CompletableFuture<Object> b = remoteCall("b", "b", new Object[] {});

        Integer aVal = (Integer) (a.get());
        Integer bVal = (Integer) (b.get());

        Assert.assertEquals(new Integer(100), aVal);
        Assert.assertEquals(new Integer(101), bVal);
    }
}

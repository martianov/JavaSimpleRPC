package com.martianov.simplerpc.server.test;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class  BasicServerTest extends AbstractServerTest {
    AtomicLong atomicLongService =  new AtomicLong(0);

    @Override
    protected void registerServices() {
        addService("atomicLong", atomicLongService);
    }

    @Test
    public void testService() {
        System.out.println(send(messageFactory().createRequest(0, "atomicLong", "incrementAndGet", new Object[0])));

        Assert.assertEquals(1, atomicLongService.get());
    }
}

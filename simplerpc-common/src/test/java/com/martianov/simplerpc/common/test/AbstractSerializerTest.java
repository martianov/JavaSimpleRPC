package com.martianov.simplerpc.common.test;

import com.martianov.simplerpc.common.intf.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public abstract class AbstractSerializerTest {
    private ISerializer serializer = null;
    private IMessageFactory messageFactory = null;

    protected abstract ISerializer createSerializer();
    protected abstract IMessageFactory createFactory();

    @Before
    public void init() {
        serializer = createSerializer();
        messageFactory = createFactory();
    }

    @Test
    public void testSerializer() throws SerializerException {
        IRequest req = messageFactory.createRequest("service", "method", new Object[0]);

        byte[] reqBytes = serializer.toBytes(req);

        IMessage deserializedReqUntyped = serializer.fromBytes(reqBytes);

        Assert.assertTrue(deserializedReqUntyped instanceof IRequest);

        IRequest deserializedReq = (IRequest) deserializedReqUntyped;

        Assert.assertEquals(req.getServiceName(), deserializedReq.getServiceName());
        Assert.assertEquals(req.getMethodName(), deserializedReq.getMethodName());
        Assert.assertArrayEquals(req.getArguments(), deserializedReq.getArguments());
    }
}

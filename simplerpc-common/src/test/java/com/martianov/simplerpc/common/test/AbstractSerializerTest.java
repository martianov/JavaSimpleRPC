package com.martianov.simplerpc.common.test;

import com.martianov.simplerpc.common.intf.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

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

    private IMessage roundtrip(IMessage message) throws SerializerException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        serializer.write(bos, message);

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        return serializer.read(bis);
    }

    @Test
    public void testSerializer() throws SerializerException {
        IRequest req = messageFactory.createRequest("service", "method", new Object[0]);
        IRequest deserializedReq = (IRequest) roundtrip(req);

        Assert.assertEquals(req.getServiceName(), deserializedReq.getServiceName());
        Assert.assertEquals(req.getMethodName(), deserializedReq.getMethodName());
        Assert.assertArrayEquals(req.getArguments(), deserializedReq.getArguments());
    }
}

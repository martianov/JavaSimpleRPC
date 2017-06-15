package com.martianov.simplerpc.common.test;

import com.martianov.simplerpc.common.impl.basic.BasicMessageFactory;
import com.martianov.simplerpc.common.impl.basic.BasicSerializer;
import com.martianov.simplerpc.common.intf.IMessageFactory;
import com.martianov.simplerpc.common.intf.ISerializer;

/**
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class BasicSerializerTest extends AbstractSerializerTest {
    @Override
    protected ISerializer createSerializer() {
        return new BasicSerializer();
    }

    @Override
    protected IMessageFactory createFactory() {
        return new BasicMessageFactory();
    }
}

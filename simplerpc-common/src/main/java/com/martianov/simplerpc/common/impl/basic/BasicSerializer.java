package com.martianov.simplerpc.common.impl.basic;

import com.martianov.simplerpc.common.intf.IMessage;
import com.martianov.simplerpc.common.intf.ISerializer;
import com.martianov.simplerpc.common.intf.SerializerException;

import java.io.*;

/**
 * TODO: make things right
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class BasicSerializer implements ISerializer {
    @Override
    public byte[] toBytes(IMessage msg) throws SerializerException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bos.toByteArray();
    }

    @Override
    public IMessage fromBytes(byte[] bytes) throws SerializerException {
        IMessage msg = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try {
            ObjectInputStream ois = new ObjectInputStream(bis);
            msg = (IMessage) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return msg;
    }
}

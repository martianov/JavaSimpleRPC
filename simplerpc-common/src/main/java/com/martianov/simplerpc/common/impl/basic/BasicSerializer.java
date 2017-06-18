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
    public IMessage read(InputStream is) throws IOException, SerializerException {
        IMessage message = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(is);
            message = (IMessage) ois.readObject();
        } catch (ClassNotFoundException | ClassCastException e) {
            throw new SerializerException("Failed to deserialize message", e);
        }
        return message;
    }

    @Override
    public void write(OutputStream os, IMessage msg) throws IOException, SerializerException {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(msg);
        }  catch (InvalidClassException | NotSerializableException e) {
            throw new SerializerException("Failed to serialize message", e);
        }
    }
}

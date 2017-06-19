package com.martianov.simplerpc.common.junk;

import com.martianov.simplerpc.common.message.IMessage;
import com.martianov.simplerpc.common.junk.ISerializer;
import com.martianov.simplerpc.common.junk.SerializerException;

import java.io.*;

/**
 * TODO: make things right
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class BasicSerializer implements ISerializer {
    private final InputStream intputStream;
    private final OutputStream outputStream;

    private ObjectInputStream ois = null;
    private ObjectOutputStream oos = null;

    public BasicSerializer(InputStream intputStream, OutputStream outputStream) throws IOException {
        this.intputStream = intputStream;
        this.outputStream = outputStream;




    }

    @Override
    public IMessage read(InputStream is1) throws IOException, SerializerException {
        IMessage message = null;
        try {
            //ObjectInputStream ois = new ObjectInputStream(is);
            if (null == ois) {
                ois = new ObjectInputStream(intputStream);
            }
            message = (IMessage) ois.readObject();
        } catch (ClassNotFoundException | ClassCastException e) {
            throw new SerializerException("Failed to deserialize message", e);
        }
        return message;
    }

    @Override
    public void write(OutputStream os, IMessage msg) throws IOException, SerializerException {
        try {
            //ObjectOutputStream oos = new ObjectOutputStream(os);
            if (null == oos) {
                oos = new ObjectOutputStream(outputStream);
            }
            oos.writeObject(msg);
            oos.flush();
        }  catch (InvalidClassException | NotSerializableException e) {
            throw new SerializerException("Failed to serialize message", e);
        }
    }
}

package com.martianov.simplerpc.common.connection.impl;

import com.martianov.simplerpc.common.connection.ConnectionException;
import com.martianov.simplerpc.common.connection.IConnection;
import com.martianov.simplerpc.common.message.IMessage;

import java.io.*;

/**
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public abstract class AbsctractBasicConnection implements IConnection {
    private final Object sendMux = new Object();
    private final Object receiveMux = new Object();

    private ObjectOutputStream oos = null;
    private ObjectInputStream ois = null;

    protected abstract OutputStream outputStream() throws IOException;
    protected abstract InputStream inputStream() throws IOException;

    protected void processSendException(IOException e) throws ConnectionException {
    }
    protected void processReceiveException(IOException e) throws ConnectionException {
    }


    @Override
    public void send(IMessage message) throws ConnectionException {
        synchronized (sendMux) {
            try {
                if (null == oos) {
                    oos = new ObjectOutputStream(outputStream());
                }
                oos.writeObject(message);
            } catch (InvalidClassException | NotSerializableException e) {
                throw new ConnectionException("Failed to serialize message", e);
            } catch (IOException e) {
                processSendException(e);
                throw new ConnectionException("Failed to send message", e);
            }
        }
    }

    @Override
    public IMessage receive() throws ConnectionException {
        IMessage message = null;
        synchronized (receiveMux) {
            try {
                if (null == ois) {
                    ois = new ObjectInputStream(inputStream());
                }
                message = (IMessage) ois.readObject();
            } catch (IOException e) {
                processReceiveException(e);
                throw new ConnectionException("Failed to receive message", e);
            } catch (ClassNotFoundException e) {
                throw new ConnectionException("Failed to deserialize message", e);
            } catch (ClassCastException e) {
                throw new ConnectionException("Unexpected message class: " + message.getClass().getName(), e);
            }
        }
        return message;
    }
}

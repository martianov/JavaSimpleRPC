package com.martianov.simplerpc.common.connection.impl;

import com.martianov.simplerpc.common.connection.ConnectionClosedException;
import com.martianov.simplerpc.common.connection.ConnectionException;

import java.io.*;
import java.net.Socket;

/**
 * Simple RPC connection trough socket.
 *
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class BasicSocketConnection extends AbsctractBasicConnection {
    private final Socket socket;

    private final Object sendMux = new Object();
    private final Object receiveMux = new Object();

    private ObjectOutputStream oos = null;
    private ObjectInputStream ois = null;

    public BasicSocketConnection(Socket socket) {
        this.socket = socket;
    }

    /**
     * Open connection to specified host and port.
     *
     * @param host host
     * @paran port port
     * @throws ConnectionException failed to establish connection.
     * */
    public static BasicSocketConnection connect(String host, int port) throws ConnectionException {
        try {
            Socket socket = new Socket(host, port);
            return new BasicSocketConnection(socket);
        } catch (IOException e) {
            throw new ConnectionException("Failed to establish connection", e);
        }
    }

    @Override
    protected OutputStream outputStream() throws IOException {
        return socket.getOutputStream();
    }

    @Override
    protected InputStream inputStream() throws IOException {
        return socket.getInputStream();
    }

    @Override
    protected void processSendException(IOException e) throws ConnectionException {
        if (socket.isClosed() || !socket.isConnected()) {
            throw new ConnectionClosedException("Connection closed.");
        }
    }

    @Override
    protected void processReceiveException(IOException e) throws ConnectionException {
        if (socket.isClosed() || !socket.isConnected()) {
            throw new ConnectionClosedException("Connection closed.");
        }
    }

    /**
     * Close connection.
     *
     * @throws IOException error during closing of underlying socket.
     * */
    public void close() throws IOException {
        socket.close();
    }
}

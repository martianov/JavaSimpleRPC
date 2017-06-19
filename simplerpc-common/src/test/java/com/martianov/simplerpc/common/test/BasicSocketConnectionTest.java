package com.martianov.simplerpc.common.test;

import com.martianov.simplerpc.common.message.impl.basic.BasicMessageFactory;
import com.martianov.simplerpc.common.connection.impl.BasicSocketConnection;
import com.martianov.simplerpc.common.connection.IConnection;
import com.martianov.simplerpc.common.message.IMessageFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

/**
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class BasicSocketConnectionTest extends AbstractConnectionTest {
    private static final int SERVER_SOCKET_PORT = 8095;

    private IMessageFactory messageFactory;

    private ServerSocket serverSocket;
    private Socket socket;

    private BasicSocketConnection serverConnection;
    private BasicSocketConnection clientConnection;


    private void closeSocketQuietly(Socket socket) {
        try {
            socket.close();
        } catch (IOException e) {
            //skip;
        }
    }

    private void closeSocketQuietly(ServerSocket socket) {
        try {
            socket.close();
        } catch (IOException e) {
            //skip;
        }
    }

    @Override
    protected void doInit() throws Exception {
        messageFactory = new BasicMessageFactory();

        ServerSocket acceptSocket = new ServerSocket(SERVER_SOCKET_PORT);

        CompletableFuture<Socket> fut = new CompletableFuture<>();
        Thread acceptThread = new Thread(() -> {
            try {
                Socket serverSocket = acceptSocket.accept();
                fut.complete(serverSocket);
            } catch (IOException e) {
                fut.completeExceptionally(e);
            }

            closeSocketQuietly(acceptSocket);
        });
        acceptThread.start();

        Socket clientSocket = null;
        Socket serverSocket = null;
        try {
            clientSocket = new Socket("localhost", SERVER_SOCKET_PORT);
            serverSocket = fut.get();
        } catch (IOException e) {
            if (null != clientSocket) {
                closeSocketQuietly(clientSocket);
            }
            if (!fut.isDone()) {
                closeSocketQuietly(acceptSocket);
            }
        }

        clientConnection = new BasicSocketConnection(clientSocket);
        serverConnection = new BasicSocketConnection(serverSocket);
    }

    @Override
    protected void doShutdown() throws Exception {
        if (null != clientConnection) {
            clientConnection.close();
        }
        if (null != serverConnection) {
            serverConnection.close();
        }
    }

    @Override
    protected IConnection getClientConnection() {
        return clientConnection;
    }

    @Override
    protected IConnection getServerConnection() {
        return serverConnection;
    }

    @Override
    protected IMessageFactory getMessageFactory() {
        return messageFactory;
    }
}

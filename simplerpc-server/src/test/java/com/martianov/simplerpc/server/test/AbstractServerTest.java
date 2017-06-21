package com.martianov.simplerpc.server.test;

import com.martianov.simplerpc.common.connection.ConnectionException;
import com.martianov.simplerpc.common.connection.IConnection;
import com.martianov.simplerpc.common.connection.impl.BasicSocketConnection;
import com.martianov.simplerpc.common.message.impl.basic.BasicMessageFactory;
import com.martianov.simplerpc.common.message.IMessage;
import com.martianov.simplerpc.common.message.IMessageFactory;
import com.martianov.simplerpc.server.Server;
import com.martianov.simplerpc.server.ServerListener;
import com.martianov.simplerpc.server.services.IServiceProvider;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Base test class for server.
 *
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public abstract class AbstractServerTest implements ServerListener, IServiceProvider {
    private static final int TEST_PORT = 8094;
    private static final int SOCKET_TIMEOUT = 5000;

    private Server server;
    private IMessageFactory messageFactory;
    private final AtomicBoolean serverStarted = new AtomicBoolean(false);
    private final Map<String, Object> servicesMap = new HashMap<>();

    public void serverStarted() {
        serverStarted.set(true);
        synchronized (serverStarted) {
            serverStarted.notifyAll();
        }
    }

    @Before
    public void start() throws Exception {
        registerServices();

        messageFactory = createMessageFactory();

        server = new Server(TEST_PORT, this, this, messageFactory);

        server.start();

        while (!serverStarted.get()) {
            synchronized (serverStarted) {
                if (!serverStarted.get()) {
                    serverStarted.wait();
                }
            }
        }
    }

    @After
    public void stop() throws Exception {
        server.stop();
    }

    public void serverStopped(Exception error) {
        if (null != error) {
            Assert.fail("Server ended abnormally");
        }
    }

    protected Server server() {
        return server;
    }

    public Object serviceByName(String name) {
        return servicesMap.get(name);
    }

    protected void addService(String name, Object service) {
        servicesMap.put(name, service);
    }

    protected IMessageFactory createMessageFactory() {
        return new BasicMessageFactory();
    }

    protected IMessageFactory messageFactory() {
        return messageFactory;
    }


    protected abstract void registerServices();

    protected CompletableFuture<IMessage> sendAsync(IMessage message) {
        final CompletableFuture<IMessage> fut = new CompletableFuture<>();
        try {
            Socket socket = new Socket("localhost", TEST_PORT);
            socket.setSoTimeout(SOCKET_TIMEOUT);

            IConnection conn = new BasicSocketConnection(socket);
            conn.send(message);
            new Thread(() -> {
                try {
                    IMessage res = conn.receive();
                    fut.complete(res);
                } catch (ConnectionException e) {
                    fut.completeExceptionally(e);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    // skip;
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Failed to send message: " + e.getMessage());
        }
        return fut;
    }

    protected IMessage sendSync(IMessage message) throws IOException, ConnectionException {
        Socket socket = new Socket("localhost", TEST_PORT);
        socket.setSoTimeout(SOCKET_TIMEOUT);

        IConnection conn = new BasicSocketConnection(socket);
        conn.send(message);
        return conn.receive();
    }
}

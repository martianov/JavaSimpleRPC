package com.martianov.simplerpc.server.test;

import com.martianov.simplerpc.common.impl.basic.BasicMessageFactory;
import com.martianov.simplerpc.common.impl.basic.BasicSerializer;
import com.martianov.simplerpc.common.intf.IMessage;
import com.martianov.simplerpc.common.intf.IMessageFactory;
import com.martianov.simplerpc.common.intf.ISerializer;
import com.martianov.simplerpc.server.Server;
import com.martianov.simplerpc.server.ServerListener;
import com.martianov.simplerpc.server.services.IServiceProvider;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
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
    private ISerializer serializer;
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
        serializer = createSerializer();

        server = new Server(TEST_PORT, this, this, messageFactory, serializer);

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

    protected ISerializer createSerializer() {
        return new BasicSerializer();
    }

    protected IMessageFactory messageFactory() {
        return messageFactory;
    }

    protected ISerializer serializer() {
        return serializer;
    }


    protected abstract void registerServices();

    protected IMessage send(IMessage message) {
        IMessage res = null;
        try {
            Socket socket = new Socket("localhost", TEST_PORT);
            socket.setSoTimeout(SOCKET_TIMEOUT);

            serializer.write(socket.getOutputStream(), message);
            res = serializer.read(socket.getInputStream());
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Failed to send message: " + e.getMessage());
        }
        return res;
    }
}

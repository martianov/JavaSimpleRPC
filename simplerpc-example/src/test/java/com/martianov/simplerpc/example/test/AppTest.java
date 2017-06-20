package com.martianov.simplerpc.example.test;

import com.martianov.simplerpc.example.ExampleApplication;
import com.martianov.simplerpc.server.Server;
import com.martianov.simplerpc.server.ServerException;
import com.martianov.simplerpc.server.services.IServiceProvider;
import com.martianov.simplerpc.server.services.impl.ReflectionProvider;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class AppTest implements IServiceProvider {
    private final static int SERVER_PORT = 9000;

    private AtomicLong atomicLongService = new AtomicLong(0);
    private Server server;

    @Before
    public void startServer() throws ServerException {
        server = new Server(SERVER_PORT, this);
        server.start();
    }

    @After
    public void stopServer() {
        server.stop();
    }

    @Test
    public void testExampleApp() throws InterruptedException {
        int clientsCount = 1;
        int workersCount = 50;
        int callsCount = 100;

        ExampleApplication app = new ExampleApplication("localhost", SERVER_PORT, 1, 50, 100, new String[] {"atomicLong"}, new String[] {"incrementAndGet"});

        int rc = app.start();

        Assert.assertEquals(0, rc);
        Assert.assertEquals(clientsCount * workersCount * callsCount, atomicLongService.get());
    }

    @Override
    public Object serviceByName(String name) {
        if ("atomicLong".equals(name)) {
            return atomicLongService;
        }
        return null;
    }
}

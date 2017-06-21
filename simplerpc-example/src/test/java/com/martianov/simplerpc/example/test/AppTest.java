package com.martianov.simplerpc.example.test;

import com.martianov.simplerpc.example.ExampleApplication;
import com.martianov.simplerpc.server.Server;
import com.martianov.simplerpc.server.ServerException;
import com.martianov.simplerpc.server.services.IServiceProvider;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Starts server and check application execution.
 *
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class AppTest implements IServiceProvider {
    private final static int SERVER_PORT = 9000;

    private AtomicLong atomicLongService = new AtomicLong(0);
    private AtomicLong atomicLongService2 = new AtomicLong(0);
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
        int clientsCount = 5;
        int workersCount = 4;
        int callsCount = 1000;

        ExampleApplication app = new ExampleApplication("localhost", SERVER_PORT, clientsCount, workersCount, callsCount,
                new String[] {"atomicLong", "atomicLong2"}, new String[] {"incrementAndGet", "incrementAndGet"});

        int rc = app.start();

        Assert.assertEquals(0, rc);
        Assert.assertEquals(clientsCount * workersCount * callsCount / 2, atomicLongService.get());
        Assert.assertEquals(clientsCount * workersCount * callsCount / 2, atomicLongService2.get());
    }

    @Override
    public Object serviceByName(String name) {
        if ("atomicLong".equals(name)) {
            return atomicLongService;
        }
        if ("atomicLong2".equals(name)) {
            return atomicLongService2;
        }
        return null;
    }
}

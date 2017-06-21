package com.martianov.simplerpc.client.test;

import com.martianov.simplerpc.client.Client;
import com.martianov.simplerpc.client.IClient;
import com.martianov.simplerpc.client.RemoteExecutionException;
import com.martianov.simplerpc.client.test.stub.TestClient;
import com.martianov.simplerpc.common.connection.ConnectionException;
import com.martianov.simplerpc.common.connection.impl.BasicPipedConnection;
import com.martianov.simplerpc.common.message.impl.basic.BasicMessage;
import com.martianov.simplerpc.common.message.impl.basic.BasicMessageFactory;
import com.martianov.simplerpc.common.message.IMessage;
import com.martianov.simplerpc.common.message.IMessageFactory;
import com.martianov.simplerpc.common.message.IRequest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Abstract class for client implementation testing.
 *
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class AbstractClientTest {
    private Set<Exception> failures = new HashSet<>();

    private class ResponseAction implements Runnable {
        private final IMessage message;
        private final long delay;

        private ResponseAction(IMessage message, long delay) {
            this.message = message;
            this.delay = delay;
        }

        public long getDelay() {
            return delay;
        }

        @Override
        public void run() {
            try {
                conn.send(message);
            } catch (ConnectionException e) {
                if (!client.isClosed()) {
                    synchronized (failures) {
                        failures.add(e);
                    }
                }
            }
        }

        public void setCallID(long callID) {
            ((BasicMessage) message).setCallID(callID);
        }
    }

    private class ReaderThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    IRequest msg = (IRequest) conn.receive();

                    ResponseAction action = responses.get(msg.getServiceName());
                    action.setCallID(msg.getCallID());

                    executorService.schedule(action, action.getDelay(), TimeUnit.MILLISECONDS);
                } catch (ConnectionException e) {
                    if (!client.isClosed()) {
                        synchronized (failures) {
                            failures.add(e);
                        }
                    }
                    break;
                }
            }
        }
    }

    private TestClient client;
    private IMessageFactory messageFactory;
    private BasicPipedConnection conn;

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);
    private Map<String, ResponseAction> responses = new HashMap<>();

    private ReaderThread readerThread;
    private AtomicInteger callCounter = new AtomicInteger(1);

    @Before
    public void setUp() throws IOException {
        conn = BasicPipedConnection.create();

        messageFactory = createMessageFactory();

        client = new TestClient(messageFactory, conn.getOtherSide());

        registerResponses();

        readerThread = new ReaderThread();
        readerThread.start();
    }

    @After
    public void setDown() throws IOException, InterruptedException {
        client.close();

        executorService.shutdown();
        executorService.awaitTermination(5000, TimeUnit.MILLISECONDS);

        readerThread.join();

        for (Exception failure : failures) {
            failure.printStackTrace();
        }
        boolean fail = !failures.isEmpty();
        failures.clear();

        if (fail) {
            Assert.fail("There are errors during test");
        }
    }

    protected IMessageFactory createMessageFactory() {
        return new BasicMessageFactory();
    }

    /**
     * Override it to register responses before test execution using
     * #registerResponse(java.lang.String, com.martianov.simplerpc.common.message.IMessage, long) method.
     */
    protected void registerResponses() {
    }

    /**
     * Register response for particular service. All request for this service will be responded by registered message with
     * specified delay.
     *
     * @param serviceName service name
     * @param response    response message
     * @param delay       response delay in milliseconds.
     */
    protected void registerResponse(String serviceName, IMessage response, long delay) {
        responses.put(serviceName, new ResponseAction(response, delay));
    }

    /**
     * "Executes" remote call in separate thread.
     *
     * @param serviceName service name
     * @param methodName  method name
     * @param args        arguments
     * @return future for remote call result.
     */
    CompletableFuture<Object> remoteCall(final String serviceName, final String methodName, final Object[] args) {
        final CompletableFuture<Object> fut = new CompletableFuture<>();
        Thread thread = new Thread("Call #" + callCounter.getAndIncrement()) {
            @Override
            public void run() {
                Object res = null;
                try {
                    res = client.remoteCall(serviceName, methodName, args);
                } catch (ConnectionException | RemoteExecutionException e) {
                    fut.completeExceptionally(e);
                    return;
                }
                fut.complete(res);
            }
        };
        thread.start();

        return fut;
    }


    /**
     * Returns client instance.
     *
     * @return client instance.
     * */
    public IClient client() {
        return client;
    }


    /**
     * Returns message factory.
     *
     * @return message factory.
     *
     * */
    public IMessageFactory messageFactory() {
        return messageFactory;
    }
}

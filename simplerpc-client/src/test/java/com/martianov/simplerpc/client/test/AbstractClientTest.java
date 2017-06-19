package com.martianov.simplerpc.client.test;

import com.martianov.simplerpc.client.ClientException;
import com.martianov.simplerpc.client.IClient;
import com.martianov.simplerpc.client.test.stub.TestClient;
import com.martianov.simplerpc.common.connection.ConnectionException;
import com.martianov.simplerpc.common.connection.impl.BasicPipedConnection;
import com.martianov.simplerpc.common.message.impl.basic.BasicMessageFactory;
import com.martianov.simplerpc.common.message.IMessage;
import com.martianov.simplerpc.common.message.IMessageFactory;
import com.martianov.simplerpc.common.message.IRequest;
import org.junit.Before;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class AbstractClientTest {
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
            //TODO: fail test if exception
            try {
                conn.send(message);
            } catch (ConnectionException e) {
                e.printStackTrace();
            }
        }
    }

    private class ReaderThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    IRequest msg = (IRequest) conn.receive();

                    ResponseAction action = responses.get(msg.getServiceName());

                    executorService.schedule(action, action.getDelay(), TimeUnit.MILLISECONDS);
                } catch (ConnectionException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    private IClient client;
    private IMessageFactory messageFactory;
    private BasicPipedConnection conn;

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);
    private Map<String, ResponseAction> responses = new HashMap<>();

    @Before
    public void init() throws IOException {
        conn = BasicPipedConnection.create();

        messageFactory = createMessageFactory();

        client = new TestClient(messageFactory, conn.getOtherSide());

        registerResponses();

        Thread thread = new ReaderThread();
        thread.start();
    }

    protected IMessageFactory createMessageFactory() {
        return new BasicMessageFactory();
    }


    protected void registerResponses() {
    }

    protected void registerResponse(String serviceName, IMessage response, long delay) {
        responses.put(serviceName, new ResponseAction(response, delay));
    }

    public IClient client() {
        return client;
    }

    public IMessageFactory messageFactory() {
        return messageFactory;
    }

    private AtomicInteger callCounter = new AtomicInteger(1);
    CompletableFuture<Object> remoteCall(final String serviceName, final String methodName, final Object[] args) {
        final CompletableFuture<Object> fut = new CompletableFuture<>();
        Thread thread = new Thread("Call #" + callCounter.getAndIncrement()) {
            @Override
            public void run() {
                Object res  = null;
                try {
                    res = client.remoteCall(serviceName, methodName, args);
                } catch (ClientException e) {
                    e.printStackTrace();
                    fut.completeExceptionally(e);
                }
                fut.complete(res);
            }
        };
        thread.start();

        return fut;
    }
}

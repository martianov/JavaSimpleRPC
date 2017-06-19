package com.martianov.simplerpc.client;

import com.martianov.simplerpc.common.connection.ConnectionException;
import com.martianov.simplerpc.common.connection.IConnection;
import com.martianov.simplerpc.common.message.IError;
import com.martianov.simplerpc.common.message.IMessage;
import com.martianov.simplerpc.common.message.IMessageFactory;
import com.martianov.simplerpc.common.message.IResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * RPC Client base thread-safe implementation.
 * 
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public abstract class AbstractClient implements IClient {
    private static final Logger LOG = LogManager.getLogger(AbstractClient.class.getName());

    private static final int WAITER_MONITORS_COUNT = 20;

    private final IMessageFactory messageFactory;
    private final IConnection conn;

    private final AtomicLong genCallID = new AtomicLong(0);

    private final AtomicBoolean readerFlag = new AtomicBoolean(true);
    private final ConcurrentHashMap<Long, IMessage> results = new ConcurrentHashMap<>();
    private final AtomicInteger[] waiterMonitors = new AtomicInteger[WAITER_MONITORS_COUNT];

    public AbstractClient(IMessageFactory messageFactory, IConnection conn) {
        this.messageFactory = messageFactory;
        this.conn = conn;

        for (int i = 0; i < WAITER_MONITORS_COUNT; i++) {
            waiterMonitors[i] = new AtomicInteger(0);
        }
    }

    @Override
    public Object remoteCall(String serviceName, String methodName, Object[] arguments) throws ConnectionException, RemoteExecutionException{
        long callID = genCallID.incrementAndGet();
        IMessage req = messageFactory.createRequest(callID, serviceName, methodName, arguments);

        conn.send(req);
        LOG.info("Request sent: " + req);

        IMessage res = null;
        while (null == res) {
            if (readerFlag.compareAndSet(true, false)) {
                //I'm the reader

                while (null == res) {
                    IMessage msg;
                    try {
                        msg = conn.receive();
                    } catch (ConnectionException e) {
                        readerFlag.set(true);
                        throw e;
                    }

                    if (msg.getCallID() == callID) {
                        res = msg;
                        readerFlag.set(true);
                        wakeUpAny();
                    } else {
                        Object waiterMonitor = getWaiterMonitor(msg.getCallID());
                        synchronized (waiterMonitor) {
                            results.put(msg.getCallID(), msg);
                            waiterMonitor.notifyAll();
                        }
                    }
                }
            } else {
                //I'm waiter
                AtomicInteger waiterMonitor = getWaiterMonitor(callID);
                synchronized (waiterMonitor) {
                    if (results.containsKey(callID)) {
                        res = results.remove(callID);
                    } else {
                        waiterMonitor.getAndIncrement();
                        try {
                            waiterMonitor.wait();
                        } catch (InterruptedException e) {
                            //skip
                        }
                        waiterMonitor.decrementAndGet();
                        if (results.containsKey(callID)) {
                            res = results.remove(callID);
                        }
                    }
                }
            }
        }

        Object result = null;

        LOG.info("Response received: " + res);

        if (res instanceof IResult) {
            result = ((IResult) res).getResult();
        } else if (res instanceof IError) {
            throw new RemoteExecutionException(((IError) res).getMessage());
        }

        return result;
    }

    private AtomicInteger getWaiterMonitor(long callID) {
        return waiterMonitors[(int) callID % WAITER_MONITORS_COUNT];
    }

    private void wakeUpAny() {
        for (AtomicInteger waiterMonitor : waiterMonitors) {
            if (0 < waiterMonitor.get()) {
                synchronized (waiterMonitor) {
                    waiterMonitor.notify();
                }
                break;
            }
        }
    }

    @Override
    public void close() {
        try {
            conn.close();
        } catch (ConnectionException e) {
            LOG.error("Failed to close connection", e);
        }
    }
}

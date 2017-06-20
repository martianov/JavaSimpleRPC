package com.martianov.simplerpc.example;

import com.martianov.simplerpc.client.IClient;
import com.martianov.simplerpc.client.RemoteExecutionException;
import com.martianov.simplerpc.common.connection.ConnectionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class WorkerThread extends Thread {
    private static final Logger LOG = LogManager.getLogger(WorkerThread.class);

    private final IClient client;
    private final String serviceName;
    private final String methodName;
    private final long callCount;
    private final CyclicBarrier allReady;

    public WorkerThread(IClient client, String serviceName, String methodName, long callCount, CyclicBarrier allReady) {
        this.client = client;
        this.serviceName = serviceName;
        this.methodName = methodName;
        this.callCount = callCount;
        this.allReady = allReady;
    }

    @Override
    public void run() {
        try {
            allReady.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            LOG.error("Failed to await on cyclic barrier", e);
        }

        for (int i = 0; i < callCount; i++) {
            try {
                client.remoteCall(serviceName, methodName, new Object[0]);
            } catch (ConnectionException e) {
                LOG.error("Connection error", e);
                break;
            } catch (RemoteExecutionException e) {
                LOG.error("Remote execution error", e);
            }
        }
    }
}

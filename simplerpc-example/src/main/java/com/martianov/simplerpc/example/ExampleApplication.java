package com.martianov.simplerpc.example;

import com.martianov.simplerpc.client.Client;
import com.martianov.simplerpc.client.IClient;
import com.martianov.simplerpc.common.connection.ConnectionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;


/**
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class ExampleApplication {
    private static final Logger LOG = LogManager.getLogger(ExampleApplication.class.getName());

    private final String serverHost;
    private final int serverPort;

    private final int clientsCount;
    private final int workersCount;
    private final int callCount;
    private final String[] serviceNames;
    private final String[] methodNames;

    private IClient[] clients;
    private WorkerThread[] workers;


    public ExampleApplication(String serverHost, int serverPort, int clientsCount, int workersCount, int callCount, String[] serviceNames, String[] methodNames) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.clientsCount = clientsCount;
        this.workersCount = workersCount;
        this.callCount = callCount;
        this.serviceNames = serviceNames;
        this.methodNames = methodNames;
    }

    private void createClients() throws ConnectionException {
        for (int i = 0; i < clientsCount; i++) {
            clients[i] = Client.create(serverHost, serverPort);
        }
    }

    private void closeClients() {
        for (int i = 0; i < clientsCount; i++) {
            if (null != clients[i]) {
                clients[i].close();
            }
        }
    }

    private void startWorkers(CyclicBarrier allReady) {
        workers = new WorkerThread[clientsCount * workersCount];
        for (int i = 0; i < clientsCount; i++) {
            for (int j = 0; j < workersCount; j++) {
                int idx = i*workersCount + j;
                WorkerThread worker = new WorkerThread("Example App Thread #" + i + "-" + j, clients[i],
                        serviceNames[idx % serviceNames.length], methodNames[idx % methodNames.length], callCount, allReady);
                workers[idx] = worker;
                worker.start();
            }
        }
    }

    private void interruptWorkers() {
        for (WorkerThread workerThread: workers) {
            workerThread.interrupt();
        }
    }

    private void awaitWorkers() throws InterruptedException {
        for (WorkerThread workerThread: workers) {
            workerThread.join();
        }
    }

    public int start() throws InterruptedException {
        clients = new IClient[clientsCount];
        try {
            createClients();
        }  catch (ConnectionException e) {
            LOG.error("Failed to create client", e);
            closeClients();
            return 1;
        }

        CyclicBarrier allReady = new CyclicBarrier(clientsCount * workersCount + 1);
        startWorkers(allReady);


        try {
            allReady.await();
        } catch (BrokenBarrierException e) {
            LOG.error("Failed to await workers", e);
            interruptWorkers();
            closeClients();
            return 1;
        }

        LOG.info("Started");

        awaitWorkers();

        LOG.info("Finished");

        return 0;
    }
}

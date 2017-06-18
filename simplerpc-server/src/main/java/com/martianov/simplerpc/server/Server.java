package com.martianov.simplerpc.server;

import com.martianov.simplerpc.common.impl.basic.BasicMessageFactory;
import com.martianov.simplerpc.common.impl.basic.BasicSerializer;
import com.martianov.simplerpc.common.intf.IMessageFactory;
import com.martianov.simplerpc.common.intf.ISerializer;
import com.martianov.simplerpc.server.services.IServiceProvider;
import com.martianov.simplerpc.server.services.ServiceMethodCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Simple RPC Server.
 *
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class Server implements ClientThreadListener {
    private static final Logger LOG = LogManager.getLogger(Server.class.getName());

    private static final long ACCEPT_LOOP_END_TIMEOUT = 5000;

    private final int port;
    private final ServerListener listener;
    private final IServiceProvider serviceProvider;

    private final IMessageFactory messageFactory;
    private final ISerializer serializer;

    private final ServiceMethodCache cache;
    private ServerSocket serverSocket = null;

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    private final AtomicBoolean stopServer = new AtomicBoolean(true);
    private final AtomicBoolean acceptLoopEnded = new AtomicBoolean(true);

    ConcurrentMap<String, ClientThread> clientThreads = new ConcurrentHashMap<>();

    private Thread serveThread = null;

    public Server(int port, IServiceProvider serviceProvider) {
        this(port, null, serviceProvider, new BasicMessageFactory(), new BasicSerializer());
    }

    public Server(int port, ServerListener listener, IServiceProvider serviceProvider, IMessageFactory messageFactory, ISerializer serializer) {
        this.port = port;
        this.listener = listener;
        this.messageFactory = messageFactory;
        this.serializer = serializer;
        this.serviceProvider = serviceProvider;
        this.cache = new ServiceMethodCache(serviceProvider);
    }

    private void init() throws ServerException {
        try {
            serverSocket = new ServerSocket(port);
            stopServer.set(false);
        } catch (IOException e) {
            throw new ServerException("Failed to open server socket at port " + port, e);
        }
    }

    private void closeServerSocket() {
        if (null != serverSocket){
            try {
                serverSocket.close();
            } catch (IOException e1) {
                LOG.error("Failed to close server socket", e1);
            }
            serverSocket = null;
        }
    }

    private void acceptLoop() {
        Exception failure = null;

        try {
            acceptLoopEnded.set(false);

            while (!stopServer.get()) {
                try {
                    Socket socket = serverSocket.accept();

                    String threadName = "Client Thread[" + socket.getInetAddress() + ":" + socket.getPort() + "]";

                    ClientThread thread = new ClientThread(threadName, socket, serializer, executorService, cache, messageFactory, this);
                    clientThreads.put(threadName, thread);

                    thread.start();
                } catch (IOException e) {
                    if (!stopServer.get()) {
                        LOG.error("Failed to accept socket connection. Stopping server..", e);
                        closeServerSocket();
                        failure = e;
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Error inside accept loop", e);
            failure = e;
        } finally {
            acceptLoopEnded.set(true);
            synchronized (acceptLoopEnded) {
                acceptLoopEnded.notifyAll();
            }

            if (null != failure) {
                stop(failure);
            }
        }
    }

    public void serve() throws ServerException {
        init();

        if (listener != null) {
            listener.serverStarted();
        }

        acceptLoop();
    }

    public void start() throws ServerException {
        init();

        final Server server = this;
        serveThread = new Thread(Utils.genThreadName("Serve Thread")) {
            @Override
            public void run() {
                server.acceptLoop();
            }
        };

        serveThread.start();

        if (listener != null) {
            listener.serverStarted();
        }
    }

    public void stop() {
        stop(null);
    }

    private void waitForAcceptLoopEnded() {
        while (!acceptLoopEnded.get()) {
            synchronized (acceptLoopEnded) {
                if (!acceptLoopEnded.get()) {
                    try {
                        acceptLoopEnded.wait();
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        }
    }

    private void stop(Exception error) {
        if (!stopServer.get()) {
            stopServer.set(true);

            if (!acceptLoopEnded.get()) {
                closeServerSocket();
            }


            for (ClientThread thread: clientThreads.values()) {
                System.out.println("stopping " + thread.getName() );
                thread.stopThread();
            }
            clientThreads.clear();

            waitForAcceptLoopEnded();

            if (null != listener) {
                listener.serverStopped(error);
            }
        }
    }

    @Override
    public void clientThreadStopped(String threadName) {
        clientThreads.remove(threadName);
    }
}

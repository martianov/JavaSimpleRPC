package com.martianov.simplerpc.server;

import com.martianov.simplerpc.common.connection.impl.BasicSocketConnection;
import com.martianov.simplerpc.common.message.impl.basic.BasicMessageFactory;
import com.martianov.simplerpc.common.message.IMessageFactory;
import com.martianov.simplerpc.server.services.IServiceProvider;
import com.martianov.simplerpc.server.services.ServiceMethodCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Simple RPC Server.
 *
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class Server implements ClientThreadListener {
    private static final Logger LOG = LogManager.getLogger(Server.class.getName());

    private final int port;
    private final ServerListener listener;

    private final IServiceProvider serviceProvider;
    private final ServiceMethodCache cache;

    private final IMessageFactory messageFactory;

    private ServerSocket serverSocket = null;

    private ExecutorService executorService = Executors.newFixedThreadPool(10, new ThreadFactory() {
        int threadNum = 0;
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "Server Worker Thread #" + threadNum++);
        }
    });

    private final AtomicBoolean stopServer = new AtomicBoolean(true);
    private final AtomicBoolean acceptLoopEnded = new AtomicBoolean(true);

    ConcurrentMap<String, ClientThread> clientThreads = new ConcurrentHashMap<>();

    private Thread serveThread = null;

    public Server(int port, IServiceProvider serviceProvider) {
        this(port, null, serviceProvider, new BasicMessageFactory());
    }

    public Server(int port, ServerListener listener, IServiceProvider serviceProvider, IMessageFactory messageFactory) {
        this.port = port;
        this.listener = listener;
        this.messageFactory = messageFactory;
        this.serviceProvider = serviceProvider;
        this.cache = new ServiceMethodCache(serviceProvider);
    }

    /**
     * Initialize server.
     *
     * @throws ServerException error during server initialization.
     * */
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



                    ClientThread thread = new ClientThread(threadName, socket, new BasicSocketConnection(socket), executorService, cache, messageFactory, this);
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

    /**
     * Initialize server and run accept loop in current thread.
     * */
    public void serve() throws ServerException {
        init();

        if (listener != null) {
            listener.serverStarted();
        }

        acceptLoop();
    }

    /**
     * Initialize server and run accept loop in separate thread.
     * */
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

    /**
     * Stop server. Method is blocking.
     * */
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

package com.martianov.simplerpc.server;

import com.martianov.simplerpc.common.connection.ConnectionClosedException;
import com.martianov.simplerpc.common.connection.ConnectionException;
import com.martianov.simplerpc.common.connection.IConnection;
import com.martianov.simplerpc.common.message.IMessage;
import com.martianov.simplerpc.common.message.IMessageFactory;
import com.martianov.simplerpc.server.services.ServiceMethodCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class ClientThread extends Thread {
    private static final Logger LOG = LogManager.getLogger(ClientThread.class.getName());

    private final AtomicBoolean stopped = new AtomicBoolean(false);
    private final Socket socket;
    private final IConnection conn;
    private final ExecutorService executorService;
    private final ServiceMethodCache cache;
    private final IMessageFactory factory;
    private final ClientThreadListener listener;

    public ClientThread(String name, Socket socket, IConnection conn, ExecutorService executorService,
                        ServiceMethodCache cache, IMessageFactory factory, ClientThreadListener listener) {
        super(name);
        this.socket = socket;
        this.conn = conn;
        this.executorService = executorService;
        this.cache = cache;
        this.factory = factory;
        this.listener = listener;
    }

    @Override
    public void run() {
        while (!stopped.get()) {
            try {
                IMessage message = conn.receive();
                LOG.debug("Message received: " + message);

//                System.out.println("bb1");
                executorService.execute(new MessageHandler(message, socket, cache, factory, conn));
//                System.out.println("cc1");
            } catch (ConnectionClosedException e) {
                LOG.debug("Client disconnected.");
                listener.clientThreadStopped(getName());
                break;
            } catch (ConnectionException e) {
                if (!stopped.get()) {
                    LOG.error("Failed to read message", e);
                }
            }
        }
    }

    public void stopThread() {
        if (stopped.compareAndSet(false, true)) {
            try {
                socket.close();
            } catch (IOException e) {
                //skip
            }
        }
    }
}

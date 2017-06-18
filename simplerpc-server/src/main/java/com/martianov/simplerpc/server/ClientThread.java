package com.martianov.simplerpc.server;

import com.martianov.simplerpc.common.intf.IMessage;
import com.martianov.simplerpc.common.intf.IMessageFactory;
import com.martianov.simplerpc.common.intf.ISerializer;
import com.martianov.simplerpc.common.intf.SerializerException;
import com.martianov.simplerpc.server.services.ServiceMethodCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.EOFException;
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
    private final ISerializer serializer;
    private final ExecutorService executorService;
    private final ServiceMethodCache cache;
    private final IMessageFactory factory;
    private final ClientThreadListener listener;

    public ClientThread(String name, Socket socket, ISerializer serializer, ExecutorService executorService,
                        ServiceMethodCache cache, IMessageFactory factory, ClientThreadListener listener) {
        super(name);
        this.socket = socket;
        this.serializer = serializer;
        this.executorService = executorService;
        this.cache = cache;
        this.factory = factory;
        this.listener = listener;
    }

    @Override
    public void run() {
        while (!stopped.get()) {
            try {
                IMessage message = serializer.read(socket.getInputStream());
                LOG.info("Message received: " + message);

                executorService.execute(new MessageHandler(message, socket, cache, factory, serializer));
            } catch (EOFException e) {
                LOG.info("Client disconnected.");
                listener.clientThreadStopped(getName());
                break;
            } catch (IOException | SerializerException e) {
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

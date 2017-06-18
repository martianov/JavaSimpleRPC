package com.martianov.simplerpc.server;

import com.martianov.simplerpc.common.intf.*;
import com.martianov.simplerpc.server.services.ServiceException;
import com.martianov.simplerpc.server.services.ServiceMethodCache;
import com.martianov.simplerpc.server.services.ServiceMethodPair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;

/**
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class MessageHandler implements Runnable {
    private static Logger LOG = LogManager.getLogger(MessageHandler.class.getName());

    private final IMessage message;
    private final Socket socket;
    private final ServiceMethodCache cache;
    private final IMessageFactory messageFactory;
    private final ISerializer serializer;

    public MessageHandler(IMessage message, Socket socket, ServiceMethodCache cache, IMessageFactory messageFactory, ISerializer serializer) {
        this.message = message;
        this.socket = socket;
        this.cache = cache;
        this.messageFactory = messageFactory;
        this.serializer = serializer;
    }

    private String logPrefix() {
        return "Client=" + socket.getInetAddress() + ":" + socket.getPort()
                + ", callID=" + message.getCallID() + ": ";
    }


    @Override
    public void run() {
        if (!(message instanceof IRequest)) {
            LOG.error(logPrefix() + "Unexpected message type: " + message.getClass().getName());
            return;
        }

        IRequest req = (IRequest) message;

        IMessage res;

        try {
            ServiceMethodPair pair = cache.get(req.getServiceName(), req.getMethodName());
            Object result = pair.invoke(req.getArguments());

            if (pair.isVoid()) {
                res = messageFactory.createVoidResult(message.getCallID());
            } else {
                res = messageFactory.createResult(message.getCallID(), result);
            }
        } catch (ServiceException e) {
            res = messageFactory.createError(message.getCallID(), e.getMessage());
        }

        try {
            serializer.write(socket.getOutputStream(), res);

            LOG.info(logPrefix() + "Response sent: " + res);
        } catch (IOException | SerializerException e) {
            LOG.error(logPrefix() + "Failed to send response", e);
        }
    }
}

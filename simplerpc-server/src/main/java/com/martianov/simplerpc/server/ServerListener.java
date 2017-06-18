package com.martianov.simplerpc.server;

/**
 * Server's events listener.
 *
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public interface ServerListener {
    /**
     * Called on server start.
     * */
    void serverStarted();

    /**
     * Called on server stop. If server is stopped abnormally - error contains corresponding exception;
     * otherwise error is null.
     *
     * @param error If server is stopped abnormally - error contains corresponding exception; null - otherwise.
     * */
    void serverStopped(Exception error);
}

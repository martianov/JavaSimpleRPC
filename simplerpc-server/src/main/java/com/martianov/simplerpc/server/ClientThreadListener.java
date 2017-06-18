package com.martianov.simplerpc.server;

/**
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public interface ClientThreadListener {
    void clientThreadStopped(String threadName);
}

package com.martianov.simplerpc.server.services;

/**
 * Provides services.
 *
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public interface IServiceProvider {
    /**
     * Returns service object by service name.
     * */
    Object serviceByName(String name);
}

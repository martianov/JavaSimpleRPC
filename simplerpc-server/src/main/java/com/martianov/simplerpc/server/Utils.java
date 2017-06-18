package com.martianov.simplerpc.server;

/**
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class Utils {
    public static final String THREAD_NAME_PREFIX = "SimpleRPCServer - ";

    public static String genThreadName(String seed) {
        return THREAD_NAME_PREFIX + seed;
    }
}

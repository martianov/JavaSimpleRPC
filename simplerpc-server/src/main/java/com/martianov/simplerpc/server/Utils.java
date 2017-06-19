package com.martianov.simplerpc.server;

/**
 * Utility class.
 *
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class Utils {
    public static final String THREAD_NAME_PREFIX = "SimpleRPCServer - ";

    /**
     * Creates thread name for utility thread using common prefix.
     *
     * @return thread name.
     * */
    public static String genThreadName(String seed) {
        return THREAD_NAME_PREFIX + seed;
    }
}

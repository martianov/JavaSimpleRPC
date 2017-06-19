package com.martianov.simplerpc.client;

/**
 * Simple RPC Client.
 *
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public interface IClient {
    /**
     * Remote method call. Blocking operation.
     *
     * @param serviceName service name
     * @param methodName method name
     * @param arguments arguments
     *
     * @return method execution result
     * @throws ClientException remote method call exception.
     * */
    Object remoteCall(String serviceName, String methodName, Object[] arguments) throws ClientException;

    /**
     * Close client and underlying connection.
     * */
    void close();
}

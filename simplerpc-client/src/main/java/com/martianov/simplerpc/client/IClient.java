package com.martianov.simplerpc.client;

import com.martianov.simplerpc.common.connection.ConnectionException;

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
     * @throws RemoteExecutionException exception during execution on remote side.
     * @throws ConnectionException connection exception
     * */
    Object remoteCall(String serviceName, String methodName, Object[] arguments) throws ConnectionException, RemoteExecutionException;

    /**
     * Close client and underlying connection.
     * */
    void close();
}

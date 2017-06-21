package com.martianov.simplerpc.server.services.impl;

import com.martianov.simplerpc.server.services.IServiceProvider;
import com.martianov.simplerpc.server.services.ServiceProviderException;

import java.util.HashMap;
import java.util.Map;

/**
 * Creates services using class name.
 *
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class ReflectionProvider implements IServiceProvider {
    private Map<String, Object> servicesMap = new HashMap<>();

    public void register(String name, String serviceClassName) throws ServiceProviderException {
        if (servicesMap.containsKey(name)) {
            throw new ServiceProviderException("Duplicate service name \"" + serviceClassName + "\"");
        }

        try {
            Class serviceClass = this.getClass().getClassLoader().loadClass(serviceClassName);
            Object service = serviceClass.newInstance();
            servicesMap.put(name, service);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new ServiceProviderException("Failed to instantiate service " + name, e);
        }
    }

    @Override
    public Object serviceByName(String name) {
        return servicesMap.get(name);
    }
}

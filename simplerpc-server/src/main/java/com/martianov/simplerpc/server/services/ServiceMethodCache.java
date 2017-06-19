package com.martianov.simplerpc.server.services;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Caches lookups for services methods. Implies that services methods aren't overloaded.
 *
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public class ServiceMethodCache {
    private static class ServiceEntry {
        private final Object service;
        private final ConcurrentHashMap<String, ServiceMethodPair> methodsMap = new ConcurrentHashMap<>();

        public ServiceEntry(Object service) {
            this.service = service;
        }

        public ServiceMethodPair get(String methodName) {
            ServiceMethodPair pair = methodsMap.get(methodName);
            if (null == pair) {
                for (Method method : service.getClass().getMethods()) {
                    if (method.getName().equals(methodName)) {
                        pair = new ServiceMethodPair(service, method);
                        methodsMap.put(methodName, pair);
                    }
                }
            }
            return pair;
        }
    }

    private ConcurrentHashMap<String, ServiceEntry> servicesMap = new ConcurrentHashMap<>();
    private final IServiceProvider provider;

    public ServiceMethodCache(IServiceProvider provider) {
        this.provider = provider;
    }

    public ServiceMethodPair get(String serviceName, String methodName) throws ServiceException {
        ServiceEntry entry = servicesMap.get(serviceName);
        if (null == entry) {
            Object service = provider.serviceByName(serviceName);
            if (null == service) {
                throw new ServiceException("Service not found, serviceName=" + serviceName);
            }
            entry = new ServiceEntry(service);
            servicesMap.put(serviceName, entry);
        }

        ServiceMethodPair pair = entry.get(methodName);

        if (null == pair) {
            throw new ServiceException("Method not found, serviceName=" + serviceName + ", methodName=" + methodName);
        }

        return pair;
    }
}

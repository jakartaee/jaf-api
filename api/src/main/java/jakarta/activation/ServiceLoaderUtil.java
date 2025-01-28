/*
 * Copyright (c) 2021, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.activation;

import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Shared ServiceLoader/FactoryFinder Utils. JAF and MAIL use the same loading
 * logic of thread context class loader, calling class loader, and finally the
 * system class loader.
 *
 * @author Miroslav.Kos@oracle.com
 */
class ServiceLoaderUtil {

    static <P, T extends Exception> P firstByServiceLoader(Class<P> spiClass,
                                                           ClassLoader loader,
                                                           Logger logger,
                                                           ExceptionHandler<T> handler) throws T {
        logger.log(Level.FINE, "Using java.util.ServiceLoader to find {0}", spiClass.getName());
        // service discovery
        try {
            ServiceLoader<P> serviceLoader = ServiceLoader.load(spiClass, loader);

            for (P impl : serviceLoader) {
                logger.log(Level.FINE, "ServiceProvider loading Facility used; returning object [{0}]", impl.getClass().getName());

                return impl;
            }
        } catch (Throwable t) {
            throw handler.createException(t, "Error while searching for service [" + spiClass.getName() + "]");
        }
        return null;
    }

    @SuppressWarnings({"unchecked"})
    static <P> Class<P> nullSafeLoadClass(String className, ClassLoader classLoader) throws ClassNotFoundException {
        if (classLoader == null) { //Match behavior of ServiceLoader
            classLoader = ClassLoader.getSystemClassLoader();
        }
        return (Class<P>) Class.forName(className, false, classLoader);
    }

    // Returns instance of required class. It checks package access (security).
    static <P, T extends Exception> P newInstance(String className,
                                                  Class<P> service, ClassLoader classLoader,
                                                  final ExceptionHandler<T> handler) throws T {
        try {
            Class<P> cls = safeLoadClass(className, classLoader);
            return service.cast(cls.getConstructor().newInstance());
        } catch (ClassNotFoundException x) {
            throw handler.createException(x, "Provider " + className + " not found");
        } catch (Exception x) {
            throw handler.createException(x, "Provider " + className + " could not be instantiated: " + x);
        }
    }

    @SuppressWarnings({"unchecked"})
    static <P> Class<P> safeLoadClass(String className,
                                      ClassLoader classLoader) throws ClassNotFoundException {
        return nullSafeLoadClass(className, classLoader);
    }

    static abstract class ExceptionHandler<T extends Exception> {

        public abstract T createException(Throwable throwable, String message);

    }
}

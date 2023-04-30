/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.activation;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;


class FactoryFinder {

    private static final Logger logger = Logger.getLogger("jakarta.activation");

    private static final ServiceLoaderUtil.ExceptionHandler<RuntimeException> EXCEPTION_HANDLER =
            new ServiceLoaderUtil.ExceptionHandler<RuntimeException>() {
                @Override
                public RuntimeException createException(Throwable throwable, String message) {
                    return new RuntimeException(message, throwable);
                }
            };

    /**
     * Finds the implementation {@code Class} object for the given
     * factory type.  If it fails and {@code tryFallback} is {@code true}
     * finds the {@code Class} object for the given default class name.
     * The arguments supplied must be used in order
     * Note the default class name may be needed even if fallback
     * is not to be attempted in order to check if requested type is fallback.
     * <P>
     * This method is package private so that this code can be shared.
     *
     * @param factoryClass     factory abstract class or interface to be found
     * @param defaultClassName the implementation class name, which is
     *                         to be used only if nothing else
     *                         is found; {@code null} to indicate
     *                         that there is no default class name
     * @param tryFallback      whether to try the default class as a
     *                         fallback
     * @return the {@code Class} object of the specified message factory;
     * may not be {@code null}
     * @throws RuntimeException if there is no factory found
     */
    static <T> T find(Class<T> factoryClass,
                      String defaultClassName,
                      boolean tryFallback) throws RuntimeException {

        ClassLoader tccl = ServiceLoaderUtil.contextClassLoader(EXCEPTION_HANDLER);
        String factoryId = factoryClass.getName();

        // Use the system property first
        String className = fromSystemProperty(factoryId);
        if (className != null) {
            T result = newInstance(className, defaultClassName, tccl);
            if (result != null) {
                return result;
            }
            // try api loader
            result = newInstance(className, defaultClassName, FactoryFinder.class.getClassLoader());
            if (result != null) {
                return result;
            }
        }

        // standard services: java.util.ServiceLoader
        T factory = ServiceLoaderUtil.firstByServiceLoader(
                factoryClass,
                logger,
                EXCEPTION_HANDLER);
        if (factory != null) {
            return factory;
        }

        // handling Glassfish/OSGi (platform specific default)
        if (isOsgi()) {
            T result = lookupUsingOSGiServiceLoader(factoryId);
            if (result != null) {
                return result;
            }
        }

        // If not found and fallback should not be tried, throw RuntimeException.
        if (!tryFallback) {
            throw new RuntimeException(
                    "Provider for " + factoryId + " cannot be found", null);
        }

        // We didn't find the class through the usual means so try the default
        // (built in) factory if specified.
        if (defaultClassName == null) {
            throw new RuntimeException(
                    "Provider for " + factoryId + " cannot be found", null);
        }
        return newInstance(defaultClassName, defaultClassName, tccl);
    }

    private static <T> T newInstance(String className, String defaultClassName, ClassLoader tccl) throws RuntimeException {
        return ServiceLoaderUtil.newInstance(
                className,
                defaultClassName,
                tccl,
                EXCEPTION_HANDLER);
    }

    private static String fromSystemProperty(String factoryId) {
        return getSystemProperty(factoryId);
    }

    private static String getSystemProperty(final String property) {
        logger.log(Level.FINE, "Checking system property {0}", property);
        String value = AccessController.doPrivileged(new PrivilegedAction<String>() {
            @Override
            public String run() {
                return System.getProperty(property);
            }
        });
        logFound(value);
        return value;
    }

    private static void logFound(String value) {
        if (value != null) {
            logger.log(Level.FINE, "  found {0}", value);
        } else {
            logger.log(Level.FINE, "  not found");
        }
    }

    private static final String OSGI_SERVICE_LOADER_CLASS_NAME = "org.glassfish.hk2.osgiresourcelocator.ServiceLoader";

    private static boolean isOsgi() {
        try {
            Class.forName(OSGI_SERVICE_LOADER_CLASS_NAME);
            return true;
        } catch (ClassNotFoundException ignored) {
        }
        return false;
    }

    @SuppressWarnings({"unchecked"})
    private static <T> T lookupUsingOSGiServiceLoader(String factoryId) {
        try {
            // Use reflection to avoid having any dependency on HK2 ServiceLoader class
            Class<?> serviceClass = Class.forName(factoryId);
            Class<?>[] args = new Class<?>[]{serviceClass};
            Class<?> target = Class.forName(OSGI_SERVICE_LOADER_CLASS_NAME);
            Method m = target.getMethod("lookupProviderInstances", Class.class);
            Iterator<?> iter = ((Iterable<?>) m.invoke(null, (Object[]) args)).iterator();
            return iter.hasNext() ? (T) iter.next() : null;
        } catch (Exception ignored) {
            // log and continue
            return null;
        }
    }

}

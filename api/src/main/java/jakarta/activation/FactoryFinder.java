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

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;


class FactoryFinder {

    private static final Logger logger = Logger.getLogger("jakarta.activation");

    private static final ServiceLoaderUtil.ExceptionHandler<RuntimeException> EXCEPTION_HANDLER =
            new ServiceLoaderUtil.ExceptionHandler<RuntimeException>() {
                @Override
                public RuntimeException createException(Throwable throwable, String message) {
                    return new IllegalStateException(message, throwable);
                }
            };

    /**
     * Finds the implementation {@code Class} object for the given
     * factory type.
     * <P>
     * This method is package private so that this code can be shared.
     *
     * @param factoryClass     factory abstract class or interface to be found
     * @return the {@code Class} object of the specified message factory;
     * may not be {@code null}
     * @throws IllegalStateException if there is no factory found
     */
    static <T> T find(Class<T> factoryClass) throws RuntimeException {
        for (ClassLoader l : getClassLoaders(
                                Thread.class,
                                FactoryFinder.class,
                                System.class)) {
            T f = find(factoryClass, l);
            if (f != null) {
                return f;
            }
        }

        throw EXCEPTION_HANDLER.createException((Throwable) null,
                "Provider for " + factoryClass.getName() + " cannot be found");
    }

    static <T> T find(Class<T> factoryClass, ClassLoader loader) throws RuntimeException {
        // Use the system property first
        String className = fromSystemProperty(factoryClass.getName());
        if (className != null) {
            T result = newInstance(className, factoryClass, loader);
            if (result != null) {
                return result;
            }
        }

        // standard services: java.util.ServiceLoader
        T factory = ServiceLoaderUtil.firstByServiceLoader(
                factoryClass,
                loader,
                logger,
                EXCEPTION_HANDLER);
        if (factory != null) {
            return factory;
        }

        // handling Glassfish/OSGi (platform specific default)
        T result = lookupUsingHk2ServiceLoader(factoryClass, loader);
        if (result != null) {
            return result;
        }

        return null;
    }

    private static <T> T newInstance(String className,
                            Class<? extends T> service, ClassLoader loader)
                                throws RuntimeException {
        return ServiceLoaderUtil.newInstance(
                className,
                service,
                loader,
                EXCEPTION_HANDLER);
    }

    private static String fromSystemProperty(String factoryId) {
        String systemProp = getSystemProperty(factoryId);
        return systemProp;
    }

    private static String getSystemProperty(final String property) {
        logger.log(Level.FINE, "Checking system property {0}", property);
        String value = System.getProperty(property);
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

    private static Class<?>[] getHk2ServiceLoaderTargets(Class<?> factoryClass) {
        ClassLoader[] loaders = getClassLoaders(Thread.class, factoryClass, System.class);

        Class<?>[] classes = new Class<?>[loaders.length];
        int w = 0;
        for (ClassLoader loader : loaders) {
            if (loader != null) {
                try {
                    classes[w++] = Class.forName("org.glassfish.hk2.osgiresourcelocator.ServiceLoader", false, loader);
                } catch (Exception | LinkageError ignored) {
                }  //GlassFish class loaders can throw undocumented exceptions
            }
        }

        if (classes.length != w) {
           classes = Arrays.copyOf(classes, w);
        }
        return classes;
    }

    @SuppressWarnings({"unchecked"})
    private static <T> T lookupUsingHk2ServiceLoader(Class<T> factoryClass, ClassLoader loader) {
        for (Class<?> target : getHk2ServiceLoaderTargets(factoryClass)) {
            try {
                // Use reflection to avoid having any dependency on HK2 ServiceLoader class
                Class<?> serviceClass = Class.forName(factoryClass.getName(), false, loader);
                Class<?>[] args = new Class<?>[]{serviceClass};
                Method m = target.getMethod("lookupProviderInstances", Class.class);
                Iterable<?> iterable = ((Iterable<?>) m.invoke(null, (Object[]) args));
                if (iterable != null) {
                    Iterator<?> iter = iterable.iterator();
                    if (iter.hasNext()) {
                        return factoryClass.cast(iter.next()); //Verify classloader.
                    }
                }
            } catch (Exception ignored) {
                // log and continue
            }
        }
        return null;
    }

    private static ClassLoader[] getClassLoaders(final Class<?>... classes) {
        ClassLoader[] loaders = new ClassLoader[classes.length];
        int w = 0;
        for (Class<?> k : classes) {
            ClassLoader cl = null;
            if (k == Thread.class) {
                try {
                    cl = Thread.currentThread().getContextClassLoader();
                } catch (SecurityException ex) {
                }
            } else if (k == System.class) {
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (SecurityException ex) {
                }
            } else {
                try {
                    cl = k.getClassLoader();
                } catch (SecurityException ex) {
                }
            }

            if (cl != null) {
               loaders[w++] = cl;
            }
        }

        if (loaders.length != w) {
            loaders = Arrays.copyOf(loaders, w);
        }
        return loaders;
    }
}

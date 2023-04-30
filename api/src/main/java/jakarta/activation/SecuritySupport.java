/*
 * Copyright (c) 1997, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.activation;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Security related methods that only work on J2SE 1.2 and newer.
 */
class SecuritySupport {

    private SecuritySupport() {
        // private constructor, can't create an instance
    }

    public static ClassLoader getContextClassLoader() {
        return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            public ClassLoader run() {
                ClassLoader cl = null;
                try {
                    cl = Thread.currentThread().getContextClassLoader();
                } catch (SecurityException ex) {
                }
                return cl;
            }
        });
    }

    public static InputStream getResourceAsStream(final Class<?> c,
                                                  final String name) throws IOException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<InputStream>() {
                public InputStream run() throws IOException {
                    return c.getResourceAsStream(name);
                }
            });
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getException();
        }
    }

    public static URL[] getResources(final ClassLoader cl, final String name) {
        return AccessController.doPrivileged(new PrivilegedAction<URL[]>() {
            public URL[] run() {
                URL[] ret = null;
                try {
                    List<URL> v = new ArrayList<>();
                    Enumeration<URL> e = cl.getResources(name);
                    while (e != null && e.hasMoreElements()) {
                        URL url = e.nextElement();
                        if (url != null)
                            v.add(url);
                    }
                    if (v.size() > 0) {
                        ret = new URL[v.size()];
                        ret = v.toArray(ret);
                    }
                } catch (IOException | SecurityException ioex) {
                }
                return ret;
            }
        });
    }

    public static URL[] getSystemResources(final String name) {
        return AccessController.doPrivileged(new PrivilegedAction<URL[]>() {
            public URL[] run() {
                URL[] ret = null;
                try {
                    List<URL> v = new ArrayList<>();
                    Enumeration<URL> e = ClassLoader.getSystemResources(name);
                    while (e != null && e.hasMoreElements()) {
                        URL url = e.nextElement();
                        if (url != null)
                            v.add(url);
                    }
                    if (v.size() > 0) {
                        ret = new URL[v.size()];
                        ret = v.toArray(ret);
                    }
                } catch (IOException | SecurityException ioex) {
                }
                return ret;
            }
        });
    }

    public static InputStream openStream(final URL url) throws IOException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<InputStream>() {
                public InputStream run() throws IOException {
                    return url.openStream();
                }
            });
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getException();
        }
    }
}

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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Logging related methods.
 */
class LogSupport {
    private static boolean debug = false;
    private static Logger logger;
    private static final Level level = Level.FINE;

    static {
        try {
            debug = Boolean.getBoolean("jakarta.activation.debug") ||
                    Boolean.getBoolean("javax.activation.debug");
        } catch (Throwable t) {
            // ignore any errors
        }
        logger = Logger.getLogger("jakarta.activation");
    }

    /**
     * Constructor.
     */
    private LogSupport() {
        // private constructor, can't create instances
    }

    public static void log(String msg) {
        if (debug)
            System.out.println(msg);
        logger.log(level, msg);
    }

    public static void log(String msg, Throwable t) {
        if (debug)
            System.out.println(msg + "; Exception: " + t);
        logger.log(level, msg, t);
    }

    public static boolean isLoggable() {
        return debug || logger.isLoggable(level);
    }
}

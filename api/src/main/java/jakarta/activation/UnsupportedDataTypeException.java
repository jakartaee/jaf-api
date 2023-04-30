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

/**
 * Signals that the requested operation does not support the
 * requested data type.
 *
 * @see jakarta.activation.DataHandler
 */

public class UnsupportedDataTypeException extends IOException {

    private static final long serialVersionUID = -3584600599376858820L;

    /**
     * Constructs an UnsupportedDataTypeException with no detail
     * message.
     */
    public UnsupportedDataTypeException() {
        super();
    }

    /**
     * Constructs an UnsupportedDataTypeException with the specified
     * message.
     *
     * @param s The detail message.
     */
    public UnsupportedDataTypeException(String s) {
        super(s);
    }
}

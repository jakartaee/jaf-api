/*
 * Copyright (c) 1997, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package	com.sun.activation.registries;

/**
 *	A class to encapsulate Mailcap parsing related exceptions
 */
public class MailcapParseException extends Exception {

    private static final long serialVersionUID = -1445946122972156790L;

    public MailcapParseException() {
	super();
    }

    public MailcapParseException(String inInfo) {
	super(inInfo);
    }
}

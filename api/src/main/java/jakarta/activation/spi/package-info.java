/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/**
 * <p>Provides interfaces which implementations will be used as service providers for other services
 * that used by Jakarta Activation.</p>
 * <p>Implementation of Jakarta Activation must implement interfaces declared in this package.
 * Jakarta Activation uses {@link java.util.ServiceLoader} class to discover
 * and load implementations of the interfaces from this package using standard Java SPI mechanism.</p>
 */
package jakarta.activation.spi;
/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/**
 * Jakarta Activation API
 */
module jakarta.activation {
    uses jakarta.activation.spi.MailcapRegistryProvider;
    uses jakarta.activation.spi.MimeTypeRegistryProvider;
    exports jakarta.activation;
    exports jakarta.activation.spi;
    requires java.logging;
    //reflective call to java.beans.Beans.instantiate
    requires static java.desktop;
}

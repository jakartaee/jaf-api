/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.activation.spi;

import jakarta.activation.MimeTypeRegistry;

import java.io.IOException;
import java.io.InputStream;

public interface MimeTypeRegistryProvider {

    MimeTypeRegistry getByFileName(String name) throws IOException;

    MimeTypeRegistry getByInputStream(InputStream inputStream) throws IOException;

    MimeTypeRegistry getDefault();
}
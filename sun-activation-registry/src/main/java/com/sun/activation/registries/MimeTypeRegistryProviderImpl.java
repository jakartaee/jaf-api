/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.activation.registries;

import jakarta.activation.MimeTypeRegistry;
import jakarta.activation.spi.MimeTypeRegistryProvider;

import java.io.IOException;
import java.io.InputStream;

public class MimeTypeRegistryProviderImpl implements MimeTypeRegistryProvider {
    @Override
    public MimeTypeRegistry getByFileName(String name) throws IOException {
        return new MimeTypeFile(name);
    }

    @Override
    public MimeTypeRegistry getByInputStream(InputStream inputStream) throws IOException {
        return new MimeTypeFile(inputStream);
    }

    @Override
    public MimeTypeRegistry getInMemory() {
        return new MimeTypeFile();
    }
}

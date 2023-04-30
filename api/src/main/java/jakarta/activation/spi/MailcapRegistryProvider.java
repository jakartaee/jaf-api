/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package jakarta.activation.spi;

import jakarta.activation.MailcapRegistry;

import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.ServiceConfigurationError;

/**
 * This interface defines a factory for <code>MailcapRegistry</code>. An
 * implementation of this interface should provide instances of the MailcapRegistry
 * based on the way how to access the storage for MailcapEntries.
 * <p>
 * Jakarta Activation uses Service Provider Interface and <code>ServiceLoader</code>
 * to obtain an instance of the implementation of the <code>MailcapRegistryProvider</code>.
 */
public interface MailcapRegistryProvider {

    /**
     * Retrieve an instance of the MailcapRegistry based on the name of the file where the MailcapEntries are stored.
     *
     * @param name The name of the file that stores MailcapEntries.
     * @return The instance of the <code>MailcapRegistry</code>, or <i>null</i> if none are found.
     * @throws IOException If an instance of the MailcapRegistry class cannot be found or loaded.
     */
    MailcapRegistry getByFileName(String name) throws IOException;

    /**
     * Retrieve an instance of the MailcapRegistry based on the InputStream
     * that is used to read data from some named resource.
     *
     * @param inputStream InputStream for some resource that contains MailcapEntries.
     * @return The instance of the <code>MailcapRegistry</code>, or <i>null</i> if none are found.
     * @throws IOException If an instance of the MailcapRegistry class cannot be found or loaded.
     */
    MailcapRegistry getByInputStream(InputStream inputStream) throws IOException;

    /**
     * Retrieve an instance of the in-memory implementation of the MailcapRegistry.
     *
     * @return In-memory implementation of the MailcapRegistry.
     * @throws NoSuchElementException    If no implementations were found.
     * @throws ServiceConfigurationError If no implementations were loaded.
     */
    MailcapRegistry getInMemory();
}

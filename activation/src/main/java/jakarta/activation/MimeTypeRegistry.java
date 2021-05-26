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

/**
 * The MimeTypeRegistry interface is implemented by objects that can
 * be used to store and retrieve MimeTypeEntries.
 * <p>
 * Application must implement {@link jakarta.activation.spi.MimeTypeRegistryProvider}
 * to create new instances of the MimeTypeRegistry. Implementation of the MimeTypeRegistry
 * can store MimeTypeEntries in different ways and that storage must be accessible through the
 * {@link jakarta.activation.spi.MimeTypeRegistryProvider} methods.
 * Implementation of the MimeTypeRegistry must contain in-memory storage for MimeTypeEntries.
 */
public interface MimeTypeRegistry {

    /**
     * get the MimeTypeEntry based on the file extension
     *
     * @param file_ext the file extension
     * @return the MimeTypeEntry
     */
    MimeTypeEntry getMimeTypeEntry(String file_ext);

    /**
     * Get the MIME type string corresponding to the file extension.
     *
     * @param file_ext the file extension
     * @return the MIME type string
     */
    default String getMIMETypeString(String file_ext) {
        MimeTypeEntry entry = this.getMimeTypeEntry(file_ext);

        if (entry != null) {
            return entry.getMIMEType();
        }
        return null;
    }

    /**
     * Appends string of entries to the types registry
     *
     * @param mime_types the mime.types string
     */
    void appendToRegistry(String mime_types);
}
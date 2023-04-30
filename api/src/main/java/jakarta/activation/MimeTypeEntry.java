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

/**
 * Represents mapping between the file extension and the MIME type string.
 */
public class MimeTypeEntry {
    private String type;
    private String extension;

    /**
     * Create new {@code MimeTypeEntry}
     *
     * @param mime_type the MIME type string
     * @param file_ext  the file extension
     */
    public MimeTypeEntry(String mime_type, String file_ext) {
        type = mime_type;
        extension = file_ext;
    }

    /**
     * Get MIME type string
     *
     * @return the MIME type string
     */
    public String getMIMEType() {
        return type;
    }

    /**
     * Get the file extension
     *
     * @return the file extension
     */
    public String getFileExtension() {
        return extension;
    }

    @Override
    public String toString() {
        return "MIMETypeEntry: " + type + ", " + extension;
    }
}

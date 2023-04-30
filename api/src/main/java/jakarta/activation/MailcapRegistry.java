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

import java.util.List;
import java.util.Map;

/**
 * The MailcapRegistry interface is implemented by objects that can
 * be used to store and retrieve MailcapEntries.
 * <p>
 * Application must implement {@link jakarta.activation.spi.MailcapRegistryProvider}
 * to create new instances of the MailcapRegistry. Implementation of the MailcapRegistry
 * can store MailcapEntries in different ways and that storage must be accessible through the
 * {@link jakarta.activation.spi.MailcapRegistryProvider} methods.
 * Implementation of the MailcapRegistry must contain in-memory storage for MailcapEntries.
 */
public interface MailcapRegistry {

    /**
     * Get the Map of MailcapEntries based on the MIME type.
     *
     * <p>
     * <strong>Semantics:</strong> First check for the literal mime type,
     * if that fails looks for wildcard &lt;type&gt;/\* and return that.
     * Return the list of all that hit.
     *
     * @param    mime_type    the MIME type
     * @return the map of MailcapEntries
     */
    Map<String, List<String>> getMailcapList(String mime_type);

    /**
     * Get the Map of fallback MailcapEntries based on the MIME type.
     *
     * <p>
     * <strong>Semantics:</strong> First check for the literal mime type,
     * if that fails looks for wildcard &lt;type&gt;/\* and return that.
     * Return the list of all that hit.
     *
     * @param    mime_type    the MIME type
     * @return the map of fallback MailcapEntries
     */
    Map<String, List<String>> getMailcapFallbackList(String mime_type);

    /**
     * Return all the MIME types known to this mailcap file.
     *
     * @return a String array of the MIME types
     */
    String[] getMimeTypes();

    /**
     * Return all the native comands for the given MIME type.
     *
     * @param    mime_type    the MIME type
     * @return a String array of the commands
     */
    String[] getNativeCommands(String mime_type);

    /**
     * appendToMailcap: Append to this Mailcap DB, use the mailcap
     * format:
     * Comment == "# <i>comment string</i>"
     * Entry == "mimetype;        javabeanclass"
     * <p>
     * Example:
     * # this is a comment
     * image/gif       jaf.viewers.ImageViewer
     *
     * @param    mail_cap    the mailcap string
     */
    void appendToMailcap(String mail_cap);
}

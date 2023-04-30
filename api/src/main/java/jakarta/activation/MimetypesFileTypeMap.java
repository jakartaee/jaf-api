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

import jakarta.activation.spi.MimeTypeRegistryProvider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.NoSuchElementException;
import java.util.ServiceConfigurationError;
import java.util.Vector;

/**
 * This class extends FileTypeMap and provides data typing of files
 * via their file extension. It uses the <code>.mime.types</code> format. <p>
 *
 * <b>MIME types file search order:</b><p>
 * The MimetypesFileTypeMap looks in various places in the user's
 * system for MIME types file entries. When requests are made
 * to search for MIME types in the MimetypesFileTypeMap, it searches
 * MIME types files in the following order:
 * <ol>
 * <li> Programmatically added entries to the MimetypesFileTypeMap instance.
 * <li> The file <code>.mime.types</code> in the user's home directory.
 * <li> The file <code>mime.types</code> in the Java runtime.
 * <li> The file or resources named <code>META-INF/mime.types</code>.
 * <li> The file or resource named <code>META-INF/mimetypes.default</code>
 * (usually found only in the <code>activation.jar</code> file).
 * </ol>
 * <p>
 * (The current implementation looks for the <code>mime.types</code> file
 * in the Java runtime in the directory <code><i>java.home</i>/conf</code>
 * if it exists, and otherwise in the directory
 * <code><i>java.home</i>/lib</code>, where <i>java.home</i> is the value
 * of the "java.home" System property.  Note that the "conf" directory was
 * introduced in JDK 9.)
 * <p>
 * <b>MIME types file format:</b><p>
 *
 * <code>
 * # comments begin with a '#'<br>
 * # the format is &lt;mime type&gt; &lt;space separated file extensions&gt;<br>
 * # for example:<br>
 * text/plain    txt text TXT<br>
 * # this would map file.txt, file.text, and file.TXT to<br>
 * # the mime type "text/plain"<br>
 * </code>
 *
 * @author Bart Calder
 * @author Bill Shannon
 */
public class MimetypesFileTypeMap extends FileTypeMap {
    /*
     * We manage a collection of databases, searched in order.
     */
    private MimeTypeRegistry[] DB;
    private static final int PROG = 0;    // programmatically added entries

    private static final String defaultType = "application/octet-stream";

    private static final String confDir;

    static {
        String dir = null;
        try {
            dir = AccessController.doPrivileged(
                    new PrivilegedAction<String>() {
                        public String run() {
                            String home = System.getProperty("java.home");
                            String newdir = home + File.separator + "conf";
                            File conf = new File(newdir);
                            if (conf.exists())
                                return newdir + File.separator;
                            else
                                return home + File.separator + "lib" + File.separator;
                        }
                    });
        } catch (Exception ex) {
            if (LogSupport.isLoggable())
                LogSupport.log("Exception during MimetypesFileTypeMap class loading", ex);
        }
        confDir = dir;
    }

    /**
     * The default constructor.
     */
    public MimetypesFileTypeMap() {
        Vector<MimeTypeRegistry> dbv = new Vector<>(5);    // usually 5 or less databases
        MimeTypeRegistry mf = null;
        dbv.addElement(null);        // place holder for PROG entry

        LogSupport.log("MimetypesFileTypeMap: load HOME");
        try {
            String user_home = System.getProperty("user.home");

            if (user_home != null) {
                String path = user_home + File.separator + ".mime.types";
                mf = loadFile(path);
                if (mf != null)
                    dbv.addElement(mf);
            }
        } catch (SecurityException ex) {
            if (LogSupport.isLoggable())
                LogSupport.log("Exception during MimetypesFileTypeMap class instantiation", ex);
        }

        LogSupport.log("MimetypesFileTypeMap: load SYS");
        try {
            // check system's home
            if (confDir != null) {
                mf = loadFile(confDir + "mime.types");
                if (mf != null)
                    dbv.addElement(mf);
            }
        } catch (SecurityException ex) {
            if (LogSupport.isLoggable())
                LogSupport.log("Exception during MimetypesFileTypeMap class instantiation", ex);
        }

        LogSupport.log("MimetypesFileTypeMap: load JAR");
        // load from the app's jar file
        loadAllResources(dbv, "META-INF/mime.types");

        LogSupport.log("MimetypesFileTypeMap: load DEF");
        mf = loadResource("/META-INF/mimetypes.default");

        if (mf != null)
            dbv.addElement(mf);

        DB = new MimeTypeRegistry[dbv.size()];
        dbv.copyInto(DB);
    }

    /**
     * Load from the named resource.
     */
    private MimeTypeRegistry loadResource(String name) {
        InputStream clis = null;
        try {
            clis = SecuritySupport.getResourceAsStream(this.getClass(), name);
            if (clis != null) {
                MimeTypeRegistry mf = getImplementation().getByInputStream(clis);
                if (LogSupport.isLoggable())
                    LogSupport.log("MimetypesFileTypeMap: successfully " +
                            "loaded mime types file: " + name);
                return mf;
            } else {
                if (LogSupport.isLoggable())
                    LogSupport.log("MimetypesFileTypeMap: not loading " +
                            "mime types file: " + name);
            }
        } catch (IOException | SecurityException e) {
            if (LogSupport.isLoggable())
                LogSupport.log("MimetypesFileTypeMap: can't load " + name, e);
        } catch (NoSuchElementException | ServiceConfigurationError e) {
            if (LogSupport.isLoggable()) {
                LogSupport.log("Cannot find or load an implementation for MimeTypeRegistryProvider." +
                        "MimeTypeRegistry: can't load " + name, e);
            }
        } finally {
            try {
                if (clis != null)
                    clis.close();
            } catch (IOException ex) {
                if (LogSupport.isLoggable())
                    LogSupport.log("InputStream cannot be close for " + name, ex);
            }
        }
        return null;
    }

    /**
     * Load all of the named resource.
     */
    private void loadAllResources(Vector<MimeTypeRegistry> v, String name) {
        boolean anyLoaded = false;
        try {
            URL[] urls;
            ClassLoader cld = null;
            // First try the "application's" class loader.
            cld = SecuritySupport.getContextClassLoader();
            if (cld == null)
                cld = this.getClass().getClassLoader();
            if (cld != null)
                urls = SecuritySupport.getResources(cld, name);
            else
                urls = SecuritySupport.getSystemResources(name);
            if (urls != null) {
                if (LogSupport.isLoggable())
                    LogSupport.log("MimetypesFileTypeMap: getResources");
                for (int i = 0; i < urls.length; i++) {
                    URL url = urls[i];
                    InputStream clis = null;
                    if (LogSupport.isLoggable())
                        LogSupport.log("MimetypesFileTypeMap: URL " + url);
                    try {
                        clis = SecuritySupport.openStream(url);
                        if (clis != null) {
                            v.addElement(
                                    getImplementation().getByInputStream(clis)
                            );
                            anyLoaded = true;
                            if (LogSupport.isLoggable())
                                LogSupport.log("MimetypesFileTypeMap: " +
                                        "successfully loaded " +
                                        "mime types from URL: " + url);
                        } else {
                            if (LogSupport.isLoggable())
                                LogSupport.log("MimetypesFileTypeMap: " +
                                        "not loading " +
                                        "mime types from URL: " + url);
                        }
                    } catch (IOException | SecurityException ioex) {
                        if (LogSupport.isLoggable())
                            LogSupport.log("MimetypesFileTypeMap: can't load " +
                                    url, ioex);
                    } catch (NoSuchElementException | ServiceConfigurationError e) {
                        if (LogSupport.isLoggable()) {
                            LogSupport.log("Cannot find or load an implementation for MimeTypeRegistryProvider." +
                                    "MimeTypeRegistry: can't load " + url, e);
                        }
                    } finally {
                        try {
                            if (clis != null)
                                clis.close();
                        } catch (IOException cex) {
                            if (LogSupport.isLoggable())
                                LogSupport.log("InputStream cannot be close for " + name, cex);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            if (LogSupport.isLoggable())
                LogSupport.log("MimetypesFileTypeMap: can't load " + name, ex);
        }

        // if failed to load anything, fall back to old technique, just in case
        if (!anyLoaded) {
            LogSupport.log("MimetypesFileTypeMap: !anyLoaded");
            MimeTypeRegistry mf = loadResource("/" + name);
            if (mf != null)
                v.addElement(mf);
        }
    }

    /**
     * Load the named file.
     */
    private MimeTypeRegistry loadFile(String name) {
        MimeTypeRegistry mtf = null;

        try {
            mtf = getImplementation().getByFileName(name);
        } catch (IOException e) {
            if (LogSupport.isLoggable()) {
                LogSupport.log("MimeTypeRegistry: can't load from file - " + name, e);
            }
        } catch (NoSuchElementException | ServiceConfigurationError e) {
            if (LogSupport.isLoggable()) {
                LogSupport.log("Cannot find or load an implementation for MimeTypeRegistryProvider." +
                        "MimeTypeRegistry: can't load " + name, e);
            }
        }
        return mtf;
    }

    /**
     * Construct a MimetypesFileTypeMap with programmatic entries
     * added from the named file.
     *
     * @param mimeTypeFileName the file name
     * @throws IOException for errors reading the file
     */
    public MimetypesFileTypeMap(String mimeTypeFileName) throws IOException {
        this();
        try {
            DB[PROG] = getImplementation().getByFileName(mimeTypeFileName);
        } catch (NoSuchElementException | ServiceConfigurationError e) {
            String errorMessage = "Cannot find or load an implementation for MimeTypeRegistryProvider." +
                    "MimeTypeRegistry: can't load " + mimeTypeFileName;
            if (LogSupport.isLoggable()) {
                LogSupport.log(errorMessage, e);
            }
            throw new IOException(errorMessage, e);
        }
    }

    /**
     * Construct a MimetypesFileTypeMap with programmatic entries
     * added from the InputStream.
     *
     * @param is the input stream to read from
     */
    public MimetypesFileTypeMap(InputStream is) {
        this();
        try {
            DB[PROG] = getImplementation().getByInputStream(is);
        } catch (IOException ex) {
            // XXX - really should throw it
        } catch (NoSuchElementException | ServiceConfigurationError e) {
            if (LogSupport.isLoggable()) {
                LogSupport.log("Cannot find or load an implementation for MimeTypeRegistryProvider." +
                        "MimeTypeRegistry: can't load InputStream", e);
            }
        }
    }

    /**
     * Prepend the MIME type values to the registry.
     *
     * @param mime_types A .mime.types formatted string of entries.
     */
    public synchronized void addMimeTypes(String mime_types) {
        try {
            // check to see if we have created the registry
            if (DB[PROG] == null) {
                DB[PROG] = getImplementation().getInMemory();
            }
            DB[PROG].appendToRegistry(mime_types);
        } catch (NoSuchElementException | ServiceConfigurationError e) {
            if (LogSupport.isLoggable()) {
                LogSupport.log("Cannot find or load an implementation for MimeTypeRegistryProvider." +
                        "MimeTypeRegistry: can't add " + mime_types, e);
            }
            throw e;
        }
    }

    /**
     * Return the MIME type of the <Code>File</Code> object.
     * The implementation in this class calls
     * <code>getContentType(f.getName())</code>.
     *
     * @param f the file
     * @return the file's MIME type
     */
    public String getContentType(File f) {
        return this.getContentType(f.getName());
    }

    /**
     * Return the MIME type of the <Code>Path</Code> object.
     * The implementation in this class calls
     * <code>getContentType(p.getFileName().toString())</code>.
     *
     * @param p the file <Code>Path</Code>
     * @return the file's MIME type
     */
    public String getContentType(Path p) {
        return this.getContentType(p.getFileName().toString());
    }

    /**
     * Return the MIME type based on the specified file name.
     * The MIME type entries are searched as described above under
     * <i>MIME types file search order</i>.
     * If no entry is found, the type "application/octet-stream" is returned.
     *
     * @param filename the file name
     * @return the file's MIME type
     */
    public synchronized String getContentType(String filename) {
        int dot_pos = filename.lastIndexOf("."); // period index

        if (dot_pos < 0)
            return defaultType;

        String file_ext = filename.substring(dot_pos + 1);
        if (file_ext.length() == 0)
            return defaultType;

        for (int i = 0; i < DB.length; i++) {
            if (DB[i] == null)
                continue;
            String result = DB[i].getMIMETypeString(file_ext);
            if (result != null)
                return result;
        }
        return defaultType;
    }

    private MimeTypeRegistryProvider getImplementation() {
        if (System.getSecurityManager() != null) {
            return AccessController.doPrivileged(new PrivilegedAction<MimeTypeRegistryProvider>() {
                public MimeTypeRegistryProvider run() {
                    return FactoryFinder.find(MimeTypeRegistryProvider.class,
                            null,
                            false);
                }
            });
        } else {
            return FactoryFinder.find(MimeTypeRegistryProvider.class,
                    null,
                    false);
        }
    }

    /*
     * for debugging...
     *
     public static void main(String[] argv) throws Exception {
     MimetypesFileTypeMap map = new MimetypesFileTypeMap();
     System.out.println("File " + argv[0] + " has MIME type " +
     map.getContentType(argv[0]));
     System.exit(0);
     }
     */
}

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

import jakarta.activation.spi.MailcapRegistryProvider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.ServiceConfigurationError;

/**
 * MailcapCommandMap extends the CommandMap
 * abstract class. It implements a CommandMap whose configuration
 * is based on mailcap files
 * (<A HREF="http://www.ietf.org/rfc/rfc1524.txt">RFC 1524</A>).
 * The MailcapCommandMap can be configured both programmatically
 * and via configuration files.
 * <p>
 * <b>Mailcap file search order:</b><p>
 * The MailcapCommandMap looks in various places in the user's
 * system for mailcap file entries. When requests are made
 * to search for commands in the MailcapCommandMap, it searches
 * mailcap files in the following order:
 * <ol>
 * <li> Programatically added entries to the MailcapCommandMap instance.
 * <li> The file <code>.mailcap</code> in the user's home directory.
 * <li> The file <code>mailcap</code> in the Java runtime.
 * <li> The file or resources named <code>META-INF/mailcap</code>.
 * <li> The file or resource named <code>META-INF/mailcap.default</code>
 * (usually found only in the <code>activation.jar</code> file).
 * </ol>
 * <p>
 * (The current implementation looks for the <code>mailcap</code> file
 * in the Java runtime in the directory <code><i>java.home</i>/conf</code>
 * if it exists, and otherwise in the directory
 * <code><i>java.home</i>/lib</code>, where <i>java.home</i> is the value
 * of the "java.home" System property.  Note that the "conf" directory was
 * introduced in JDK 9.)
 * <p>
 * <b>Mailcap file format:</b><p>
 *
 * Mailcap files must conform to the mailcap
 * file specification (RFC 1524, <i>A User Agent Configuration Mechanism
 * For Multimedia Mail Format Information</i>).
 * The file format consists of entries corresponding to
 * particular MIME types. In general, the specification
 * specifies <i>applications</i> for clients to use when they
 * themselves cannot operate on the specified MIME type. The
 * MailcapCommandMap extends this specification by using a parameter mechanism
 * in mailcap files that allows JavaBeans(tm) components to be specified as
 * corresponding to particular commands for a MIME type.<p>
 *
 * When a mailcap file is
 * parsed, the MailcapCommandMap recognizes certain parameter signatures,
 * specifically those parameter names that begin with <code>x-java-</code>.
 * The MailcapCommandMap uses this signature to find
 * command entries for inclusion into its registries.
 * Parameter names with the form <code>x-java-&lt;name&gt;</code>
 * are read by the MailcapCommandMap as identifying a command
 * with the name <i>name</i>. When the <i>name</i> is <code>
 * content-handler</code> the MailcapCommandMap recognizes the class
 * signified by this parameter as a <i>DataContentHandler</i>.
 * All other commands are handled generically regardless of command
 * name. The command implementation is specified by a fully qualified
 * class name of a JavaBean(tm) component. For example; a command for viewing
 * some data can be specified as: <code>x-java-view=com.foo.ViewBean</code>.<p>
 *
 * When the command name is <code>fallback-entry</code>, the value of
 * the command may be <code>true</code> or <code>false</code>.  An
 * entry for a MIME type that includes a parameter of
 * <code>x-java-fallback-entry=true</code> defines fallback commands
 * for that MIME type that will only be used if no non-fallback entry
 * can be found.  For example, an entry of the form <code>text/*; ;
 * x-java-fallback-entry=true; x-java-view=com.sun.TextViewer</code>
 * specifies a view command to be used for any text MIME type.  This
 * view command would only be used if a non-fallback view command for
 * the MIME type could not be found.<p>
 *
 * MailcapCommandMap aware mailcap files have the
 * following general form:<p>
 * <code>
 * # Comments begin with a '#' and continue to the end of the line.<br>
 * &lt;mime type&gt;; ; &lt;parameter list&gt;<br>
 * # Where a parameter list consists of one or more parameters,<br>
 * # where parameters look like: x-java-view=com.sun.TextViewer<br>
 * # and a parameter list looks like: <br>
 * text/plain; ; x-java-view=com.sun.TextViewer; x-java-edit=com.sun.TextEdit
 * <br>
 * # Note that mailcap entries that do not contain 'x-java' parameters<br>
 * # and comply to RFC 1524 are simply ignored:<br>
 * image/gif; /usr/dt/bin/sdtimage %s<br>
 *
 * </code>
 *
 * @author Bart Calder
 * @author Bill Shannon
 */

public class MailcapCommandMap extends CommandMap {
    /*
     * We manage a collection of databases, searched in order.
     */
    private MailcapRegistry[] DB;
    private static final int PROG = 0;    // programmatically added entries

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
            // ignore any exceptions
        }
        confDir = dir;
    }

    /**
     * The default Constructor.
     */
    public MailcapCommandMap() {
        super();
        List<MailcapRegistry> dbv = new ArrayList<>(5);    // usually 5 or less databases
        MailcapRegistry mf = null;
        dbv.add(null);        // place holder for PROG entry

        LogSupport.log("MailcapCommandMap: load HOME");
        try {
            String user_home = System.getProperty("user.home");

            if (user_home != null) {
                String path = user_home + File.separator + ".mailcap";
                mf = loadFile(path);
                if (mf != null)
                    dbv.add(mf);
            }
        } catch (SecurityException ex) {
        }

        LogSupport.log("MailcapCommandMap: load SYS");
        try {
            // check system's home
            if (confDir != null) {
                mf = loadFile(confDir + "mailcap");
                if (mf != null)
                    dbv.add(mf);
            }
        } catch (SecurityException ex) {
        }

        LogSupport.log("MailcapCommandMap: load JAR");
        // load from the app's jar file
        loadAllResources(dbv, "META-INF/mailcap");

        LogSupport.log("MailcapCommandMap: load DEF");
        mf = loadResource("/META-INF/mailcap.default");

        if (mf != null)
            dbv.add(mf);

        DB = new MailcapRegistry[dbv.size()];
        DB = dbv.toArray(DB);
    }

    /**
     * Load from the named resource.
     */
    private MailcapRegistry loadResource(String name) {
        try (InputStream clis = SecuritySupport.getResourceAsStream(this.getClass(), name)) {
            if (clis != null) {
                MailcapRegistry mf = getImplementation().getByInputStream(clis);
                if (LogSupport.isLoggable())
                    LogSupport.log("MailcapCommandMap: successfully loaded " +
                            "mailcap file: " + name);
                return mf;
            } else {
                if (LogSupport.isLoggable())
                    LogSupport.log("MailcapCommandMap: not loading " +
                            "mailcap file: " + name);
            }
        } catch (IOException | SecurityException e) {
            if (LogSupport.isLoggable())
                LogSupport.log("MailcapCommandMap: can't load " + name, e);
        } catch (NoSuchElementException | ServiceConfigurationError e) {
            if (LogSupport.isLoggable()) {
                LogSupport.log("Cannot find or load an implementation for MailcapRegistryProvider. " +
                        "MailcapRegistry: can't load " + name, e);
            }
        }
        return null;
    }

    /**
     * Load all of the named resource.
     */
    private void loadAllResources(List<MailcapRegistry> v, String name) {
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
                    LogSupport.log("MailcapCommandMap: getResources");
                for (int i = 0; i < urls.length; i++) {
                    URL url = urls[i];
                    if (LogSupport.isLoggable())
                        LogSupport.log("MailcapCommandMap: URL " + url);
                    try (InputStream clis = SecuritySupport.openStream(url)) {
                        if (clis != null) {
                            v.add(getImplementation().getByInputStream(clis));
                            anyLoaded = true;
                            if (LogSupport.isLoggable())
                                LogSupport.log("MailcapCommandMap: " +
                                        "successfully loaded " +
                                        "mailcap file from URL: " +
                                        url);
                        } else {
                            if (LogSupport.isLoggable())
                                LogSupport.log("MailcapCommandMap: " +
                                        "not loading mailcap " +
                                        "file from URL: " + url);
                        }
                    } catch (IOException | SecurityException ioex) {
                        if (LogSupport.isLoggable())
                            LogSupport.log("MailcapCommandMap: can't load " +
                                    url, ioex);
                    } catch (NoSuchElementException | ServiceConfigurationError e) {
                        if (LogSupport.isLoggable()) {
                            LogSupport.log("Cannot find or load an implementation for MailcapRegistryProvider. " +
                                    "MailcapRegistry: can't load " + name, e);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            if (LogSupport.isLoggable())
                LogSupport.log("MailcapCommandMap: can't load " + name, ex);
        }

        // if failed to load anything, fall back to old technique, just in case
        if (!anyLoaded) {
            if (LogSupport.isLoggable())
                LogSupport.log("MailcapCommandMap: !anyLoaded");
            MailcapRegistry mf = loadResource("/" + name);
            if (mf != null)
                v.add(mf);
        }
    }

    /**
     * Load from the named file.
     */
    private MailcapRegistry loadFile(String name) {
        MailcapRegistry mtf = null;

        try {
            mtf = getImplementation().getByFileName(name);
        } catch (IOException e) {
            if (LogSupport.isLoggable()) {
                LogSupport.log("MailcapRegistry: can't load from file - " + name, e);
            }
        } catch (NoSuchElementException | ServiceConfigurationError e) {
            if (LogSupport.isLoggable()) {
                LogSupport.log("Cannot find or load an implementation for MailcapRegistryProvider. " +
                        "MailcapRegistry: can't load " + name, e);
            }
        }
        return mtf;
    }

    /**
     * Constructor that allows the caller to specify the path
     * of a <i>mailcap</i> file.
     *
     * @param fileName The name of the <i>mailcap</i> file to open
     * @throws IOException if the file can't be accessed
     */
    public MailcapCommandMap(String fileName) throws IOException {
        this();
        if (DB[PROG] == null) {
            try {
                DB[PROG] = getImplementation().getByFileName(fileName);
            } catch (NoSuchElementException | ServiceConfigurationError e) {
                String message = "Cannot find or load an implementation for MailcapRegistryProvider. " +
                        "MailcapRegistry: can't load " + fileName;
                if (LogSupport.isLoggable()) {
                    LogSupport.log(message, e);
                }
                throw new IOException(message, e);
            }
        }
        if (DB[PROG] != null && LogSupport.isLoggable()) {
            LogSupport.log("MailcapCommandMap: load PROG from " + fileName);
        }
    }


    /**
     * Constructor that allows the caller to specify an <i>InputStream</i>
     * containing a mailcap file.
     *
     * @param is InputStream of the <i>mailcap</i> file to open
     */
    public MailcapCommandMap(InputStream is) {
        this();

        if (DB[PROG] == null) {
            try {
                DB[PROG] = getImplementation().getByInputStream(is);
            } catch (IOException ex) {
                // XXX - should throw it
            } catch (NoSuchElementException | ServiceConfigurationError e) {
                if (LogSupport.isLoggable()) {
                    LogSupport.log("Cannot find or load an implementation for MailcapRegistryProvider." +
                            "MailcapRegistry: can't load InputStream", e);
                }
            }
        }
        if (DB[PROG] != null && LogSupport.isLoggable()) {
            LogSupport.log("MailcapCommandMap: load PROG");
        }
    }

    /**
     * Get the preferred command list for a MIME Type. The MailcapCommandMap
     * searches the mailcap files as described above under
     * <i>Mailcap file search order</i>.<p>
     *
     * The result of the search is a proper subset of available
     * commands in all mailcap files known to this instance of
     * MailcapCommandMap.  The first entry for a particular command
     * is considered the preferred command.
     *
     * @param mimeType the MIME type
     * @return the CommandInfo objects representing the preferred commands.
     */
    public synchronized CommandInfo[] getPreferredCommands(String mimeType) {
        List<CommandInfo> cmdList = new ArrayList<>();
        if (mimeType != null)
            mimeType = mimeType.toLowerCase(Locale.ENGLISH);

        for (int i = 0; i < DB.length; i++) {
            if (DB[i] == null)
                continue;
            Map<String, List<String>> cmdMap = DB[i].getMailcapList(mimeType);
            if (cmdMap != null)
                appendPrefCmdsToList(cmdMap, cmdList);
        }

        // now add the fallback commands
        for (int i = 0; i < DB.length; i++) {
            if (DB[i] == null)
                continue;
            Map<String, List<String>> cmdMap = DB[i].getMailcapFallbackList(mimeType);
            if (cmdMap != null)
                appendPrefCmdsToList(cmdMap, cmdList);
        }

        CommandInfo[] cmdInfos = new CommandInfo[cmdList.size()];
        cmdInfos = cmdList.toArray(cmdInfos);

        return cmdInfos;
    }

    /**
     * Put the commands that are in the hash table, into the list.
     */
    private void appendPrefCmdsToList(Map<String, List<String>> cmdHash, List<CommandInfo> cmdList) {
        Iterator<String> verb_enum = cmdHash.keySet().iterator();

        while (verb_enum.hasNext()) {
            String verb = verb_enum.next();
            if (!checkForVerb(cmdList, verb)) {
                List<String> cmdList2 = cmdHash.get(verb); // get the list
                String className = cmdList2.get(0);
                cmdList.add(new CommandInfo(verb, className));
            }
        }
    }

    /**
     * Check the cmdList to see if this command exists, return
     * true if the verb is there.
     */
    private boolean checkForVerb(List<CommandInfo> cmdList, String verb) {
        Iterator<CommandInfo> ee = cmdList.iterator();
        while (ee.hasNext()) {
            String enum_verb = (ee.next()).getCommandName();
            if (enum_verb.equals(verb))
                return true;
        }
        return false;
    }

    /**
     * Get all the available commands in all mailcap files known to
     * this instance of MailcapCommandMap for this MIME type.
     *
     * @param mimeType the MIME type
     * @return the CommandInfo objects representing all the commands.
     */
    public synchronized CommandInfo[] getAllCommands(String mimeType) {
        List<CommandInfo> cmdList = new ArrayList<>();
        if (mimeType != null)
            mimeType = mimeType.toLowerCase(Locale.ENGLISH);

        for (int i = 0; i < DB.length; i++) {
            if (DB[i] == null)
                continue;
            Map<String, List<String>> cmdMap = DB[i].getMailcapList(mimeType);
            if (cmdMap != null)
                appendCmdsToList(cmdMap, cmdList);
        }

        // now add the fallback commands
        for (int i = 0; i < DB.length; i++) {
            if (DB[i] == null)
                continue;
            Map<String, List<String>> cmdMap = DB[i].getMailcapFallbackList(mimeType);
            if (cmdMap != null)
                appendCmdsToList(cmdMap, cmdList);
        }

        CommandInfo[] cmdInfos = new CommandInfo[cmdList.size()];
        cmdInfos = cmdList.toArray(cmdInfos);

        return cmdInfos;
    }

    /**
     * Put the commands that are in the hash table, into the list.
     */
    private void appendCmdsToList(Map<String, List<String>> typeHash, List<CommandInfo> cmdList) {
        Iterator<String> verb_enum = typeHash.keySet().iterator();

        while (verb_enum.hasNext()) {
            String verb = verb_enum.next();
            List<String> cmdList2 = typeHash.get(verb);
            Iterator<String> cmd_enum = cmdList2.iterator();

            while (cmd_enum.hasNext()) {
                String cmd = cmd_enum.next();
                cmdList.add(new CommandInfo(verb, cmd));
                // cmdList.add(0, new CommandInfo(verb, cmd));
            }
        }
    }

    /**
     * Get the command corresponding to <code>cmdName</code> for the MIME type.
     *
     * @param mimeType the MIME type
     * @param cmdName  the command name
     * @return the CommandInfo object corresponding to the command.
     */
    public synchronized CommandInfo getCommand(String mimeType,
                                               String cmdName) {
        if (mimeType != null)
            mimeType = mimeType.toLowerCase(Locale.ENGLISH);

        for (int i = 0; i < DB.length; i++) {
            if (DB[i] == null)
                continue;
            Map<String, List<String>> cmdMap = DB[i].getMailcapList(mimeType);
            if (cmdMap != null) {
                // get the cmd list for the cmd
                List<String> v = cmdMap.get(cmdName);
                if (v != null) {
                    String cmdClassName = v.get(0);

                    if (cmdClassName != null)
                        return new CommandInfo(cmdName, cmdClassName);
                }
            }
        }

        // now try the fallback list
        for (int i = 0; i < DB.length; i++) {
            if (DB[i] == null)
                continue;
            Map<String, List<String>> cmdMap = DB[i].getMailcapFallbackList(mimeType);
            if (cmdMap != null) {
                // get the cmd list for the cmd
                List<String> v = cmdMap.get(cmdName);
                if (v != null) {
                    String cmdClassName = v.get(0);

                    if (cmdClassName != null)
                        return new CommandInfo(cmdName, cmdClassName);
                }
            }
        }
        return null;
    }

    /**
     * Add entries to the registry.  Programmatically
     * added entries are searched before other entries.<p>
     *
     * The string that is passed in should be in mailcap
     * format.
     *
     * @param mail_cap a correctly formatted mailcap string
     */
    public synchronized void addMailcap(String mail_cap) {
        // check to see if one exists
        LogSupport.log("MailcapCommandMap: add to PROG");
        try {
            if (DB[PROG] == null) {
                DB[PROG] = getImplementation().getInMemory();
            }
            DB[PROG].appendToMailcap(mail_cap);
        } catch (NoSuchElementException | ServiceConfigurationError e) {
            if (LogSupport.isLoggable()) {
                LogSupport.log("Cannot find or load an implementation for MailcapRegistryProvider. " +
                        "MailcapRegistry: can't load", e);
            }
            throw e;
        }
    }

    /**
     * Return the DataContentHandler for the specified MIME type.
     *
     * @param mimeType the MIME type
     * @return the DataContentHandler
     */
    public synchronized DataContentHandler createDataContentHandler(
            String mimeType) {
        if (LogSupport.isLoggable())
            LogSupport.log(
                    "MailcapCommandMap: createDataContentHandler for " + mimeType);
        if (mimeType != null)
            mimeType = mimeType.toLowerCase(Locale.ENGLISH);

        for (int i = 0; i < DB.length; i++) {
            if (DB[i] == null)
                continue;
            if (LogSupport.isLoggable())
                LogSupport.log("  search DB #" + i);
            Map<String, List<String>> cmdMap = DB[i].getMailcapList(mimeType);
            if (cmdMap != null) {
                List<String> v = cmdMap.get("content-handler");
                if (v != null) {
                    String name = v.get(0);
                    DataContentHandler dch = getDataContentHandler(name);
                    if (dch != null)
                        return dch;
                }
            }
        }

        // now try the fallback entries
        for (int i = 0; i < DB.length; i++) {
            if (DB[i] == null)
                continue;
            if (LogSupport.isLoggable())
                LogSupport.log("  search fallback DB #" + i);
            Map<String, List<String>> cmdMap = DB[i].getMailcapFallbackList(mimeType);
            if (cmdMap != null) {
                List<String> v = cmdMap.get("content-handler");
                if (v != null) {
                    String name = v.get(0);
                    DataContentHandler dch = getDataContentHandler(name);
                    if (dch != null)
                        return dch;
                }
            }
        }
        return null;
    }

    private DataContentHandler getDataContentHandler(String name) {
        if (LogSupport.isLoggable())
            LogSupport.log("    got content-handler");
        if (LogSupport.isLoggable())
            LogSupport.log("      class " + name);
        try {
            ClassLoader cld = null;
            // First try the "application's" class loader.
            cld = SecuritySupport.getContextClassLoader();
            if (cld == null)
                cld = this.getClass().getClassLoader();
            Class<?> cl = null;
            try {
                cl = cld.loadClass(name);
            } catch (Exception ex) {
                // if anything goes wrong, do it the old way
                cl = Class.forName(name);
            }
            if (cl != null)        // XXX - always true?
                return (DataContentHandler)
                        cl.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            if (LogSupport.isLoggable())
                LogSupport.log("Can't load DCH " + name, e);
        }
        return null;
    }

    /**
     * Get all the MIME types known to this command map.
     *
     * @return array of MIME types as strings
     * @since JAF 1.1
     */
    public synchronized String[] getMimeTypes() {
        List<String> mtList = new ArrayList<>();

        for (int i = 0; i < DB.length; i++) {
            if (DB[i] == null)
                continue;
            String[] ts = DB[i].getMimeTypes();
            if (ts != null) {
                for (int j = 0; j < ts.length; j++) {
                    // eliminate duplicates
                    if (!mtList.contains(ts[j]))
                        mtList.add(ts[j]);
                }
            }
        }

        String[] mts = new String[mtList.size()];
        mts = mtList.toArray(mts);

        return mts;
    }

    /**
     * Get the native commands for the given MIME type.
     * Returns an array of strings where each string is
     * an entire mailcap file entry.  The application
     * will need to parse the entry to extract the actual
     * command as well as any attributes it needs. See
     * <A HREF="http://www.ietf.org/rfc/rfc1524.txt">RFC 1524</A>
     * for details of the mailcap entry syntax.  Only mailcap
     * entries that specify a view command for the specified
     * MIME type are returned.
     *
     * @param    mimeType    the MIME type
     * @return array of native command entries
     * @since JAF 1.1
     */
    public synchronized String[] getNativeCommands(String mimeType) {
        List<String> cmdList = new ArrayList<>();
        if (mimeType != null)
            mimeType = mimeType.toLowerCase(Locale.ENGLISH);

        for (int i = 0; i < DB.length; i++) {
            if (DB[i] == null)
                continue;
            String[] cmds = DB[i].getNativeCommands(mimeType);
            if (cmds != null) {
                for (int j = 0; j < cmds.length; j++) {
                    // eliminate duplicates
                    if (!cmdList.contains(cmds[j]))
                        cmdList.add(cmds[j]);
                }
            }
        }

        String[] cmds = new String[cmdList.size()];
        cmds = cmdList.toArray(cmds);

        return cmds;
    }

    private MailcapRegistryProvider getImplementation() {
        if (System.getSecurityManager() != null) {
            return AccessController.doPrivileged(new PrivilegedAction<MailcapRegistryProvider>() {
                public MailcapRegistryProvider run() {
                    return FactoryFinder.find(MailcapRegistryProvider.class,
                            null,
                            false);
                }
            });
        } else {
            return FactoryFinder.find(MailcapRegistryProvider.class,
                    null,
                    false);
        }
    }

    /*
     * for debugging...
     *
    public static void main(String[] argv) throws Exception {
	MailcapCommandMap map = new MailcapCommandMap();
	CommandInfo[] cmdInfo;

	cmdInfo = map.getPreferredCommands(argv[0]);
	System.out.println("Preferred Commands:");
	for (int i = 0; i < cmdInfo.length; i++)
	    System.out.println("Command " + cmdInfo[i].getCommandName() + " [" +
					    cmdInfo[i].getCommandClass() + "]");
	cmdInfo = map.getAllCommands(argv[0]);
	System.out.println();
	System.out.println("All Commands:");
	for (int i = 0; i < cmdInfo.length; i++)
	    System.out.println("Command " + cmdInfo[i].getCommandName() + " [" +
					    cmdInfo[i].getCommandClass() + "]");
	DataContentHandler dch = map.createDataContentHandler(argv[0]);
	if (dch != null)
	    System.out.println("DataContentHandler " +
						dch.getClass().toString());
	System.exit(0);
    }
    */
}

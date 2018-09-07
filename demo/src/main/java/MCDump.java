/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

import java.io.*;
import java.beans.*;
import com.sun.activation.registries.*;
import javax.activation.*;

/**
 * Dump out everything we know about a MailcapCommandMap.
 */
public class MCDump {
    static MailcapCommandMap mcf = null;

    public static void main(String args[]) {
	
	try {
	    if (args.length == 0)
		mcf = new MailcapCommandMap();
	    else
		mcf = new MailcapCommandMap(args[0]);
	} catch (Exception e){ 
	    e.printStackTrace();
	    System.exit(1);
	}

	String[] types = mcf.getMimeTypes();
	if (types == null) {
	    System.out.println("No known MIME types");
	    System.exit(0);
	} else {
	    System.out.println("Known MIME types:");
	    for (int i = 0; i < types.length; i++)
		System.out.println("\t" + types[i]);
	}

	System.out.println();
	System.out.println("All commands for each MIME type:");
	for (int i = 0; i < types.length; i++) {
	    System.out.println("    " + types[i]);
	    CommandInfo[] cmdinfo = mcf.getAllCommands(types[i]);
	    if (cmdinfo == null) {
		System.out.println("\tNONE");
	    } else {
		for (int k = 0; k < cmdinfo.length; k++)
		    System.out.println("\t" + cmdinfo[k].getCommandName() +
			": " + cmdinfo[k].getCommandClass());
	    }
	}

	System.out.println();
	System.out.println("Preferred commands for each MIME type:");
	for (int i = 0; i < types.length; i++) {
	    System.out.println("    " + types[i]);
	    CommandInfo[] cmdinfo = mcf.getPreferredCommands(types[i]);
	    if (cmdinfo == null) {
		System.out.println("\tNONE");
	    } else {
		for (int k = 0; k < cmdinfo.length; k++)
		    System.out.println("\t" + cmdinfo[k].getCommandName() +
			": " + cmdinfo[k].getCommandClass());
	    }
	}

	System.out.println();
	System.out.println("Native commands for each MIME type:");
	for (int i = 0; i < types.length; i++) {
	    System.out.println("    " + types[i]);
	    String[] cmds = mcf.getNativeCommands(types[i]);
	    if (cmds.length == 0) {
		System.out.println("\tNONE");
	    } else {
		for (int k = 0; k < cmds.length; k++)
		    System.out.println("\t" + cmds[k]);
	    }
	}
    }
}

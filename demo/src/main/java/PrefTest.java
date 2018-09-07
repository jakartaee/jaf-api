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
import javax.activation.*;

public class PrefTest {

    public static void main(String args[]) {
	MailcapCommandMap mcf = null;

	if (args.length > 0) {
	    try {
		mcf = new MailcapCommandMap(args[0]);
	    } catch (Exception e) {
		e.printStackTrace();
		System.exit(1);
	    }
	} else
	    mcf = new MailcapCommandMap();

	CommandInfo cmdinfo[] = mcf.getAllCommands("text/plain");
	
	if (cmdinfo != null) {
	    System.out.println("ALL Commands for text/plain:");
	    for (int i = 0; i < cmdinfo.length; i++) {
		System.out.println("Verb: " + cmdinfo[i].getCommandName() +
				  " Class: " + cmdinfo[i].getCommandClass());
	    }
	    System.out.println("done");
	} else {
	    System.out.println("no commands");
	}
	System.out.println();

	cmdinfo = mcf.getPreferredCommands("text/plain");
	if (cmdinfo != null) {
	    System.out.println("PREFERRED Commands for text/plain:");
	    for (int i = 0; i < cmdinfo.length; i++) {
		System.out.println("Verb: " + cmdinfo[i].getCommandName() +
				  " Class: " + cmdinfo[i].getCommandClass());
	    }
	    System.out.println("done");
	} else {
	    System.out.println("no commands");
	}
	System.out.println();
    }
}

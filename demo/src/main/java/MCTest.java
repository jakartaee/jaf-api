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

public class MCTest {
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

	CommandInfo cmdinfo[] = mcf.getAllCommands("text/plain");
        System.out.print("Are there any commands for text/plain?");

	if (cmdinfo != null) {
	    System.out.println("number of cmds = " + cmdinfo.length);
	    System.out.println("now try an individual cmd");
	    CommandInfo info = mcf.getCommand("text/plain", "view");
	    if (info != null) {
		System.out.println("Got command...");
	    } else {
		System.out.println("no cmds");
	    }

	    mcf.addMailcap("text/plain;; x-java-flobotz=com.sun.activation.flobotz\n");	
	    //	    System.out.println("...dome");
	    if (cmdinfo != null) {
		cmdinfo = mcf.getAllCommands("text/plain");
		System.out.println("now we have cmds = " + cmdinfo.length);
		
	    }	

        } else {
	    System.out.println("NO CMDS AT ALL!");
	}
    }
}

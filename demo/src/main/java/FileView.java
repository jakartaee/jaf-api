/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

import java.awt.*;
import java.beans.*;
import java.net.*;
import javax.activation.*;

public class FileView {
    private Frame frame;

    public static void main(String args[]) throws Exception {
        FileView fv = new FileView();
        if (args.length == 0) {
            System.out.println("usage: FileView  file.txt");
            System.exit(1);
        }
        fv.view(args[0]);
    }

    private void view(String filename) throws Exception {
  	FileDataSource fds = new FileDataSource(filename); 
  	DataHandler dh = new DataHandler(fds); 
	// comment out previous two lines, and uncomment next
	// line and pass in a URL on the command line.
	// DataHandler dh = new DataHandler(new URL(filename));

	CommandInfo bi = dh.getCommand("view");
	
	if (bi == null) {
	    System.out.println("no viewer found, exiting");
	    System.exit(1);
	}

	frame = new Frame("Viewer");
	frame.add((Component)dh.getBean(bi));
	frame.setSize(new Dimension(400,300));
	frame.show();
    }
}

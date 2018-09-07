/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

import java.net.*;
import java.io.*;
import javax.activation.*;

public class DHURL {
    URL url = null;
    DataHandler dh = null;
    
    public static void main(String args[]){
        DHURL test = new DHURL();

        if(args.length == 0) {
            System.out.println("usage: DHURL url");
            System.exit(1);
        }

        test.setURL(args[0]);

        test.doit();

    }

    public void setURL(String url) {
	
	try {
	    this.url = new URL(url);
	} catch(MalformedURLException e) {
	    e.printStackTrace();
	    System.out.println("malformed URL!!!");
	    System.exit(1);
	}

    }
    
    public void doit() {
	System.out.print("Creating DataHandler...");
	dh = new DataHandler(url);
	System.out.println("...done.");

	System.out.println("The MimeType of the DH : " +
			   dh.getContentType());
	try {
	InputStream is = dh.getInputStream();
	if(is != null)
	    System.out.println("got an inputstream");
	} catch(Exception e) {
	    e.printStackTrace();
	}

	
    }
	
    
}

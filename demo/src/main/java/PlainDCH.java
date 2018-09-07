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
import java.awt.datatransfer.DataFlavor;
import javax.activation.*;


public class PlainDCH implements DataContentHandler {
    /**
     * return the DataFlavors for this <code>DataContentHandler</code>
     * @return The DataFlavors.
     */
    public DataFlavor[] getTransferDataFlavors() { // throws Exception;
	DataFlavor flavors[] = new DataFlavor[2];
	

	try {
	    flavors[0] = new ActivationDataFlavor(Class.forName("java.lang.String"),
					   "text/plain",
					   "text string");
	} catch(Exception e)
	    { System.out.println(e); }

	flavors[1] = new DataFlavor("text/plain", "Plain Text");
	return flavors;
    }
    /**
     * return the Transfer Data of type DataFlavor from InputStream
     * @param df The DataFlavor.
     * @param ins The InputStream corresponding to the data.
     * @return The constructed Object.
     */
    public Object getTransferData(DataFlavor df, DataSource ds) {
	
	// this is sort of hacky, but will work for the
	// sake of testing...
	if(df.getMimeType().equals("text/plain")) {
	    if(df.getRepresentationClass().getName().equals(
					       "java.lang.String")) {
		// spit out String
		StringBuffer buf = new StringBuffer();
		char data[] = new char[1024];
		// InputStream is = null;
		InputStreamReader isr = null;
		int bytes_read = 0;
		int total_bytes = 0;

		try {
		    isr = new InputStreamReader(ds.getInputStream());
		    
// 		    while(is.read(data) > 0)
// 			buf.append(data);

		    while(true){
			bytes_read = isr.read(data);
			if(bytes_read > 0)
			    buf.append(data, total_bytes, bytes_read);
			else
			    break;
			total_bytes += bytes_read;
		    } 
		} catch(Exception e) {}

		return buf.toString();
		
	    }
	    else if(df.getRepresentationClass().getName().equals(
					     "java.io.InputStream")){
		// spit out InputStream
		try {
		    return ds.getInputStream();
		} catch (Exception e) {}
	    }
		
	} 

	    return null;
    }
    
    /**
     *
     */
    public Object getContent(DataSource ds) { // throws Exception;
	StringBuffer buf = new StringBuffer();
	char data[] = new char[1024];
	// InputStream is = null;
	InputStreamReader isr = null;
	int bytes_read = 0;
	int total_bytes = 0;
	
	try {
	    isr = new InputStreamReader(ds.getInputStream());
	    
	    // 		    while(is.read(data) > 0)
	    // 			buf.append(data);
	    
	    while(true){
		bytes_read = isr.read(data);
		if(bytes_read > 0)
		    buf.append(data, total_bytes, bytes_read);
		else
		    break;
		total_bytes += bytes_read;
	    } 
	} catch(Exception e) {}
	
	return buf.toString();
    }
    /**
     * construct an object from a byte stream
     * (similar semantically to previous method, we are deciding
     *  which one to support)
     */
    public void writeTo(Object obj, String mimeTye, OutputStream os) 
	throws IOException {
	// throws Exception;
    }
    
}

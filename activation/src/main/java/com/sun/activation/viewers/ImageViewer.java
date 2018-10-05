/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.activation.viewers;

import java.awt.*;
import java.io.*;
import java.beans.*;
import javax.activation.*;

public class ImageViewer extends Panel implements CommandObject {
    // UI Vars...
    private ImageViewerCanvas canvas = null;
    
    // File Vars
    //    private InputStream data_ins = null;
    private Image image = null;
    private DataHandler _dh = null;
    
    private boolean DEBUG = false;
    /**
     * Constructor
     */
    public ImageViewer(){
	
	// create the ImageViewerCanvas
	canvas = new ImageViewerCanvas();
	add(canvas);
    }
    /**
     * Set the DataHandler for this CommandObject
     * @param dh the DataHandler
     */
    public void setCommandContext(String verb, DataHandler dh)	throws IOException{
	_dh = dh;
	this.setInputStream( _dh.getInputStream() );
    }
    //--------------------------------------------------------------------
    
    /**
     * Set the data stream, component to assume it is ready to
     * be read.
     */
    private void setInputStream(InputStream ins) throws IOException {
	MediaTracker mt = new MediaTracker(this);
	int bytes_read = 0;
	byte data[] = new byte[1024];
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	
	while((bytes_read = ins.read(data)) >0)
	    baos.write(data, 0, bytes_read);
	ins.close();
	
	// convert the buffer into an image
	image = getToolkit().createImage(baos.toByteArray());
	
	mt.addImage(image, 0);
	
	try {
	    mt.waitForID(0);
	    mt.waitForAll();
	    if(mt.statusID(0, true ) != MediaTracker.COMPLETE){
		System.out.println("Error occured in image loading = " +
				   mt.getErrorsID(0));
		
	    }
	    
	}
	catch(InterruptedException e) {
	    throw new IOException("Error reading image data");
	}
	
	canvas.setImage(image);
	if(DEBUG)
	    System.out.println("calling invalidate");
	
    }
    //--------------------------------------------------------------------
    public void addNotify(){
	super.addNotify(); // call the real one first...
	this.invalidate();
	this.validate();
	this.doLayout();
    }
    //--------------------------------------------------------------------
    public Dimension getPreferredSize(){
	return canvas.getPreferredSize();
    }

}












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
import java.lang.reflect.Method;
import java.io.*;
import java.awt.event.*;

/**
 * Class <code>CompViewer</code> creates a 'viewer' component
 * that implements the CommandObject interface.
 *
 */
public class CompViewer extends Frame implements WindowListener {

    /**
     * Our constructor...
     */
    public CompViewer(){
	super("Component");
	this.initCompViewer(null);
    }

    public CompViewer(String name){
	super(name);
	this.initCompViewer(name);
    }

    public void initCompViewer(String name){
	if (name != null)
	    setTitle(name);
	setSize(400,400);
	setLayout(new BorderLayout());
	this.addWindowListener(this);
    }
    
    ////////////////////////////////////////////////////////////////////////
    // we got our bean as a component display it!
    void setBean(Component new_bean)
	{
	    Dimension start_dim = null;
	    add((Component)new_bean, "Center");
	    start_dim =  ((Component)new_bean).getPreferredSize();
	    
	    if(start_dim.width != 0 && start_dim.height != 0) {
		// this is what we do under normal conditions
		start_dim.height += 40;
		start_dim.width += 15;
		this.setSize( start_dim );
		((Component)new_bean).invalidate(); 
		((Component)new_bean).validate();
		((Component)new_bean).doLayout();
		show();           
	    }
	    else {
		// we get here if for some reason our child's
		// getPref size needs to have it's peer created
		// first...
		show();           
		start_dim =  ((Component)new_bean).getPreferredSize();
		start_dim.height += 40;
		start_dim.width += 15;
		this.setSize( start_dim );
		((Component)new_bean).validate();
	    }
	    this.setSize(this.getSize());
	    validate();
	}


    /**
     * Make the bean based on it's class loader and name
     */
    private Object makeBean(ClassLoader cls, String beanName) {
	Object new_bean = null;

	try {
	    try {
		new_bean = java.beans.Beans.instantiate(cls, beanName);
	    }
	    catch(IOException e) {
		System.out.println("CompViewer:Beans.instantiate:IOException " + beanName + ".");
		System.out.println(e);
		System.exit(1);
	    }
	}
	catch (ClassNotFoundException e) {
	    System.out.println("CompViewer:Beans.instantiate:ClassNotFoundException " + beanName + "."); 
	    System.out.println(e);
	    System.exit(1);
	}
      
	if( !(new_bean instanceof Component) ) {
	    System.out.println("CompViewer: " + beanName + " not instance of awt.Component exiting");
	    System.exit(1);
	}
	return new_bean;
    }

    public void windowOpened(WindowEvent e) {}
    public void windowClosing(WindowEvent e) {
	this.setVisible(false);
    }
    public void windowClosed(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}

}




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

public class ImageViewerCanvas extends Canvas
{
  private Image canvas_image = null;
  
  /**
   * The constructor
   */
  public ImageViewerCanvas()
    {
	
    }

  /**
   * set the image
   * @param new_image the image
   */
  public void setImage(Image new_image)
    {
      canvas_image = new_image;
      this.invalidate();
      this.repaint();
    }
  
  /**
   * getPreferredSize
   */
  public Dimension getPreferredSize()
    {
      Dimension d = null;
      
      if(canvas_image == null)
	{
	  d = new Dimension(200, 200);
	}
      else
	d = new Dimension(canvas_image.getWidth(this), 
			  canvas_image.getHeight(this));

      return d;
    }
  /**
   * paint method
   */
  public void paint(Graphics g)
    {

      if(canvas_image != null)
	g.drawImage(canvas_image, 0, 0, this);

    }
  
}

/*
        Naked Objects - a framework that exposes behaviourally complete
        business objects directly to the user.
        Copyright (C) 2000 - 2003  Naked Objects Group Ltd

        This program is free software; you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation; either version 2 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program; if not, write to the Free Software
        Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

        The authors can be contacted via www.nakedobjects.org (the
        registered address of Naked Objects Group is Kingsway House, 123 Goldworth
        Road, Woking GU21 1NR, UK).
*/
package org.nakedobjects.viewer.skylark.util;

import java.awt.Canvas;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.awt.image.RGBImageFilter;

import org.apache.log4j.Logger;
import org.nakedobjects.viewer.skylark.Color;
import org.nakedobjects.viewer.skylark.Icon;



class ImageTemplate {
    private final static Logger LOG = Logger.getLogger(ImageTemplate.class);
    private Image image;
    private MediaTracker mt = new MediaTracker(new Canvas());

    public ImageTemplate(Image image) {
		if(image == null) {
			throw new NullPointerException();
		}
        this.image = image;
    }

    public Icon getFullSizeImage() {
        return new Icon(image);
    }

    public Icon getIcon(int height, Color tint) {
        Image iconImage;

        if (height == image.getHeight(null)) {
            iconImage = image;
        } else {
            iconImage = image.getScaledInstance(-1, height, Image.SCALE_SMOOTH);

            if (iconImage != null) {
                mt.addImage(iconImage, 0);

                try {
                    mt.waitForAll();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (mt.isErrorAny()) {
                    LOG.error("Failed to create scaled image: " + iconImage + " " +
                        mt.getErrorsAny()[0]);
                    mt.removeImage(iconImage);
                    iconImage = null;
                } else {
                    mt.removeImage(iconImage);
                    LOG.info("Image " + iconImage + " scaled to " + height);
                }
            }

            if (iconImage == null || iconImage.getWidth(null) == -1) {
                throw new RuntimeException("scaled image! " + iconImage.toString());
            }
        }

        if(tint != null) {
        	LOG.debug("tinting image " + tint);
        	Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(iconImage.getSource(), new Filter()));
        }
        
        return new Icon(iconImage);
    }
    
    private class Filter extends RGBImageFilter {

		public int filterRGB(int x, int y, int rgb) {
			return 0xFFFFFF - rgb;
		}
    }
}

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
package org.nakedobjects.viewer.skylark.value;

import org.nakedobjects.object.InvalidEntryException;
import org.nakedobjects.object.Naked;
import org.nakedobjects.object.reflect.Value;
import org.nakedobjects.object.value.Logical;
import org.nakedobjects.utility.NotImplementedException;
import org.nakedobjects.viewer.skylark.Canvas;
import org.nakedobjects.viewer.skylark.Click;
import org.nakedobjects.viewer.skylark.Color;
import org.nakedobjects.viewer.skylark.Content;
import org.nakedobjects.viewer.skylark.Location;
import org.nakedobjects.viewer.skylark.ObjectContent;
import org.nakedobjects.viewer.skylark.Size;
import org.nakedobjects.viewer.skylark.Style;
import org.nakedobjects.viewer.skylark.ValueContent;
import org.nakedobjects.viewer.skylark.View;
import org.nakedobjects.viewer.skylark.ViewAxis;
import org.nakedobjects.viewer.skylark.ViewSpecification;
import org.nakedobjects.viewer.skylark.core.AbstractFieldSpecification;


public class ColorField extends AbstractField {

	public static class Specification extends AbstractFieldSpecification {
	    public String getName() {
	        return "Color";
	    }

	    public View createView(Content content, ViewAxis axis) {
			return new ColorField(content, this, axis);
	    }
	    
	    public boolean canDisplay(Naked object) {
	    	return object instanceof org.nakedobjects.object.value.Color;
		}
	}
	
    public ColorField(Content content, ViewSpecification specification, ViewAxis axis) {
        super(content, specification, axis);
    }

    public void draw(Canvas canvas) {
        Color color;

        if (hasFocus()) {
            color = Style.PRIMARY1;
        } else if (getParent().getState().isObjectIdentified()) {
            color = Style.IDENTIFIED;
        } else if (getParent().getState().isRootViewIdentified()) {
            color = Style.PRIMARY2;
        } else {
            color = Style.SECONDARY1;
        }

    	int top = 0;
    	int left = 0;

    	Size size = getSize();
    	int w = size.getWidth() - 1;
    	int h = size.getHeight() - 1;
    	canvas.drawRectangle(left, top, w, h, color);
     	left++;
    	top++;
    	w -= 1;
    	h -= 1;
        canvas.drawSolidRectangle(left, top, w, h, new Color(getColor().intValue()));
    }

    public void firstClick(Click click) {
        View overlay = new ColorFieldOverlay(this);
        Location location = getView().getLocationWithinViewer();
        location.move(10, 10);
		overlay.setLocation(location);
        overlay.setSize(overlay.getRequiredSize());
        overlay.markDamaged();
        getViewManager().setOverlayView(overlay);
    }

    public int getBaseline() {
        return VPADDING + Style.NORMAL.getAscent();
    }

    public Size getRequiredSize() {
		Size size = super.getRequiredSize();
		size.extendWidth(45);
        return size; 
  }

    org.nakedobjects.object.value.Color getColor() {
        ValueContent content = ((ValueContent) getContent());
        org.nakedobjects.object.value.Color value = (org.nakedobjects.object.value.Color) content.getValue();

        return value;
    }

    public void refresh() {
        ValueContent content = ((ValueContent) getContent());
        Value field = content.getValueField();

        if (field.isDerived()) {
            content.getValue().copyObject((Logical) field.get(
                    ((ObjectContent) getParent().getContent()).getObject()));
        }
    }

	void setColor(int color) {
		try {
            set("" + color);
        } catch (InvalidEntryException e) {
            throw new NotImplementedException();
        }
	}
    
    
}

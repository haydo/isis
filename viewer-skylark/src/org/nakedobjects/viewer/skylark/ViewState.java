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
package org.nakedobjects.viewer.skylark;

public class ViewState implements Cloneable {
    private static final byte CAN_DROP = 0x10;
    private static final byte CANT_DROP = 0x20;
    private static final byte OBJECT_IDENTIFIED = 0x04;
    private static final byte ROOT_VIEW_IDENTIFIED = 0x01;
    private static final byte VIEW_IDENTIFIED = 0x02;
    private byte state;

    public void setCanDrop() {
        state |= CAN_DROP;
    }

    public void setCantDrop() {
        state |= CANT_DROP;
    }

    public void setObjectIdentified() {
        state |= OBJECT_IDENTIFIED;
    }

    public boolean isObjectIdentified() {
        return (state & OBJECT_IDENTIFIED) > 0;
    }

    public void setRootViewIdentified() {
        state |= ROOT_VIEW_IDENTIFIED;
    }

    public boolean isRootViewIdentified() {
        return (state & ROOT_VIEW_IDENTIFIED) > 0;
    }

    public void setViewIdentified() {
        state |= VIEW_IDENTIFIED;
    }

    public boolean isViewIdentified() {
        return (state & VIEW_IDENTIFIED) > 0;
    }

    public boolean canDrop() {
        return (state & CAN_DROP) > 0;
    }

    public boolean cantDrop() {
        return (state & CANT_DROP) > 0;
    }

    public void clearObjectIdentified() {
        state &= ~(OBJECT_IDENTIFIED | CAN_DROP | CANT_DROP);
    }

    public void clearRootViewIdentified() {
        state &= ~ROOT_VIEW_IDENTIFIED;
    }

    public void clearViewIdentified() {
        state &= ~(VIEW_IDENTIFIED | OBJECT_IDENTIFIED | CAN_DROP | CANT_DROP);
    }

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    public String toString() {
    	String str = isObjectIdentified() ? "Object-Identified " : "";
    	str += isViewIdentified() ? "View-identified " : "";
    	str += isRootViewIdentified() ? "Root-view-identified " : "";
    	str += canDrop() ? "Can-drop " : "";
    	str += cantDrop() ? "Cant-drop " : "";
		return str;
	}
}

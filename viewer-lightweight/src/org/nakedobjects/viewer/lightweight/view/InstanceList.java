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

package org.nakedobjects.viewer.lightweight.view;

import org.nakedobjects.object.NakedClass;
import org.nakedobjects.object.NakedObject;
import org.nakedobjects.object.NakedObjectManager;
import org.nakedobjects.object.collection.TypedCollection;
import org.nakedobjects.viewer.lightweight.ObjectView;
import org.nakedobjects.viewer.lightweight.RootView;
import org.nakedobjects.viewer.lightweight.View;

import java.util.Vector;


public class InstanceList extends StandardList implements RootView {
	
	public InstanceList() {
		setBorder(new RootBorder());
	}
	
	public NakedClass forNakedClass() {
        String name = ((TypedCollection) getObject()).getType().getName();
        return NakedObjectManager.getInstance().getNakedClass(name);
    }

	
	public String getName() {
		return "InstanceList";
	}
	
	/**
	 * Remove view of a deleted instance
	 */
	public void removeViewsFor(NakedObject object, Vector toRemove) {
		View[] views = getComponents();

		for (int i = 0; i < views.length; i++) {
			if(((ObjectView)views[i]).getObject().equals(object))
			{
				toRemove.addElement(views[i]);
				invalidateLayout();
				break;
			}
		}
	}


}

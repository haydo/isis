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


import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import org.apache.log4j.Logger;
import org.nakedobjects.object.ValueParseException;
import org.nakedobjects.object.control.Allow;
import org.nakedobjects.object.control.Permission;
import org.nakedobjects.object.control.Veto;
import org.nakedobjects.viewer.skylark.Location;
import org.nakedobjects.viewer.skylark.View;
import org.nakedobjects.viewer.skylark.Workspace;


public class PasteValueOption extends AbstractValueOption {
	private static final Logger LOG = Logger.getLogger(PasteValueOption.class);
	
    public PasteValueOption() {
        super("Replace with clipboard value");
    }

    public Permission disabled(View view) {

		if (!view.canChangeValue()) {
			return new Veto("Field cannot be edited");
		} else {
			return new Allow("Replace field content with '" + getClipboard() + "' from clipboard");
		}
    }

    public void execute(Workspace frame, View view, Location at) {
		String value = getClipboard();
    	try {
			getValue(view).parse(value);
			updateParent(view);
		} catch (ValueParseException e) {
			LOG.error("Invalid paste value " + e);
		}
    }

	private String getClipboard() {
		Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		 Transferable content = cb.getContents(this);
		
		String value = "illegal value";
		 try {
			 value = ((String) content.getTransferData(DataFlavor.stringFlavor));
		 } catch (Throwable e) {
			 LOG.error("Invalid clipboard operation " + e);
		 }
		return value;
	}

    public String toString() {
        return "PasteValueOption";
    }
}

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
package org.nakedobjects.viewer.lightweight;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class ApplicationFrame extends Frame {
    private Viewer viewingMechanism;

    /**
     * Creates a bordered frame with specified text as the title.
     * @see java.awt.Frame#Frame(String)
     */
    public ApplicationFrame(Viewer viewManager, String title) {
        super(title);
        this.viewingMechanism = viewManager;

        addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    ApplicationFrame.this.viewingMechanism.shutdown();
                }
            });

        setBackground(Style.APPLICATION_BACKGROUND.getAwtColor());

        /*
         * compensate for change in tab handling in Java 1.4
         */
        try {
            Class c = getClass();
            Method m = c.getMethod("setFocusTraversalKeysEnabled", new Class[] { Boolean.TYPE });
            m.invoke(this, new Object[] { Boolean.FALSE });
        } catch (SecurityException e1) {
            e1.printStackTrace();
        } catch (NoSuchMethodException ignore) {
            ;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Calls <code>update()</code> to do double-buffered drawing of all views.
     *
     * @see #update(Graphics)
     * @see java.awt.Component#paint(Graphics)
     */
    public final void paint(Graphics g) {
        update(g);
    }

    /**
     * Paints the double-buffered image.  Calls the <code>draw()</code> method
     * on each top-level view.
     *
     * @see java.awt.Component#update(Graphics)
     */
    public void update(Graphics g) {
        viewingMechanism.paint(g);
    }
}

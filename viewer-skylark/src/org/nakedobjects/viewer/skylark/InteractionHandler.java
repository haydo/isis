package org.nakedobjects.viewer.skylark;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import org.apache.log4j.Logger;


public class InteractionHandler implements MouseMotionListener, MouseListener, KeyListener {
	    private static final Logger LOG = Logger.getLogger(InteractionHandler.class);
	    private Drag drag;

	    /*
	     * The location within the frame where the mouse button was pressed down.
	     */
	    private Location downAt;
	    private Location mouseLocation;
	    private final Viewer viewer;
	    private boolean canDrag;
		private View identifiedView;


	    InteractionHandler(Viewer topView) {
	        this.viewer = topView;
	    }

	    /**
	     * Listener for key presses.  Cancels popup and drags, and forwards key
	     * presses to the view that has the keyboard focus.
	     * @see java.awt.event.KeyListener#keyPressed(KeyEvent)
	     */
	    public void keyPressed(KeyEvent ke) {
	        if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
	        	if(drag != null) {
	        		drag.cancel();
		            drag = null;
	        	}
	            viewer.clearOverlayView();
	        }
	        
	    	View keyboardFocus = viewer.getFocus();

	        if (keyboardFocus != null) {
	            keyboardFocus.keyPressed(ke.getKeyCode(), ke.getModifiers());
	        }
	        
	        redraw();
	    }

	    /**
	     * Listener for key releases and forward them to the view that has the
	     * keyboard focus.
	     * @see java.awt.event.KeyListener#keyReleased(KeyEvent)
	     */
	    public void keyReleased(KeyEvent ke) {
	    	View keyboardFocus = viewer.getFocus();

	    	if (keyboardFocus != null) {
	            keyboardFocus.keyReleased(ke.getKeyCode(), ke.getModifiers());
	        }
	        redraw();
	    }

	    /**
	     * Listener for key press, and subsequent release, and forward it as one
	     * event to the view that has the keyboard focus.
	     * @see java.awt.event.KeyListener#keyTyped(KeyEvent)
	     */
	    public void keyTyped(KeyEvent ke) {
	    	View keyboardFocus = viewer.getFocus();
	        if (keyboardFocus != null) {
	            if (!ke.isActionKey() && !Character.isISOControl(ke.getKeyChar())) {
	                keyboardFocus.keyTyped(ke.getKeyChar());
	            }
	        }
	        redraw();
	    }

	    /**
	     * Responds to mouse click events by calling <code>firstClick</code>,
	     * <code>secondClick</code>, and <code>thirdClick</code> on the view that
	     * the mouse is over.  Ignored if the mouse is not over a view.
	     *
	     * @see java.awt.event.MouseListener#mouseClicked(MouseEvent)
	     */
	    public void mouseClicked(MouseEvent me) {
	        viewer.translate(me);
	        Location mouseLocation = new Location(me.getPoint());
			View over = viewer.identifyView(mouseLocation, true);
	        if(over != null) {
				Location pointer = new Location(mouseLocation);
				Location pointerInView = new Location(mouseLocation);
				Location offset = Viewer.absoluteLocation(over);
				pointerInView.move(-offset.x, -offset.y);
//		    	LOG.debug("mouse clicked at " + pointer + " " + me.getPoint());

		        Click click = new Click(over, pointerInView, pointer, me.getModifiers());
		        LOG.debug(click);
		        
		        if (click.isButton3()) {
		            saveCurrentFieldEntry();
		            if (over != null) {
		    	        LOG.debug(" popup " + mouseLocation + " over " + over);
		    	        viewer.popupMenu(click, over);
			        	identifiedView = over;
			        }

		        } else if (over != null) {
		            if (me.getClickCount() == 1) {
		                over.firstClick(click);
		            } else if (me.getClickCount() == 2) {
		                over.secondClick(click);
		            } else if (me.getClickCount() == 3) {
		                over.thirdClick(click);
		            }
		        }
		        redraw();
	        }
	    }

	    /**
	     * Responds to mouse dragged according to the button used. If the left
	     * button then identified view is moved.
	     *
	     * @see java.awt.event.MouseMotionListener#mouseDragged(MouseEvent)
	     */
	    public void mouseDragged(MouseEvent me) {
	        viewer.translate(me);
	        mouseLocation = new Location(me.getPoint());

	        if (canDrag) { // checked to ensure that dragging over a view doesn't start a drag - it should only start when already over a view.

	            if (drag == null) { // no drag in progress yet
	                dragStart(me);
	                redraw();
	            } else {
	                drag(me);
	               redraw();
	            }
	        }
	    }

	    private void drag(MouseEvent me) {
		    View target = viewer.identifyView(new Location(me.getPoint()), false);
			drag.updateLocationWithinViewer(target, new Location(me.getPoint()));
			drag.drag();
		    identifiedView = target;
		    
			if(viewer.getOverlayView() == target) {
				LOG.error("drag identified over overlay!!!! " + target);
			}

		}


		private void dragStart(MouseEvent me) {
			Location relative = new Location(downAt);
			View identified = identifiedView;
		    LOG.debug("down at " + downAt);
			Location viewLocation = Viewer.absoluteLocation(identified);
		    LOG.debug("identified " +viewLocation);
			relative.move(-viewLocation.x, -viewLocation.y);
		    LOG.debug("relative " + relative);
			
			ViewAreaType type = identified.viewAreaType(relative);
			
			
			if (type == ViewAreaType.INTERNAL) {
			    drag = InternalDrag.create(identified, downAt, relative, me.getModifiers());
			    LOG.debug("drag from " + drag);
			} else {
			    saveCurrentFieldEntry();

			    LOG.debug("pickup " + type + ": " + identified);

			    if (type == ViewAreaType.VIEW) {
			    	drag = ViewDrag.create(identified, downAt, relative, me.getModifiers());
			    } else {
			        drag = ContentDrag.create(identified, downAt, relative, me.getModifiers());
			    }
			}
	        if(drag == null) {	
	        	// TODO this look unnecessary
	        	canDrag = false;
	        }
		}


		/**
	     * event ignored
	     * @see java.awt.event.MouseListener#mouseEntered(MouseEvent)
	     */
	    public void mouseEntered(MouseEvent arg0) {
	    }

	    /**
	     * event ignored
	     * @see java.awt.event.MouseListener#mouseExited(MouseEvent)
	     */
	    public void mouseExited(MouseEvent arg0) {
	    }

	    /**
	     * responds to mouse moved event by setting the view found underneath the
	     * mouse as the idetified view.  Views normally respond by changing the
	     * colour of themselves so they are visual distinct and hence shows itself
	     * as special compared to the rest.
	     *
	     * @see java.awt.event.MouseMotionListener#mouseMoved(MouseEvent)
	     */
	    public void mouseMoved(MouseEvent me) {
	        if (drag == null) {
		        viewer.translate(me);
		        mouseLocation = new Location(me.getPoint());

		        
		        View mouseOver = viewer.identifyView(mouseLocation, true);
		        
		        if(mouseOver != null) {
		        	
		        	if(identifiedView == null) {
//		        		LOG.debug("no current identified view");
		        		identifiedView = mouseOver;
		        	} else {
		        		if (mouseOver != identifiedView) {
		        			
		        			if (mouseOver == identifiedView) {
		        				LOG.debug("moved into subview from " + identifiedView);
		        				identifiedView.enteredSubview();
		        			} else {
		        				LOG.debug("exited " + identifiedView);
		        				identifiedView.exited();
		        			}
		        			
		        			View previouslyIdentified = identifiedView;
		        			identifiedView = mouseOver;
		        			
		        			if(mouseOver != null) {
		        				if (mouseOver == previouslyIdentified) {
		        					LOG.debug("moved back to from " + previouslyIdentified);
		        					mouseOver.exitedSubview();
		        				} else {
		        					LOG.debug("entered " + mouseOver);
		        					mouseOver.entered();
		        				}
		        			}
		        			redraw();
		        		}
		        	}
		        	
		        	Location pointer = mouseLocation;
		        	Location offset = Viewer.absoluteLocation(mouseOver);
		        	pointer.move(-offset.x, -offset.y);
		        	//		    	LOG.debug("mouse moved to " + pointer + " " + me.getPoint());
		        	mouseOver.mouseMoved(pointer);
		        	
		        	redraw();
		        }
	        }
	    }



		/**
	     * Responds to the mouse pressed event (with the left button pressed) by
	     * initiating a drag.  This sets up the <code>View</code>'s dragging state
	     * to the view that the mouse was over when the button was pressed.
	     *
	     * @see java.awt.event.MouseListener#mousePressed(MouseEvent)
	     */
	    public void mousePressed(MouseEvent me) {
	        viewer.translate(me);
	        drag = null;
	        downAt = new Location(me.getPoint());

	        // hide an overlay view when not being pointed to
	        View identified = viewer.identifyView(downAt, true);
	        if(viewer.getOverlayView() != identified) {
	        	viewer.disposeOverlayView();
	        }

            viewer.makeFocus(identified);

	        canDrag = identified != null && me.getClickCount() == 1; // drag should not be valid after double/triple click
	        identifiedView = identified;
	    }

		/**
	     * Repsonds to the mouse released event (with the left button pressed) by
	     * telling the identified view (the drop zone) that the dragged object is
	     * being dropped on it (via the views <code>drop</code> method).  If the
	     * drop takes place outside of all of the other views then the
	     * <code>workspaceDrop</code> method is called instead to indicate a drop onto the
	     * workspace.
	     *
	     * @see java.awt.event.MouseListener#mouseReleased(MouseEvent)
	     */
	    public void mouseReleased(MouseEvent me) {
	        if (drag != null) {
		        viewer.translate(me);
	            View target = identifiedView;

	            Location location = new Location(me.getPoint());
				drag.updateLocationWithinViewer(target, location);
				LOG.debug("drag ended at " + location + " over " + target);
	            drag.end();

	            viewer.disposeOverlayView();

	            drag = null;
	            
	            redraw();
	        }
	    }

	    public void saveCurrentFieldEntry() {
	    	View keyboardFocus = viewer.getFocus();

	        if (keyboardFocus != null) {
	            View focus = keyboardFocus;
	            keyboardFocus = null;
	            focus.editComplete();
	            focus.markDamaged();
	        }
	    }

	    private void redraw() {
	    	viewer.repaint();
	    }


}


/*
Naked Objects - a framework that exposes behaviourally complete
business objects directly to the user.
Copyright (C) 2000 - 2004  Naked Objects Group Ltd

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

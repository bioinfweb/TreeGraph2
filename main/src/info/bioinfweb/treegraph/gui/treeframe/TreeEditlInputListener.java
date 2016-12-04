/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2016  Ben Stöver, Sarah Wiechers, Kai Müller
 * <http://treegraph.bioinfweb.info/>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.treegraph.gui.treeframe;


import info.bioinfweb.treegraph.document.*;
import info.bioinfweb.treegraph.document.format.DistanceValue;
import info.bioinfweb.treegraph.graphics.positionpaint.PositionPaintFactory;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.commons.SystemUtils;

import java.awt.event.*;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;



/**
 * This listener implements the reactions to user inputs for editing the tree in a
 * tree window.
 * @author Ben St&ouml;ver
 */
class TreeEditlInputListener extends MouseAdapter 
    implements MouseListener, KeyListener, MouseWheelListener {
	
	public static final float ZOOM_PER_CLICK = 0.1f;
	
	
	protected TreeViewPanel owner = null;
	

	public TreeEditlInputListener(TreeViewPanel owner) {
	  this.owner = owner;	
	}
	
	
	private ConcretePaintableElement getElementByPosition(MouseEvent e) {
		return PositionPaintFactory.getInstance().getPositioner(owner.getPainterType()).elementToPosition(
				owner.getDocument(), 
				DistanceValue.pixelsToMillimeters(e.getX(), owner.pixelsPerMillimeter()), 
				DistanceValue.pixelsToMillimeters(e.getY(),	owner.pixelsPerMillimeter()),
				DistanceValue.pixelsToMillimeters(TreeViewPanel.SELECTION_MARGIN, owner.pixelsPerMillimeter()));
	}
	
	
	protected void doubleClick(MouseEvent e, ConcretePaintableElement element) {
		if (element != null) {
			if ((element instanceof TextElement) && !((element instanceof Node) && !(((Node)element).isLeaf()))) {
  			MainFrame.getInstance().getActionManagement().get("edit.editText").
				    actionPerformed(new ActionEvent(this, 0, ""));  //TODO id = 0 ein sinnvoller Wert?
			}
			else {
				MainFrame.getInstance().getActionManagement().get("format.editElementFormats").
				    actionPerformed(new ActionEvent(this, 0, ""));  //TODO id = 0 ein sinnvoller Wert?
			}
		}
	}
	
	
	private void checkPopup(MouseEvent e, ConcretePaintableElement selected) {
		if (e.isPopupTrigger()) {
			JPopupMenu menu = MainFrame.getInstance().getActionManagement().getPopupMenu(
					selected);
			if (menu != null) {
				menu.show(owner, e.getX(), e.getY());
			}
		}
	}
	
	
	@Override
	public void mousePressed(MouseEvent e) {
		owner.requestFocusInWindow();
		ConcretePaintableElement selected = getElementByPosition(e);
		if ((!e.isPopupTrigger()) && (e.getClickCount() > 1)) { 
			doubleClick(e, selected);
		}
		else {
			if (e.getButton() == MouseEvent.BUTTON3) {  //TODO Besser w�re auch eine plattformunabh�ngige L�sung �ber e.getPopupTrigger und mouseReleased + mousePressed
				if (!owner.getSelection().contains(selected)) {
					if ((e.isMetaDown() && SystemUtils.IS_OS_MAC) || (e.isControlDown()&&  !SystemUtils.IS_OS_MAC)) {
  	  			owner.getSelection().add(selected);
					}
					else {
						owner.getSelection().set(selected);
					}
  			}
				// sonst Markierung unver�ndert lassen
			}
			else if (e.getButton() == MouseEvent.BUTTON1) {
				if ((e.isMetaDown() && SystemUtils.IS_OS_MAC) || (e.isControlDown() &&  !SystemUtils.IS_OS_MAC)) {
					if (selected != null) {
						if (owner.getSelection().contains(selected)) {
							owner.getSelection().remove(selected);
						}
						else {
							owner.getSelection().add(selected);
						}
					}
				}
				else {
					owner.getSelection().set(selected);
				}
			}
			// Wurde au�erhalb eines Elements geklickt, ist die Markierung null (= keine Element).
		}
		checkPopup(e, selected);  // Muss wg. Plattformunabh�ngigkeit bei mousePressed und mouseReleased erfolgen
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		checkPopup(e, getElementByPosition(e));  // Muss wg. Plattformunabh�ngigkeit bei mousePressed und mouseReleased erfolgen
	}


	public void keyPressed(KeyEvent e) {
		if (!owner.getSelection().isEmpty() && 
				!((e.isMetaDown() && SystemUtils.IS_OS_MAC) || (e.isControlDown() && !SystemUtils.IS_OS_MAC))) {
			if (!e.isShiftDown()) {
		  	if (owner.getSelection().first() instanceof Node) {
		  		Node selected = (Node)owner.getSelection().first();
		  		switch (e.getKeyCode()) {
					  case KeyEvent.VK_RIGHT: case KeyEvent.VK_NUMPAD6:
					  	if (!selected.getChildren().isEmpty()) {
					  		owner.getSelection().set(selected.getChildren().get(0).getAfferentBranch());
					  	}
						  break;			
					  case KeyEvent.VK_LEFT: case KeyEvent.VK_NUMPAD4:
					  	if (selected.hasParent() || owner.getDocument().getTree().getFormats().getShowRooted()) {
					  		owner.getSelection().set(selected.getAfferentBranch());
					  	}
						  break;			
					  case KeyEvent.VK_UP: case KeyEvent.VK_NUMPAD8: 
					  	if (!selected.isFirst()) {
					  		owner.getSelection().set(selected.getPrevious());
					  	}
					  	break;
					  case KeyEvent.VK_DOWN: case KeyEvent.VK_NUMPAD2:
					  	if (!selected.isLast()) {
					  		owner.getSelection().set(selected.getNext());
					  	}
					  	break;
					}
		  	}
		  	else if (owner.getSelection().first() instanceof Branch) {
		  		Branch selected = (Branch)owner.getSelection().first();
		  		switch (e.getKeyCode()) {
					  case KeyEvent.VK_RIGHT: case KeyEvent.VK_NUMPAD6:
				  		owner.getSelection().set(selected.getTargetNode());
						  break;			
					  case KeyEvent.VK_LEFT: case KeyEvent.VK_NUMPAD4:
					  	if (selected.getTargetNode().hasParent()) {
					  		owner.getSelection().set(selected.getTargetNode().getParent());
			  	  	}
						  break;
					  case KeyEvent.VK_UP: case KeyEvent.VK_NUMPAD8:
					  	Labels labels = selected.getLabels();
					  	if (labels.lineCount(true) > 0) {
					  		owner.getSelection().set(labels.getAbove(true, -1, 0));
					  	}
					  	break;
					  case KeyEvent.VK_DOWN: case KeyEvent.VK_NUMPAD2:
					  	labels = selected.getLabels();
					  	if (labels.lineCount(false) > 0) {
					  	  owner.getSelection().set(labels.getBelow(false, -1, 0));
					  	}
					  	break;
					}
		  	}
		  	else if (owner.getSelection().first() instanceof Label) {
		  		Label currentSelection = (Label)owner.getSelection().first();
		  		ConcretePaintableElement newSelection = null;
		  		switch (e.getKeyCode()) {
					  case KeyEvent.VK_RIGHT: case KeyEvent.VK_NUMPAD6:
					  	newSelection = currentSelection.getNext();
						  break;			
					  case KeyEvent.VK_LEFT: case KeyEvent.VK_NUMPAD4:
					  	newSelection = currentSelection.getPrevious();
						  break;
					  case KeyEvent.VK_UP: case KeyEvent.VK_NUMPAD8:
					  	newSelection = currentSelection.getAbove();
					  	if ((newSelection == null) && !currentSelection.isAbove()) {
					  		newSelection = currentSelection.getHoldingBranch();
					  	}
					  	break;
					  case KeyEvent.VK_DOWN: case KeyEvent.VK_NUMPAD2:
					  	newSelection = currentSelection.getBelow();
					  	if ((newSelection == null) && currentSelection.isAbove()) {
					  		newSelection = currentSelection.getHoldingBranch();
					  	}
					  	break;
					}
		  		if (newSelection != null) {
		  			owner.getSelection().set(newSelection);
		  		}
		  	}
			}
			else {
		  	if (owner.getSelection().first() instanceof Node) {
		  		Node selected = (Node)owner.getSelection().first();
		  		Node newSelection = null;
		  		switch (e.getKeyCode()) {
					  case KeyEvent.VK_UP: case KeyEvent.VK_NUMPAD8:
					  	newSelection = selected.getPreviousLeaf();
					  	break;
					  case KeyEvent.VK_DOWN: case KeyEvent.VK_NUMPAD2:
					  	newSelection = selected.getNextLeaf();
					  	break;
					}
		  		if (newSelection != null) {
		  			owner.getSelection().set(newSelection);
		  		}
	      }
  		}
		}
	}

	
	public void keyReleased(KeyEvent e) {}

	
	public void keyTyped(KeyEvent e) {}


	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if ((e.isMetaDown() && SystemUtils.IS_OS_MAC) || (e.isControlDown()&&  !SystemUtils.IS_OS_MAC)) {
			owner.setZoom(owner.getZoom() - (float)e.getWheelRotation() * ZOOM_PER_CLICK);
		}
		else if (owner.getParent() != null) {
			owner.getParent().dispatchEvent(SwingUtilities.convertMouseEvent(owner, e, owner.getParent()));  // Forward to JScrollPane to scroll.
		}
	}
}
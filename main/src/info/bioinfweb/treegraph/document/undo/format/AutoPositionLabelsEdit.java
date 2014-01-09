/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2014  Ben Stöver, Kai Müller
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
package info.bioinfweb.treegraph.document.undo.format;


import java.util.Vector;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.IDManager;
import info.bioinfweb.treegraph.document.Label;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TreeSerializer;
import info.bioinfweb.treegraph.document.format.LabelFormats;
import info.bioinfweb.treegraph.document.undo.DocumentEdit;



/**
 * @author Ben St&ouml;ver
 * @since 2.0.35
 */
public class AutoPositionLabelsEdit extends DocumentEdit {
	public static final double LABEL_BLOCK_ASPECT_RATIO = 2;  // doppelt so breit wie hoch 
	public static final int LINE_POSITION_STEP = 10;  // doppelt so breit wie hoch
	
	
	private Branch[] branches;
	private String[] ids;
	private Vector<LabelFormats>[] oldFormats;
		
	
  public AutoPositionLabelsEdit(Document document, Branch[] branches) {
		super(document);
		this.branches = branches;
  	ids = IDManager.getLabelIDs(document.getTree().getPaintStart(), Label.class);
  	backupFormats();
	}
  
  
  private void backupFormats() {
  	oldFormats = new Vector[ids.length];
  	for (int i = 0; i < oldFormats.length; i++) {
			oldFormats[i] = new Vector<LabelFormats>();
		}
  	
  	for (int i = 0; i < branches.length; i++) {
    	for (int j = 0; j < ids.length; j++) {
  			Label l = branches[i].getLabels().get(ids[j]);
  			if (l != null) {
  				oldFormats[j].add(l.getFormats().clone());
  			}
  			else {
  				oldFormats[j].add(null);
  			}
  		}
		}
  }
  
  
  private void restoreFormats() {
  	for (int i = 0; i < branches.length; i++) {
			for (int j = 0; j < ids.length; j++) {
				Label l = branches[i].getLabels().get(ids[j]);
				if (l != null) {
					l.getFormats().assignLabelFormats(oldFormats[j].get(i));
				}
			}
  	}
  }
  
  
  private static void position(Branch branch, String[] ids) {
  	int labelsPerBlock = ids.length / 2 + ids.length % 2;
  	int labelsPerLine = Math.round(labelsPerBlock / 
  			(float)Math.sqrt(labelsPerBlock / LABEL_BLOCK_ASPECT_RATIO));
  	
  	// Upper block:
  	int pos = 0;
  	while (pos < labelsPerBlock) {
  		Label l = branch.getLabels().get(ids[pos]);
  		if (l != null) {
  			LabelFormats f = l.getFormats();
 			  f.setAbove(true);
 			  f.setLineNumber(pos / labelsPerLine);
 			  f.setLinePosition((pos % labelsPerLine) * LINE_POSITION_STEP);  // reinsert() wurde bis hier drei mal aufgerufen
  		}
  		pos++;
  	}
  	
  	// Lower block:
  	while (pos < ids.length) {
  		Label l = branch.getLabels().get(ids[pos]);
  		if (l != null) {
  			LabelFormats f = l.getFormats();
 			  f.setAbove(false);
 			  int localPos = pos - labelsPerBlock;
 			  f.setLineNumber(localPos / labelsPerLine);
 			  f.setLinePosition((localPos % labelsPerLine) * LINE_POSITION_STEP);
  		}
  		pos++;
  	}
  }


	public static void position(Node root) {
  	String[] ids = IDManager.getLabelIDs(root, Label.class);
  	Branch[] branches = TreeSerializer.getElementsInSubtree(root, false, Branch.class);
  	for (int i = 0; i < branches.length; i++) {
    	position(branches[i], ids);
		}
  }


	@Override
	public void redo() throws CannotRedoException {
		for (int i = 0; i < branches.length; i++) {
			position(branches[i], ids);
		}
		super.redo();
	}


	@Override
	public void undo() throws CannotUndoException {
		restoreFormats();
		super.undo();
	}


	public String getPresentationName() {
		return "Automatically position labels";
	}
}
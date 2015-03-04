/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2015  Ben Stöver, Kai Müller
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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
import info.bioinfweb.treegraph.document.undo.file.AddSupportValuesEdit;



/**
 * @author Ben St&ouml;ver
 * @since 2.0.35
 */
public class AutoPositionLabelsEdit extends DocumentEdit {
	public static final double LABEL_BLOCK_ASPECT_RATIO = 2;  // doppelt so breit wie hoch 
	public static final int LINE_POSITION_STEP = 10;  // doppelt so breit wie hoch
	
	
	private Branch[] branches;
	private List<String> ids;
	private boolean equalSupportConflictPosition;
	private Vector<LabelFormats>[] oldFormats;
		
	
  public AutoPositionLabelsEdit(Document document, Branch[] branches, boolean equalSupportConflictPosition) {
		super(document);
		this.branches = branches;
		this.equalSupportConflictPosition = equalSupportConflictPosition;
		ids = readIDs(document.getTree().getPaintStart(), equalSupportConflictPosition);
  	backupFormats();
	}
  
  
  private static String conflictIDBySupportID(String id) {
  	return id.replace(AddSupportValuesEdit.SUPPORT_NAME, AddSupportValuesEdit.CONFLICT_NAME);
  }
  
  
  private static List<String> readIDs(Node root, boolean equalSupportConflictPosition) {
  	// Create list of all IDs:
  	List<String> result = new ArrayList<String>(Arrays.asList(IDManager.getLabelIDs(root, Label.class)));
  	Collections.sort(result);  // Position labels in alphabetical order.
  	
  	if (equalSupportConflictPosition) {
  		// Search support IDs:
  		List<String> supportIDs = new ArrayList<String>();
  		for (String id : result) {
				if (id.endsWith(AddSupportValuesEdit.SUPPORT_NAME)) {
					supportIDs.add(id);
				}
			}
  		
  		// Remove conflict IDs which have an according support ID:
  		for (String supportID : supportIDs) {
  			result.remove(conflictIDBySupportID(supportID));
			}
  	}
  	return result;
  }
  
  
  private void backupFormats() {
  	oldFormats = new Vector[ids.size()];
  	for (int i = 0; i < oldFormats.length; i++) {
			oldFormats[i] = new Vector<LabelFormats>();
		}
  	
  	for (int i = 0; i < branches.length; i++) {
    	for (int j = 0; j < ids.size(); j++) {
  			Label l = branches[i].getLabels().get(ids.get(j));
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
			for (int j = 0; j < ids.size(); j++) {
				Label l = branches[i].getLabels().get(ids.get(j));
				if (l != null) {
					l.getFormats().assignLabelFormats(oldFormats[j].get(i));
				}
			}
  	}
  }
  
  
  private static Label getLabel(Branch branch, String id) {
		Label result = branch.getLabels().get(id);
		if (result == null) {
			result = branch.getLabels().get(conflictIDBySupportID(id));
		}
		return result;
  }
  
  
  private static void position(Branch branch, List<String> ids) {
  	int labelsPerBlock = ids.size() / 2 + ids.size() % 2;
  	int labelsPerLine = Math.round(labelsPerBlock / 
  			(float)Math.sqrt(labelsPerBlock / LABEL_BLOCK_ASPECT_RATIO));
  	
  	// Upper block:
  	int pos = 0;
  	while (pos < labelsPerBlock) {
  		Label l = getLabel(branch, ids.get(pos));
  		if (l != null) {
  			LabelFormats f = l.getFormats();
 			  f.setAbove(true);
 			  f.setLineNumber(pos / labelsPerLine);
 			  f.setLinePosition((pos % labelsPerLine) * LINE_POSITION_STEP);  // reinsert() wurde bis hier drei mal aufgerufen
  		}
  		pos++;
  	}
  	
  	// Lower block:
  	while (pos < ids.size()) {
  		Label l = getLabel(branch, ids.get(pos));;
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
		List<String> ids = readIDs(root, false);
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
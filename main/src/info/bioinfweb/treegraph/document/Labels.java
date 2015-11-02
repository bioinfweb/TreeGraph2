/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2015  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.document;


import info.bioinfweb.treegraph.document.format.*;
import info.bioinfweb.commons.Math2;

import java.util.*;




/**
 * This class stores the labels attached to a branch.
 * @author Ben St&ouml;ver
 */
public class Labels implements Cloneable {
	private static final int INITIAL_CAPACITY = 2; 
	private static final int CAPACITY_INCREMENT = 2; 
	public static final double DEFAULT_LINE_INDEX_INCREMENT = 10; 

	
	private Branch holdingBranch = null;
	private List<LabelLine> labelLinesAbove = 
		  new Vector<LabelLine>(INITIAL_CAPACITY, CAPACITY_INCREMENT);
	private List<LabelLine> labelLinesBelow = 
		  new Vector<LabelLine>(INITIAL_CAPACITY, CAPACITY_INCREMENT);
	
	
	public Labels(Branch holdingBranch) {
		super();
		this.holdingBranch = holdingBranch;
	}


	public Branch getHoldingBranch() {
		return holdingBranch;
	}


	public void setHoldingBranch(Branch holdingBranch) {
		this.holdingBranch = holdingBranch;
	}


	//TODO Wird nicht ausreichend oft aufgerufen!
	private void reduceLabelLineVectors() {
		int pos = labelLinesAbove.size() - 1;
		while ((pos >= 0) && (labelLinesAbove.get(pos).size() == 0)) {
			labelLinesAbove.remove(pos);
			pos--;
		}
		pos = labelLinesBelow.size() - 1;
		while ((pos >= 0) && (labelLinesBelow.get(pos).size() == 0)) {
			labelLinesBelow.remove(pos);
			pos--;
		}
	}
	
	
	private List<LabelLine> getLines(boolean above) {
		if (above) {
			return labelLinesAbove;
		}
		else {
			return labelLinesBelow;
		}
	}
	
	
	private void calculateNewLinePosition(Label label, LabelLine line) {
		double newLinePos = label.getFormats().getLinePosition();
	  int leftIndex = line.getIndexBeforeLinePos(newLinePos);
	  
	  if (leftIndex != -1) {	
	  	double leftLinePos = line.get(leftIndex).getFormats().getLinePosition();
	  	if (newLinePos == leftLinePos) {
	  		if (line.size() - 1 != leftIndex) {
	  			label.getFormats().setLinePosition((leftLinePos + line.get(leftIndex + 1).getFormats().getLinePosition()) / 2);
	  		}
	  		else {
	  			label.getFormats().setLinePosition(newLinePos + DEFAULT_LINE_INDEX_INCREMENT);
	  		}
	  	}
	  }
	}
	
	
	/**
	 * Adds a label to the List and removes any other label or hidden data entry with the same ID on the same node or branch.
	 * @param label - the label to add
	 * 
	 * @return the previous element which was attached to the node or branch this <code>Labels</code> object belongs to 
	 *         with the same ID as the inserted label (This can either be a {@link TextElementData} object, if a hidden 
	 *         data entry was replaces or an instance of a class inherited from {@link Label}), if a label with the 
	 *         specified ID was attached to the node attached to this map.        
	 */
	public Object add(Label label) {
		Object result = null;
		if (getHoldingBranch() != null) {
			result = IDManager.removeElementWithID(getHoldingBranch().getTargetNode(), label.getID());
		}
		
		label.setLabels(this);
		LabelFormats f = label.getFormats();
		List<LabelLine> lines = getLines(f.isAbove());
		// Ggf. Zeile hinzuf�gen:
		if (f.getLineNumber() >= lines.size()) {
			for (int i = lines.size(); i <= f.getLineNumber(); i++) {
				lines.add(new LabelLine());
			}
		}
		LabelLine line = lines.get(f.getLineNumber());
		calculateNewLinePosition(label, line);
		line.insert(label);
		
		return result;
	}
	
	
	/**
	 * Removes the specified label.
	 * @param label - the object to be removed
	 * @return <code>true</code> if an element was removed
	 */
	public boolean remove(Label label) {
		for (int i = 0; i < lineCount(true); i++) {
			if (labelLinesAbove.get(i).remove(label)) {
				return true;
			}
		}
		for (int i = 0; i < lineCount(false); i++) {
			if (labelLinesBelow.get(i).remove(label)) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Removes the label with the specified ID.
	 * @param id - the ID of the label to be removed
	 * @return <code>true</code> if an element was removed
	 */
	public boolean remove(String id) {
		Label l = get(id);
		if (l != null) {
			return remove(l);
		}
		else {
			return false;
		}
	}
	
	
	public boolean reinsert(Label label) {
		boolean result = remove(label);
		if (result) {  // Nur wieder einf�gen, wenn es vorher vorhanden war.
			add(label);
			reduceLabelLineVectors();
		}
		return result;
	}
	
	
	/**
	 * Returns the label at the specified position
	 * 
	 * @param above indicates whether the label is above the branch
	 * @param lineNo the number of the line the label in located in (below or above the branch) The first line has the index 0.
	 * @param lineIndex the position in the line counted from the left (Does not equal the linePosition value.)
	 * @return the label at the specified position
	 */
	public Label get(boolean above, int lineNo, int lineIndex) {
		return getLines(above).get(lineNo).get(lineIndex);
	}
	
	
	private Label getNext(List<LabelLine> lines, int addend, int lineNo, int lineIndex) {
		int aboveLineNo = lineNo + addend;
		while (Math2.isBetween(aboveLineNo, 0, lines.size() - 1) && (lines.get(aboveLineNo).size() == 0)) {
			aboveLineNo += addend;
		}
		if (Math2.isBetween(aboveLineNo, 0, lines.size() - 1)) {
			LabelLine line = lines.get(aboveLineNo);
			return line.get(Math.min(lineIndex, line.size() - 1));
		}
		else {
			return null;
		}
	}
	
	
	public Label getAbove(boolean above, int lineNo, int lineIndex) {
		reduceLabelLineVectors();
		List<LabelLine> lines = getLines(above);
		int addend = -1;
		if (above) {
			addend = 1;
		}
		return getNext(lines, addend, lineNo, lineIndex);
	}
	
	
	public Label getBelow(boolean above, int lineNo, int lineIndex) {
		reduceLabelLineVectors();
		List<LabelLine> lines = getLines(above);
		int addend = 1;
		if (above) {
			addend = -1;
		}
		return getNext(lines, addend, lineNo, lineIndex);
	}
	
	
	private Label searchByID(String id, boolean above) {
		for (int lineNo = 0; lineNo < lineCount(above); lineNo++) {
			for (int lineIndex = 0; lineIndex < labelCount(above, lineNo); lineIndex++) {
				Label label = get(above, lineNo, lineIndex); 
				if (label.getID().equals(id)) {
					return label;
				}
			}
		}
		return null;
	}
	
	
	public Label get(String id) {
		Label result = searchByID(id, true);
		if (result != null) {
			return result;
		}
		else {
			return searchByID(id, false);
		}
	}
	
	
	/**
	 * Tests if the specified label is contained in the specified block (above or below the
	 * branch).
	 * 
	 * @param above
	 * @param label
	 * @return <code>true</code> if the label is contained
	 */
	public boolean contains(boolean above, Label label) {
		List<LabelLine> lines = getLines(above);
		for (int i = 0; i < lines.size(); i++) {
			if (lines.get(i).contains(label)) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Tests if the specified label is contained in this object.
	 * 
	 * @param label
	 * @return <code>true</code> if the label is contained
	 */
	public boolean contains(Label label) {
		if (contains(true, label)) {
			return true;
		}
		else {
			return contains(false, label);
		}
	}
	
	
	public double getLastLinePos(boolean above, int lineNumber) {
		int labelCount = labelCount(above, lineNumber);
		if (labelCount > 0) {
			return get(above, lineNumber, labelCount - 1).getFormats().getLinePosition() + DEFAULT_LINE_INDEX_INCREMENT;
		}
		else {
			return 0;
		}
	}
	
	
	/**
	 * Returns the line number this label is located in.
	 * @param above - specifies the labels block
	 * @param label
	 * @return the line number or -1 if the label is not contained in the specified label block
	 *         at all
	 */
	public int getLineNo(boolean above, Label label) {
		List<LabelLine> lines = getLines(above);
		for (int i = 0; i < lines.size(); i++) {
			if (lines.get(i).contains(label)) {
				return i;
			}
		}
		return -1;
	}
	
	
	public int getLineIndex(Label label) {
		int result = getLineIndex(true, label);
		if (result == -1) {
			result = getLineIndex(false, label);
		}
		return result;
	}
	
	
	/**
	 * Returns the index of the specified label in its line.
	 * @param above
	 * @param label
	 * @return the index or -1
	 */
	public int getLineIndex(boolean above, Label label) {
		return getLineIndex(above, getLineNo(above, label), label);
	}
	
	
	/**
	 * Returns the index of the specified label in its line. This method is fater than
	 * {@link Labels#getLineIndex(boolean, Label)}.
	 * @param above
	 * @param lineNo
	 * @param label
	 * @return the index or -1
	 */
	public int getLineIndex(boolean above, int lineNo, Label label) {
		return getLines(above).get(lineNo).indexOf(label); 
	}
	
	
	public void clear() {
		labelLinesAbove.clear();
		labelLinesBelow.clear();
	}
	
	
	/**
	 * Tests if any labels are present
	 * @return true if labels are present
	 */
	public boolean isEmpty() {
		reduceLabelLineVectors();  // Stellt sicher, dass nicht ausschlie�lic leere Linien vorhanden sind.
		return labelLinesAbove.isEmpty() && labelLinesBelow.isEmpty();
	}
	
	
	public int lineCount(boolean above) {
		if (above) {
			return labelLinesAbove.size();
		}
		else {
			return labelLinesBelow.size();
		}
	}
	
	
	/** Returns the number of labels in the specified line. */
	public int labelCount(boolean above, int lineNo) {
		if (above) {
			if (labelLinesAbove.size() <= lineNo) {
				return 0;
			}
			else {
				return labelLinesAbove.get(lineNo).size();
			}
		}
		else {
			if (labelLinesBelow.size() <= lineNo) {
				return 0;
			}
			else {
				return labelLinesBelow.get(lineNo).size();
			}
		}
	}
	
	
	private boolean containsSameID(Labels other, boolean above) {
		for (int lineNo = 0; lineNo < lineCount(above); lineNo++) {
			for (int linePos = 0; linePos < labelCount(above, lineNo); linePos++) {
				if (other.get(get(above, lineNo, linePos).getID()) != null) {
					return true;
				}
			}
		}
		return false;
	}
	
	
	public boolean containsSameID(Labels other) {
		return containsSameID(other, true) || containsSameID(other, false);
	}
	
	
	private void addLabelBlock(List<Label> list, boolean above, boolean textLabel, boolean iconLabel, 
			boolean pieChart) {
		
		for (int lineNo = 0; lineNo < lineCount(above); lineNo++) {
			for (int linePos = 0; linePos < labelCount(above, lineNo); linePos++) {
				Label l = get(above, lineNo, linePos);
				if ((textLabel && (l instanceof TextLabel)) || (iconLabel && (l instanceof IconLabel)) || 
						(pieChart && (l instanceof PieChartLabel))) {
					
					list.add(l);
				}
			}
		}
	}
	
	
	private List<Label> labelList(boolean textLabel, boolean iconLabel, boolean pieChart) {
		LinkedList<Label> result = new LinkedList<Label>();
		addLabelBlock(result, true, textLabel, iconLabel, pieChart);
		addLabelBlock(result, false, textLabel, iconLabel, pieChart);
		return result;
	}
	
	
	public Label[] toLabelArray(boolean textLabel, boolean iconLabel, boolean pieChart) {
		List<Label> list = labelList(textLabel, iconLabel, pieChart);
		return list.toArray(new Label[list.size()]);
	}
	
	
	public Label[] toLabelArray() {
		return toLabelArray(true, true, true);
	}
	
	
	public TextLabel[] toTextLabelArray() {
		List<Label> list = labelList(true, false, false);
		return list.toArray(new TextLabel[list.size()]);
	}
	
	
	public IconLabel[] toIconLabelArray() {
		List<Label> list = labelList(false, true, false);
		return list.toArray(new IconLabel[list.size()]);
	}
	
	
	public PieChartLabel[] toPieChartLabelArray() {
		List<Label> list = labelList(false, false, true);
		return list.toArray(new PieChartLabel[list.size()]);
	}
	
	
	private void cloneLabelBlock(boolean above, Labels target) {
  	for (int lineNo = 0; lineNo < lineCount(above); lineNo++) {
			for (int lineIndex = 0; lineIndex < labelCount(above, lineNo); lineIndex++) {
				Label label = get(above, lineNo, lineIndex);
				label = label.clone();
				target.add(label);
			}
		}
	}
	

	/**
	 * Returns a deep copy of this <code>Labels</code>-object (including copys of all 
	 * <code>LabelLine</code>-objects and their <code>Labels</code>.
	 * Note that the holding branch is still the same as in the original 
	 * <code>Labels</code>-object.
	 */
	@Override
	public Labels clone() {
		Labels result = new Labels(null);    // getHoldingBranch() must not be specified here, because this leads to the deletion of ID elements while they are copied.
		cloneLabelBlock(true, result);
		cloneLabelBlock(false, result);
		return result;
	}
}
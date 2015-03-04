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
package info.bioinfweb.treegraph.document;


import info.bioinfweb.treegraph.document.format.FormatUtils;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.commons.graphics.GraphicsUtils;

import java.awt.Color;

import javax.swing.JOptionPane;



/**
 * Ensures that document elements (that are e.g. pasted from the clipboard) have a 
 * sufficient contrast to the document background.
 * @author Ben St&ouml;ver
 * @since 2.0.20-140
 */
public class ContrastManager {
	private State state = State.UNDEFINED;
	
	
	enum State {
		UNDEFINED, CHANGE, NO_CHANGE, ABORTED;
	}

	
	/**
	 * Asks the user whether to change any colors if this was not already done.
	 * @return <code>false</code> if the user aborted the operation during the call of this
	 *         method
	 */
	private boolean confirmChange() {
		if (state.equals(State.UNDEFINED)) {
			String[] options = {"Change unsifficient contrasted colors", "Paste unchanged", "Cancel"};
			switch (JOptionPane.showOptionDialog(MainFrame.getInstance(), "One or more of the " +
					"elements you are trying to paste have an unsufficient contrast to the " +
					"background of the document.", "Unsufficient contrast", 
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, 
					options[0])) {
			
			  case 0:
				  state = State.CHANGE;
				  break;
			  case 1:
  				state = State.NO_CHANGE;
				  break;
			  default:
  				state = State.ABORTED;
  		}
		}
		return !state.equals(State.ABORTED);
	}


	/**
	 * Checks if two colors have an unsifficient contast.
	 * @param foreground
	 * @param background
	 * @return <code>false</code> if the user aborted the operation during the call of this
	 *         method
	 */
	private boolean checkColor(Color foreground, Color background) {
		return GraphicsUtils.brightnessDifference(foreground, background)	< FormatUtils.MIN_COLOR_DIFFERENCE;
	}
	
	
	private boolean ensureLabelBlock(Labels labels, boolean above, Color bgColor, Color defaultColor) {
		for (int lineNo = 0; lineNo < labels.lineCount(above); lineNo++) {
			for (int lineIndex = 0; lineIndex < labels.labelCount(above, lineNo); lineIndex++) {
				Label label = labels.get(above, lineNo, lineIndex);
				Color color;
				if (label instanceof TextLabel) {
					color = ((TextLabel)label).getFormats().getTextColor();
				}
				else {  // IconLabel
					color = ((IconLabel)label).getFormats().getLineColor();
				}
				
				if (checkColor(color, bgColor)) {
					if (confirmChange()) {
						if (state.equals(State.CHANGE)) {
							if (label instanceof TextLabel) {
								((TextLabel)label).getFormats().setTextColor(defaultColor);
							}
							else {  // IconLabel
								((IconLabel)label).getFormats().setLineColor(defaultColor);
							}
						}
					}
					else {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	
	/**
	 * Rekursive method which contains the actual implementation.
	 * @param document - the document where the subtree shall be inserted to
	 * @param root - the root node of the subtree
	 * @return <code>false</code> if the user aborted the operation during the call of this
	 *         method
	 */
	private boolean ensureSubtreeContrast(Document document, Node root) {
		Color bgColor = document.getTree().getFormats().getBackgroundColor();
		if (checkColor(root.getFormats().getTextColor(), bgColor)) {
			if (confirmChange() && state.equals(State.CHANGE)) {  // order important
				root.getFormats().setTextColor(FormatUtils.getTextColor(document));
			}
		}
		if (checkColor(root.getFormats().getLineColor(), bgColor)) {
			if (confirmChange() && state.equals(State.CHANGE)) {
				root.getFormats().setLineColor(FormatUtils.getLineColor(document));
			}
		}
		if (checkColor(root.getAfferentBranch().getFormats().getLineColor(), bgColor)) {
			if (confirmChange() && state.equals(State.CHANGE)) {
				root.getAfferentBranch().getFormats().setLineColor(FormatUtils.getLineColor(document));
			}
		}
		if (!ensureLabelBlock(root.getAfferentBranch().getLabels(), true, bgColor, 
				FormatUtils.getTextColor(document))) {
			return false;
		}
		if (!ensureLabelBlock(root.getAfferentBranch().getLabels(), false, bgColor, 
				FormatUtils.getTextColor(document))) {
			return false;
		}
		
		if (state.equals(State.CHANGE) || state.equals(State.UNDEFINED)) {  // Bedingung nur um evtl. Zeit zu sparen
			for (int i = 0; i < root.getChildren().size(); i++) {
				if (!ensureSubtreeContrast(document, root.getChildren().get(i))) {
					return false;
				}
			}
		}
		return !state.equals(State.ABORTED);
	}
	
	
	private boolean ensureLegendContrast(Document document, Legend legend) {
		Color bgColor = document.getTree().getFormats().getBackgroundColor();
		if (checkColor(legend.getFormats().getTextColor(), bgColor)) {
			if (confirmChange() && state.equals(State.CHANGE)) {  // order important
				legend.getFormats().setTextColor(FormatUtils.getTextColor(document));
			}
		}
		if (checkColor(legend.getFormats().getLineColor(), bgColor)) {
			if (confirmChange() && state.equals(State.CHANGE)) {  // order important
				legend.getFormats().setLineColor(FormatUtils.getLineColor(document));
			}
		}
		return !state.equals(State.ABORTED);
	}
	
	
	/**
	 * Checks the colors of all elements in the subtree under <code>root</code> (including 
	 * <code>root</code> and its afferent branch) regarding to their contrast to the 
	 * background color of the document. 
	 * @param document - the document where the subtree shall be inserted to
	 * @param root - the root node of the subtree
	 * @param defaultColor - the color which shall be set to elements with an unsufficient contrast
	 * @return <code>false</code> if the subtree shall not be pasted
	 */
	public boolean ensureContrast(Document document, Node root, Legend[] legends) {
		state = State.UNDEFINED;
		boolean result = ensureSubtreeContrast(document, root);
		int pos = 0;
		while ((pos < legends.length) && ((state.equals(State.UNDEFINED) || state.equals(State.CHANGE)))) {
			result = result || ensureLegendContrast(document, legends[pos]);
			pos++;
		}
		return result;
	}
	
	
	/**
	 * Internal method which does not set <code>state = State.UNDEFINED</code>.
	 * @param document - the document where the label shall be inserted to
	 * @param label
	 * @return <code>false</code> if the user aborted the operation during the call of this
	 *         method
	 */
	private boolean ensureLabelContrast(Document document, Label label) {
		Color color;
		if (label instanceof TextLabel) {
			color = ((TextLabel)label).getFormats().getTextColor();
		}
		else {  // GraphicalLabel
			color = ((GraphicalLabel)label).getFormats().getLineColor();
		}
		if (checkColor(color, 
				document.getTree().getFormats().getBackgroundColor())) {
			
			if (confirmChange() && state.equals(State.CHANGE)) {  // order important
				if (label instanceof TextLabel) {
					((TextLabel)label).getFormats().setTextColor(FormatUtils.getTextColor(document));
				}
				else {  // GraphicalLabel
					((GraphicalLabel)label).getFormats().setLineColor(FormatUtils.getTextColor(document));
				}
			}
		}
		return !state.equals(State.ABORTED);
	}
	
	
	/**
	 * Checks the text color of the specified label regarding to its contrast to the 
	 * background color of the document. 
	 * @param document - the document where the subtree shall be inserted to
	 * @param label - the label to be inserted
	 * @param defaultColor - the color which shall be set to elements with an unsufficient contrast
	 * @return <code>false</code> if the label shall not be pasted
	 */
	public boolean ensureContrast(Document document, Label label) {
		state = State.UNDEFINED;
		return ensureLabelContrast(document, label);
	}
	
	
	/**
	 * Checks the text color of the specified labels regarding to their contrast to the 
	 * background color of the document. 
	 * @param document - the document where the subtree shall be inserted to
	 * @param labels - the labels to be inserted
	 * @return <code>false</code> if the labels shall not be pasted
	 */
	public boolean ensureContrast(Document document, Label[] labels) {
		state = State.UNDEFINED;
		for (int i = 0; i < labels.length; i++) {
			if (!ensureLabelContrast(document, labels[i])) {
				return false;
			}
		}
		return true;
	}
	
	
	/**
	 * Checks the text color of the specified legend regarding to its contrast to the 
	 * background color of the document. 
	 * @param document - the document where the subtree shall be inserted to
	 * @param legend - the legend to be inserted
	 * @return <code>false</code> if the legend shall not be pasted
	 */
	public boolean ensureContrast(Document document, Legend legend) {
		state = State.UNDEFINED;
		return ensureLegendContrast(document, legend);
	}
}
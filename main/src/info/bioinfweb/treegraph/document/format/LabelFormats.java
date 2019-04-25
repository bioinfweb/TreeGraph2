/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2019  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.document.format;


import info.bioinfweb.treegraph.document.Label;



/**
 * Implements basic label formats.
 * 
 * @author Ben St&ouml;ver
 */
public class LabelFormats implements ElementFormats, Cloneable {
	public static final float DEFAULT_MARGIN_IN_MM = 1f;
	
	
	private boolean above = true;
	private int lineNumber = 0;
	private double linePosition = 0;  // Bestimmt die Reihenfolge aller Elemente einer Zeile
	private Label owner = null;
	private Margin margin = new Margin(DEFAULT_MARGIN_IN_MM);
	
	
	public LabelFormats(Label owner) {
		this.owner = owner;
	}
	
	
	public LabelFormats(Label owner, boolean above, int line, int linePosition) {
		this.owner = owner;
		setAbove(above);
		setLineNumber(line);
		setLinePosition(linePosition);
	}


  /**
   * If this object has an owner-<code>Label</code> this is reinserted into its 
   * <code>Labels</code>-object. This is necassary because the store-position in the
   * <code>Labels</code>-object is dependent of the label position formats.
   */
  private void reinsert() {
  	if (owner != null) {
  		owner.reinsert();
  	}
  }
	
	
	/**
	 * The number of the line in which this label shall be displayed. The lines closest to the 
	 * branch have the index 0. (i.e. line numbers above the branch increase from bottom to 
	 * top and lines below the branch increase from top to bottom. If the branch is the paint
	 * start, label positions will be rotated counterclockwise.)  
	 * @return the current line number
	 */
	public int getLineNumber() {
		return lineNumber;
	}


	public void setLineNumber(int line) {
		if (line != lineNumber) {
			this.lineNumber = line;
			reinsert();
		}
	}


	/**
	 * If more than one element is located in the same line the line position is used to 
	 * determine the order of the elements. Elements with a low line position are displayed
	 * on the left.
	 * @return the current value of line position
	 */
	public double getLinePosition() {
		return linePosition;
	}


	public void setLinePosition(double linePosition) {
		if (this.linePosition != linePosition) {
			this.linePosition = linePosition;
			reinsert();
		}
	}


	public void setAbove(boolean above) {
		if (this.above != above) {
			this.above = above;
			reinsert();
		}
	}


	public boolean isAbove() {
		return above;
	}


	public Label getOwner() {
		return owner;
	}


	public void setOwner(Label labelOwner) {
		owner = labelOwner;
	}
	
	
	public Margin getMargin() {
		return margin;
	}


	public void assignLabelFormats(LabelFormats other) {
		setLineNumber(other.getLineNumber());
		setLinePosition(other.getLinePosition());
		setAbove(other.isAbove());
		getMargin().assign(other.getMargin());
	}


	/**
	 * Returns a deep copy with the owner <code>null</code>.
	 */
	@Override
	public LabelFormats clone()  {
		LabelFormats result = new LabelFormats(null);
		result.assignLabelFormats(this);
		return result;
	}
}
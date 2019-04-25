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
package info.bioinfweb.treegraph.document;


import java.util.regex.Pattern;

import info.bioinfweb.treegraph.document.format.LabelFormats;



/**
 * @author BenStoever
 */
public abstract class Label extends AbstractPaintableElement implements TreeElement, Cloneable {
	private static final Pattern idPattern = Pattern.compile("[_\\w]+");
	
	
  private Labels labels = null;
	private String id = "";


  private Label() {
		super();
	}


	public Label(Labels labels) {
  	super();
		this.labels = labels;
  }
  
  
	public abstract LabelFormats getFormats();


  public String getID() {
		return id;
	}


  public Labels getLabels() {
		return labels;
	}
  
  
  public Branch getHoldingBranch() {
  	if (getLabels() != null) {
  		return getLabels().getHoldingBranch();
  	}
  	else {
  		return null;
  	}
  }


	public void setLabels(Labels labels) {
		this.labels = labels;
	}


	public void setID(String id) {
		this.id = id;
	}
  

	public static boolean idIsValid(String id) {
		if (id == null) {
			return false;
		}
		else {
			return idPattern.matcher(id).matches();
		}
	}
	
	
	/**
	 * Reinserts this label into its owning {@link Labels}-object of one is linked. This 
	 * becomes necessary of the position of the label changes.
	 */
	public void reinsert() {
		if (getLabels() != null) {
			getLabels().reinsert(this);
		}
	}


	/**
	 * Assigns the values of the passed label to this class. Note that the link to the owning 
	 * {@link Labels}-object <code>other</code> might have is not copied.
	 * @param other
	 */
	public void assignLabelData(Label other) {
		setID(other.getID());
	}
	
	
	/**
	 * Tests if this label is located in the upper label block.
	 * @return <code>true</code>, if the label is above, <code>false</code> if the label is
	 *         below or not linked to a {@link Labels}-object. 
	 */
	public boolean isAbove() {
		return (getLabels() != null) && getLabels().contains(true, this);
	}
	
	
	/**
	 * Returns the line number of this label or -1 if it is not linked to a 
	 * {@link Labels}-object.
	 * @return
	 */
	public int getLineNo() {
		if (getLabels() != null) {
			boolean above = getLabels().contains(true, this);
		  return getLabels().getLineNo(above, this);
		}
		else {
			return -1;
		}
	}
	
	
	/**
	 * Returns the line index of the label or -1 if it is not linked to a 
	 * {@link Labels}-object.
	 * @return
	 */
	public int getLineIndex() {
		if (getLabels() != null) {
			boolean above = getLabels().contains(true, this);
		  return getLabels().getLineIndex(above, this);
		}
		else {
			return -1;
		}
	}
	
	
	public Label getNext() {
		if (getLabels() != null) {
			boolean above = getLabels().contains(true, this);
			int lineNo = getLabels().getLineNo(above, this);
			int lineIndex = getLabels().getLineIndex(above, lineNo, this);
			if (lineIndex < getLabels().labelCount(above, lineNo) - 1) {
				return getLabels().get(above, lineNo, lineIndex + 1);
			}
		}
		return null;
	}


	public Label getPrevious() {
		if (getLabels() != null) {
			boolean above = getLabels().contains(true, this);
			int lineNo = getLabels().getLineNo(above, this);
			int lineIndex = getLabels().getLineIndex(above, lineNo, this);
			if (lineIndex > 0) {
				return getLabels().get(above, lineNo, lineIndex - 1);
			}
		}
		return null;
	}


	public Label getAbove() {
		if (getLabels() != null) {
			return getLabels().getAbove(isAbove(), getLineNo(), getLineIndex());
		}
		return null;
	}


	public Label getBelow() {
		if (getLabels() != null) {
			return getLabels().getBelow(isAbove(), getLineNo(), getLineIndex());
		}
		return null;
	}


	public Node getLinkedNode() {
		return getLabels().getHoldingBranch().getTargetNode();
	}


	@Override
	public abstract Label clone();
}
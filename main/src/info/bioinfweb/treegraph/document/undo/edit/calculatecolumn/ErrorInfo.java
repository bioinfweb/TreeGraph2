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
package info.bioinfweb.treegraph.document.undo.edit.calculatecolumn;


import info.bioinfweb.treegraph.document.undo.edit.CalculateColumnEdit;



/**
 * Models an error message used in {@link CalculateColumnEdit}.
 * 
 * @author Ben St&ouml;ver
 * @since 2.13.0
 */
public class ErrorInfo {
	private String uniqueNodeName;
	private String message;
	private boolean inValueExpression = true;
	
	
	public ErrorInfo(String message, boolean inValueExpression) {
		this(null, message, inValueExpression);
	}
	
	
	public ErrorInfo(String uniqueNodeName, String message, boolean inValueExpression) {
	  super();
	  if (message == null) {
	  	throw new NullPointerException("The message must not be null.");
	  }
	  else {
		  this.uniqueNodeName = uniqueNodeName;
		  this.message = message;
		  this.inValueExpression = inValueExpression;
	  }
  }


	public String getUniqueNodeName() {
		return uniqueNodeName;
	}
	
	
	public boolean isNodeSpecific() {
		return getUniqueNodeName() != null;
	}
	
	
	public String getMessage() {
		return message;
	}


	public boolean isInValueExpression() {
		return inValueExpression;
	}

	
	public String getExpressionPrefix() {
		if (isInValueExpression()) {
			return "Error in value expression: ";
		}
		else {
			return "Error in target column expression: ";
		}
	}
	
	
	@Override
  public String toString() {
		if (isNodeSpecific()) {
			return "Calculating a value for the node " + getUniqueNodeName() + 
					" was skipped because of the following error in calulating the target columns ID: \"" + getExpressionPrefix() + 
					getMessage() + "\".";
		}
		else {
			return getExpressionPrefix() + getMessage();
		}
  }


	@Override
  public int hashCode() {
	  final int prime = 31;
	  int result = 1;
	  result = prime * result + (inValueExpression ? 1231 : 1237);
	  result = prime * result + ((message == null) ? 0 : message.hashCode());
	  result = prime * result
	          + ((uniqueNodeName == null) ? 0 : uniqueNodeName.hashCode());
	  return result;
  }


	@Override
  public boolean equals(Object obj) {
	  if (this == obj)
		  return true;
	  if (obj == null)
		  return false;
	  if (getClass() != obj.getClass())
		  return false;
	  ErrorInfo other = (ErrorInfo) obj;
	  if (inValueExpression != other.inValueExpression)
		  return false;
	  if (message == null) {
		  if (other.message != null)
			  return false;
	  } else if (!message.equals(other.message))
		  return false;
	  if (uniqueNodeName == null) {
		  if (other.uniqueNodeName != null)
			  return false;
	  } else if (!uniqueNodeName.equals(other.uniqueNodeName))
		  return false;
	  return true;
  }
}

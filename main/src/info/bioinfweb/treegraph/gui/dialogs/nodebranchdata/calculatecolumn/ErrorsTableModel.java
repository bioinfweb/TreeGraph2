/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2017  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.gui.dialogs.nodebranchdata.calculatecolumn;


import info.bioinfweb.treegraph.document.undo.edit.calculatecolumn.ErrorInfo;

import java.util.Collections;
import java.util.List;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;



public class ErrorsTableModel extends AbstractTableModel implements TableModel {
	public static final String VALUE_TEXT = "Value";
	public static final String TARGET_COLUMN_TEXT = "Target";
	
	
	private List<ErrorInfo> errors;
	
	
	public ErrorsTableModel() {
		this(null);
	}
	
	
	public ErrorsTableModel(List<ErrorInfo> errors) {
	  super();
	  if (errors == null) {
	  	this.errors = Collections.emptyList();
	  }
	  else {
	  	this.errors = errors;
	  }
  }


	public List<ErrorInfo> getErrors() {
		return errors;
	}


	public void setErrors(List<ErrorInfo> errors) {
		this.errors = errors;
		fireTableDataChanged();
	}


	@Override
  public int getColumnCount() {
	  return 3;
  }

	
	@Override
  public int getRowCount() {
	  return errors.size();
  }

	
	@Override
  public Class<?> getColumnClass(int columnIndex) {
	  return String.class;
  }


	@Override
  public String getColumnName(int columnIndex) {
		switch (columnIndex) {
			case 0:
				return "Node";
			case 1:
				return "Expression";
			case 2:
				return "Error";
			default:
			  return null;
		}
  }


	@Override
  public Object getValueAt(int rowIndex, int columnIndex) {
		ErrorInfo error = errors.get(rowIndex);
		switch (columnIndex) {
			case 0:
				return error.getUniqueNodeName();
			case 1:
				if (error.isInValueExpression()) {
					return VALUE_TEXT;
				}
				else {
					return TARGET_COLUMN_TEXT;
				}
			case 2:
				return error.getMessage();
			default:
			  return null;
		}
  }
}

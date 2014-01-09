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
package info.bioinfweb.treegraph.document.undo.edit;


import java.util.regex.Pattern;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.ComplexDocumentEdit;



public class ReplaceInNodeDataEdit extends ComplexDocumentEdit {
	private NodeBranchDataAdapter adapter = null;
	private InsertPosition position = null;
	private Pattern searchPattern = null; 
	private String newText = null;
	
	
	public enum InsertPosition {
		BEFORE, AFTER, REPLACE;
	}
	
	
	public ReplaceInNodeDataEdit(Document document, NodeBranchDataAdapter adapter, 
			InsertPosition position, String oldText, String newText, boolean caseSensitive,
			boolean wordsOnly) {
		
		super(document);
		this.adapter = adapter;
		this.position = position;
		if (position.equals(InsertPosition.REPLACE)) {
			this.searchPattern = generatePattern(oldText, caseSensitive, wordsOnly);
		}
		this.newText = newText;
	}
	
	
	public static Pattern generatePattern(String text, boolean caseSensitive, boolean wordsOnly) {
		String code = Pattern.quote(text);
		if (wordsOnly) {
			code = "(^|\\b)" + code + "($|\\b)";
		}
		int flags = Pattern.DOTALL;
		if (!caseSensitive) {
			flags += Pattern.CASE_INSENSITIVE; 
		}
		return Pattern.compile(code, flags);
	}


	private void replace(Node root) {
		String text = adapter.getText(root);
		if ((text != null) && !adapter.decimalOnly()) {
			switch (position) {
				case BEFORE:
					text = newText + text;
					break;
				case AFTER:
					text += newText;
					break;
				case REPLACE:
					text = searchPattern.matcher(text).replaceAll(newText);
					break;
			}
			adapter.setText(root, text);
		}
		
  	for (int i = 0; i < root.getChildren().size(); i++) {
 			replace(root.getChildren().get(i));
		}
	}
	
	
	@Override
	protected void performRedo() {
		if (!document.getTree().isEmpty()) {
			replace(document.getTree().getPaintStart());
		}
	}


	public String getPresentationName() {
		return "Replace text in node data";
	} 
}
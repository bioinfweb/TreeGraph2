/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011  Ben Stöver, Kai Müller
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
package info.bioinfweb.treegraph.gui.actions.view;


import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.JOptionPane;

import info.bioinfweb.treegraph.Main;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.graphics.positionpaint.PositionPaintFactory;
import info.bioinfweb.treegraph.graphics.positionpaint.PositionPaintType;
import info.bioinfweb.treegraph.gui.actions.DocumentAction;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;
import info.webinsel.wikihelp.client.WikiHelpOptionPane;



/**
 * Allows the user to change the view mode.
 * @author Ben St&ouml;ver
 */
public class ChangePainterIDAction extends DocumentAction {
	private PositionPaintType type = PositionPaintFactory.getDefaultType();
	
	
	public ChangePainterIDAction(MainFrame mainFrame, PositionPaintType type) {
		super(mainFrame);
		this.type = type;
		
		String name = PositionPaintFactory.getInstance().getName(type);
		putValue(Action.NAME, name); 
	  putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, 0);
		putValue(Action.SHORT_DESCRIPTION, name + " view"); 
	  loadSymbols("Painter" + name.replaceAll(" ", "").replaceAll("/", ""));
	}


	@Override
	protected void onActionPerformed(ActionEvent e, TreeInternalFrame frame) {
		if (PositionPaintFactory.getInstance().needsBrancheLengths(type)) {
			Tree tree = frame.getDocument().getTree();
			if (!Tree.hasAllBranchLengths(tree.getPaintStart(), false)) {
				WikiHelpOptionPane.showMessageDialog(MainFrame.getInstance(), 
						"Not all branch lengths of the current tree have been specified.\n\n" +
						"Undefined branches will be displayed as specified by their minimal " +
						"branch length format. Note that the displayed tree therefore is not " +
						"a real phylogram or chronogram.", "Missing branch length(s)", JOptionPane.WARNING_MESSAGE, 
						Main.getInstance().getWikiHelp(), 29);
			}
			else if (!tree.isEmpty() && tree.getFormats().getShowRooted() && 
					!tree.getPaintStart().getAfferentBranch().hasLength()) {
				
				WikiHelpOptionPane.showMessageDialog(MainFrame.getInstance(), 
						"The root branch length of this tree has not been specified.\n\n" +
						"It will be displayed as specified by its minimal branch length format. " +
						"Note that the displayed tree therefore is not a real phylogram or chronogram.\n\n" +
						"To avaid this message you can either specify a root branch length or " +
						"hide the root branch.", "Missing root branch length", JOptionPane.WARNING_MESSAGE, 
						Main.getInstance().getWikiHelp(), 29);
			}
		}
		frame.getTreeViewPanel().setPainterType(type);
	}


	@Override
	public void setEnabled(Document document, TreeSelection selection, NodeBranchDataAdapter tableAdapter) {
		setEnabled(document != null);
	}
}
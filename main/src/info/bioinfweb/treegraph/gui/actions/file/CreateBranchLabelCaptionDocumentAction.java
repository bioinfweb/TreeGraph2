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
package info.bioinfweb.treegraph.gui.actions.file;


import info.bioinfweb.treegraph.document.Branch;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Label;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.PieChartLabel;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.TextLabel;
import info.bioinfweb.treegraph.document.format.PieChartLabelCaptionContentType;
import info.bioinfweb.treegraph.document.format.PieChartLabelFormats;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.tools.IDManager;
import info.bioinfweb.treegraph.document.tools.TreeSerializer;
import info.bioinfweb.treegraph.gui.actions.DocumentAction;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;

import java.awt.event.ActionEvent;

import javax.swing.Action;



public class CreateBranchLabelCaptionDocumentAction extends DocumentAction {
	public static final String SOURCE_DATA_COLUMN_ID = "pieChartSource";
	
	
	public CreateBranchLabelCaptionDocumentAction(MainFrame mainFrame) {
		super(mainFrame);
		putValue(Action.NAME, "Create label caption document"); 
		putValue(Action.SHORT_DESCRIPTION, "Create a document with label captions of the selected branch.");
	}

	
	private void editPieChartLabel(PieChartLabel label, String sourceDataID) {
		TextElementData title = label.getData();
		if (title.isEmpty() || (title.isString() && title.getText().isEmpty())) {
			title.setText(label.getID());
		}
		
		for (PieChartLabel.SectionData data : label.getSectionDataList()) {
			if ((data.getCaption() == null) || data.getCaption().isEmpty()) {
				data.setCaption(data.getValueColumnID());
			}
			data.setValueColumnID(sourceDataID);
			
			PieChartLabelFormats f = label.getFormats();
			f.setShowTitle(true);
			if (PieChartLabelCaptionContentType.NONE.equals(f.getCaptionsContentType())) {
				f.setCaptionsContentType(PieChartLabelCaptionContentType.CAPTIONS);
			}
		}
	}
	

	@Override
	protected void onActionPerformed(ActionEvent e, TreeInternalFrame frame) {
		Branch sourceBranch = frame.getTreeViewPanel().getSelection().getFirstElementOfType(Branch.class);
		Document d = new Document();
		Node root = new Node(); 
		root.setAfferentBranch(sourceBranch.clone());
		d.getTree().setPaintStart(root);
		String sourceDataID = IDManager.newID(SOURCE_DATA_COLUMN_ID, IDManager.getIDListFromSubtree(root));
//		root.getMetadataRoot().put(sourceDataID, new TextElementData(1.0));
		
		Label[] labels = TreeSerializer.getElementsOnNode(root, Label.class);
		for (Label label : labels) {
			if (label instanceof TextLabel) {
				((TextLabel)label).getData().setText(label.getID());
			}
			if (label instanceof PieChartLabel) {
				editPieChartLabel((PieChartLabel)label, sourceDataID);
			}
		}
		
		d.getTree().updateElementSet();		
		getMainFrame().addInternalFrame(d);
	}


	@Override
	public void setEnabled(Document document, TreeSelection selection, NodeBranchDataAdapter tableAdapter) {
		setEnabled(oneElementSelected(selection) && selection.containsType(Branch.class) && 
				!selection.getFirstElementOfType(Branch.class).getLabels().isEmpty());
	}
}

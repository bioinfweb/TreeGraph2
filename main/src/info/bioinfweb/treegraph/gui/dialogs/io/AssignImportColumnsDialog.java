package info.bioinfweb.treegraph.gui.dialogs.io;


import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import info.bioinfweb.treegraph.document.IDManager;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.nodebranchdata.IDElementAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.VoidNodeBranchDataAdapter;
import info.bioinfweb.wikihelp.client.OkCancelApplyWikiHelpDialog;
import info.bioinfweb.wikihelp.client.WikiHelp;



public abstract class AssignImportColumnsDialog extends OkCancelApplyWikiHelpDialog {
	protected JPanel importPanel = null;
	protected JScrollPane scrollPane;
	protected Tree tree = null;

	
	public AssignImportColumnsDialog(Frame owner, boolean modal, WikiHelp wikiHelp) {
		super(owner, modal, wikiHelp);
	}
	
	
	protected boolean checkSelectedAdapters(NodeBranchDataAdapter[] adapterArray) {
		StringBuffer message = new StringBuffer();
		message.append("The following node/branch data columns were selected more than once:\n\n");
		Set<String> adapterTypes = new HashSet<String>();		
		
		boolean cancel = false;
		boolean idExists = false;
		
		for (int i = 0; i < adapterArray.length; i++) {
			NodeBranchDataAdapter adapter = adapterArray[i];
			if (!(adapter instanceof VoidNodeBranchDataAdapter)) {	
				if (adapter instanceof IDElementAdapter) {
					String name = "Node/branch data columns with the ID \"" + ((IDElementAdapter)adapter).getID();
					if (!adapterTypes.add(name)) {
						idExists = true;
						message.append(name);
						message.append("\"\n");
					}
				}
				else if (!adapterTypes.add(adapter.toString())) {
					idExists = true;
					message.append(adapter);
					message.append('\n');
				}
			}
			cancel = cancel || idExists;
		}
		message.append("\nPlease select different node/branch data columns.");		
		if (cancel) {
			JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
		}
		else {
			message.delete(0, message.length());
			message.append("The following node/branch data columns already exist in the tree:\n\n");
			for (int i = 0; i < adapterArray.length; i++) {
				NodeBranchDataAdapter adapter = adapterArray[i];
				boolean columnExists = false;
				if (!(adapter instanceof VoidNodeBranchDataAdapter)) {	
					if (adapter instanceof IDElementAdapter) {
						columnExists = IDManager.idExistsInSubtree(tree.getPaintStart(), ((IDElementAdapter)adapter).getID());
						if (columnExists) {
							message.append("Node/branch data columns with the ID \"");
							message.append(((IDElementAdapter)adapter).getID());
							message.append("\"\n");
						}						
					}
					else {
						columnExists = true;
						message.append(adapter.toString());
						message.append('\n');
					}
				}
				cancel = cancel || columnExists;
			}
			
			message.append("\n\nDo you want to possibly overwrite entries in these columns?\n");
			message.append("(Only data of those nodes that are referenced in the imported file will be affected.)");
			
			if (cancel) {
				cancel = (JOptionPane.showConfirmDialog(this, message, "Warning", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION);
			}
		}
		return !cancel;
	}
	

	/**
	 * This method initializes importPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	protected JPanel getImportPanel() {
		if (importPanel == null) {
			importPanel = new JPanel();
			importPanel.setLayout(new GridBagLayout());
		}
		return importPanel;
	}
	
	
	protected JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getImportPanel());
		}
		return scrollPane;
	}
}

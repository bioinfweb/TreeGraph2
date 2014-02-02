package info.bioinfweb.treegraph.gui.actions.file;


import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.gui.actions.EditDialogAction;
import info.bioinfweb.treegraph.gui.dialogs.EditDialog;
import info.bioinfweb.treegraph.gui.dialogs.io.NodeSupportDialog;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;

import java.awt.event.KeyEvent;

import javax.swing.Action;



/**
 * Allows the user to calculate node frequencies from a set of tree topologies. 
 * 
 * @author Ben St&ouml;ver
 */
public class NodeSupportAction extends EditDialogAction {
	public NodeSupportAction(MainFrame mainFrame) {
		super(mainFrame);
		putValue(Action.NAME, "Determine node frequency ..."); 
		putValue(Action.MNEMONIC_KEY, KeyEvent.VK_F);
		putValue(Action.SHORT_DESCRIPTION, "Get node frequency from other trees "); 
	//	putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0));
	}

	
	@Override
	public EditDialog createDialog() {
		return new NodeSupportDialog(getMainFrame());
	}


	@Override
	public void setEnabled(Document document, TreeSelection selection, NodeBranchDataAdapter tableAdapter) {
		setEnabled((document != null) && !document.getTree().isEmpty());
	}
}


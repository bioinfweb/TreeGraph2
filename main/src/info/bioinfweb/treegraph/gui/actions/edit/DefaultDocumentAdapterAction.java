package info.bioinfweb.treegraph.gui.actions.edit;

import javax.swing.Action;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.gui.actions.EditDialogAction;
import info.bioinfweb.treegraph.gui.dialogs.editelement.DefaultDocumentAdapterDialog;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;

public class DefaultDocumentAdapterAction extends EditDialogAction {
	public DefaultDocumentAdapterAction(MainFrame mainFrame) {
		super(mainFrame);
		putValue(Action.NAME, "Set default document adapters...");
	}

	
	@Override
	public DefaultDocumentAdapterDialog createDialog() {
		return new DefaultDocumentAdapterDialog(getMainFrame());
	}


	@Override
	public void setEnabled(Document document, TreeSelection selection, NodeBranchDataAdapter tableAdapter) {
		setEnabled((document != null) && !document.getTree().isEmpty());
	}
}

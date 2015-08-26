package info.bioinfweb.treegraph.gui.dialogs.io.ancestralstate;


import info.bioinfweb.treegraph.Main;
import info.bioinfweb.treegraph.document.IDManager;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.nodebranchdata.NewHiddenBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NewNodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.VoidNodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.file.ancestralstate.AncestralStateData;
import info.bioinfweb.treegraph.document.undo.file.ancestralstate.AncestralStateImportParameters;
import info.bioinfweb.treegraph.document.undo.file.importtable.ImportTableData;
import info.bioinfweb.treegraph.document.undo.file.importtable.ImportTableParameters;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NewNodeBranchDataInput;
import info.bioinfweb.wikihelp.client.OkCancelApplyWikiHelpDialog;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;



public class AssignBayesTraitsImportColumnsDialog extends OkCancelApplyWikiHelpDialog {
	private Tree tree = null;	
	private JPanel jContentPane = null;
	private JPanel importPanel = null;
	private List<CharacterInput> characterInputs = new ArrayList<CharacterInput>();
	private List<NewNodeBranchDataInput> inputs = new ArrayList<NewNodeBranchDataInput>();


	/**
	 * Creates a new instance of this dialog.
	 * @param owner the parent window
	 */
	public AssignBayesTraitsImportColumnsDialog(Frame owner) {
		super(owner, true, Main.getInstance().getWikiHelp());
		initialize();
		setLocationRelativeTo(owner);
	}


	private void createInputs(AncestralStateImportParameters parameters, AncestralStateData data) {
//		Iterator<String> keySetIterator = data.getCharacterMap().keySet().iterator();
		for (int i = 0; i < data.getCharacterCount(); i++) {			
			CharacterInput input = new CharacterInput(getImportPanel(), i, i, parameters, data, tree);
			characterInputs.add(input);
		}
		//TODO import internal node names
		pack();
	}
	
	
	public void execute(AncestralStateImportParameters parameters, AncestralStateData data, Tree tree) {
	this.tree = tree;
		createInputs(parameters, data);
		if (execute()) {
			NodeBranchDataAdapter[] adapters = new NodeBranchDataAdapter[data.getCharacterStateCount()];
			for (int i = 0; i < adapters.length; i++) {
				adapters[i] = inputs.get(i).getSelectedAdapter();
			}
			parameters.setImportAdapters(adapters);
		}		
	}
	
	
	/**
	 * Tests if selected columns already exist as prompts the user to proceed.
	 * 
	 * @see info.bioinfweb.commons.swing.OkCancelApplyDialog#apply()
	 */
	@Override
	protected boolean apply() {
		StringBuffer message = new StringBuffer();
		message.append("The following node/branch data columns already exist in the tree:\n\n");
		
		boolean cancel = false;
		Iterator<NewNodeBranchDataInput> iterator = inputs.iterator();
		while (iterator.hasNext()) {
			NodeBranchDataAdapter adapter = iterator.next().getSelectedAdapter();
			if (!(adapter instanceof VoidNodeBranchDataAdapter)) {
				boolean columnExists; 
				if (adapter instanceof NewNodeBranchDataAdapter) {
					columnExists = IDManager.idExistsInSubtree(tree.getPaintStart(), ((NewNodeBranchDataAdapter)adapter).getID());
					if (columnExists) {
						message.append("Node/branch data with the ID \"");
						message.append(((NewNodeBranchDataAdapter)adapter).getID());
						message.append("\"\n");
					}
				}
				else {
					message.append(adapter.toString());
					message.append('\n');
					columnExists = true;
				}
				cancel = cancel || columnExists;
			}
		}
		message.append("\n\nDo you want to possibly overwrite entries in these columns?\n");
		message.append("(Only data of nodes referenced by the key column, could be overwritten.)");
		
		if (cancel) {
			cancel = (JOptionPane.showConfirmDialog(this, message, "Warning", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION);
		}
		return !cancel;
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setHelpCode(66); //TODO set correct help code
		setTitle("Import BayesTraits data");
		setSize(300, 200);
		getApplyButton().setVisible(false);
		setContentPane(getJContentPane());
	}


	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BoxLayout(getJContentPane(), BoxLayout.Y_AXIS));
			jContentPane.add(getImportPanel(), null);
			jContentPane.add(getButtonsPanel(), null);
		}
		return jContentPane;
	}


	/**
	 * This method initializes importPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getImportPanel() {
		if (importPanel == null) {
			importPanel = new JPanel();
			importPanel.setLayout(new GridBagLayout());
		}
		return importPanel;
	}
}

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
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NewNodeBranchDataInput;
import info.bioinfweb.wikihelp.client.OkCancelApplyWikiHelpDialog;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;



public class AssignBayesTraitsImportColumnsDialog extends OkCancelApplyWikiHelpDialog {	
	private Tree tree = null;	
	private JPanel jContentPane = null;
	private JPanel importPanel = null;
	private JPanel importInternalNodeNamesPanel = null;
	private AncestralStateImportParameters parameters = null;
	private AncestralStateData data = null;
	
	private List<CharacterInput> characterInputs = new ArrayList<CharacterInput>();
	private NewNodeBranchDataInput internalNodeNames = null;


	/**
	 * Creates a new instance of this dialog.
	 * @param owner the parent window
	 */
	public AssignBayesTraitsImportColumnsDialog(Frame owner) {
		super(owner, true, Main.getInstance().getWikiHelp());
		initialize();
		setLocationRelativeTo(owner);
	}


	private void createInputs(AncestralStateData data) {
		getImportPanel().removeAll();
		characterInputs.clear();
		
		Iterator<String> keySetIterator = data.getCharacterMap().keySet().iterator();
		int y = 0;
		while (keySetIterator.hasNext()) {
			CharacterInput input = new CharacterInput(getImportPanel(), y, data, tree, keySetIterator.next());
			y = input.getBottomY();
			characterInputs.add(input);
		}
		
		internalNodeNames = new NewNodeBranchDataInput(getImportInternalNodeNamesPanel(), 1, y, true);
		internalNodeNames.setAdapters(tree, false, true, false, false, true, "Do not import internal node names");
		internalNodeNames.setSelectedAdapter(NewHiddenBranchDataAdapter.class);
		internalNodeNames.setID("Internal node names");
		
		pack();
	}
	
	
	public boolean execute(AncestralStateImportParameters parameters, AncestralStateData data, Tree tree) {
		this.tree = tree;
		createInputs(data);
		this.parameters = parameters;
		this.data = data;
		return execute();
	}
	
	
	/**
	 * Tests if selected columns already exist as prompts the user to proceed.
	 * 
	 * @see info.bioinfweb.commons.swing.OkCancelApplyDialog#apply()
	 */
	@Override
	protected boolean apply() {
		NodeBranchDataAdapter[] importAdapters = new NodeBranchDataAdapter[data.getCharacterStateCount()];
		String[] labelIDs = new String[data.getCharacterCount()];
		parameters.setImportAdapters(importAdapters);
		parameters.setPieChartLabelIDs(labelIDs);
		Iterator<String> keySetIterator = data.getCharacterMap().keySet().iterator();
		int importAdapterIndexStart = 0;
		int labelIDsIndexStart = 0;
		while (keySetIterator.hasNext()) {
			characterInputs.get(labelIDsIndexStart).assignParameters(parameters, importAdapterIndexStart, labelIDsIndexStart);
			importAdapterIndexStart += data.getStateCountPerCharacter(keySetIterator.next());
			labelIDsIndexStart += 1;
		}
		parameters.setInternalNodeNamesAdapter(internalNodeNames.getSelectedAdapter());
		
		StringBuffer message = new StringBuffer();
		message.append("The following node/branch data columns already exist in the tree:\n\n");
		
		boolean cancel = false;
		
		for (int i = 0; i < parameters.getImportAdapters().length; i++) {
			NodeBranchDataAdapter adapter = parameters.getImportAdapters()[i];
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
		setSize(400, 300);
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
			jContentPane.add(getImportInternalNodeNamesPanel(), null);
			jContentPane.add(getButtonsPanel(), null);
		}
		return jContentPane;
	}

	
	private JPanel getImportInternalNodeNamesPanel() {
		if (importInternalNodeNamesPanel == null) {
			importInternalNodeNamesPanel = new JPanel();
			importInternalNodeNamesPanel.setLayout(new GridBagLayout());
			importInternalNodeNamesPanel.setBorder(new TitledBorder(null, "Import internal node names", TitledBorder.LEADING, TitledBorder.TOP, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
		}
		return importInternalNodeNamesPanel;
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
			importPanel.setBorder(new TitledBorder(null, "Import character data", TitledBorder.LEADING, TitledBorder.TOP, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
		}
		return importPanel;
	}
}

package info.bioinfweb.treegraph.gui.dialogs.io.ancestralstate;


import info.bioinfweb.treegraph.Main;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeNameAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.PieChartLabelAdapter;
import info.bioinfweb.treegraph.document.undo.file.ancestralstate.AncestralStateData;
import info.bioinfweb.treegraph.document.undo.file.ancestralstate.AncestralStateImportParameters;
import info.bioinfweb.treegraph.gui.dialogs.io.AssignImportColumnsDialog;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NewNodeBranchDataInput;




import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;



public class AssignBayesTraitsImportColumnsDialog extends AssignImportColumnsDialog {	
	private JPanel jContentPane = null;
	
	private JPanel importInternalNodeNamesPanel = null;
	private AncestralStateImportParameters parameters = null;
	private AncestralStateData data = null;
	private List<CharacterInput> characterInputs = new ArrayList<CharacterInput>();
	private NewNodeBranchDataInput internalNodeNames = null;
	
	private JLabel importCharacterDataLabel = null;
	private JLabel importInternalNodeNamesLabel = null;


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
		
		GridBagConstraints importCharacterDataLabelGBC = new GridBagConstraints();	
		importCharacterDataLabelGBC.gridx = 0;
		importCharacterDataLabelGBC.anchor = GridBagConstraints.WEST;
		importCharacterDataLabelGBC.gridy =  0;
		importCharacterDataLabelGBC.insets = new Insets(4, 6, 4, 0);
		importCharacterDataLabelGBC.gridwidth = GridBagConstraints.RELATIVE;
		importCharacterDataLabel = new JLabel();
		importCharacterDataLabel.setText("Import character data");
		getImportPanel().add(importCharacterDataLabel, importCharacterDataLabelGBC);
		
		Iterator<String> keySetIterator = data.getCharacterMap().keySet().iterator();
		int y = 1;
		while (keySetIterator.hasNext()) {
			CharacterInput input = new CharacterInput(getImportPanel(), y, data, tree, keySetIterator.next());
			y = input.getBottomY();
			characterInputs.add(input);
		}
		
		GridBagConstraints importInternalNodeNamesLabelGBC = new GridBagConstraints();	
		importInternalNodeNamesLabelGBC.gridx = 0;
		importInternalNodeNamesLabelGBC.anchor = GridBagConstraints.WEST;
		importInternalNodeNamesLabelGBC.gridy =  y;
		importInternalNodeNamesLabelGBC.insets = new Insets(4, 6, 4, 0);
		importInternalNodeNamesLabelGBC.gridwidth = GridBagConstraints.RELATIVE;
		importInternalNodeNamesLabel = new JLabel();
		importInternalNodeNamesLabel.setText("Import internal node names");
		getImportInternalNodeNamesPanel().add(importInternalNodeNamesLabel, importInternalNodeNamesLabelGBC);
		
		internalNodeNames = new NewNodeBranchDataInput(getImportInternalNodeNamesPanel(), 1, y + 1, true);
		internalNodeNames.setAdapters(tree, false, true, false, false, true, "Do not import internal node names");
		internalNodeNames.setSelectedAdapter(NodeNameAdapter.class);
		internalNodeNames.setID("");
		
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
		
		NodeBranchDataAdapter[] allAdapters = new NodeBranchDataAdapter[parameters.getImportAdapters().length + parameters.getPieChartLabelIDs().length];
		int adapterCount = parameters.getImportAdapters().length;
		for (int i = 0; i < adapterCount; i++) {
			allAdapters[i] = parameters.getImportAdapters()[i];
		}
		for (int i = 0; i < parameters.getPieChartLabelIDs().length; i++) {
			allAdapters[i + adapterCount] = new PieChartLabelAdapter(parameters.getPieChartLabelIDs()[i]);
		}
		
		return checkSelectedAdapters(allAdapters);
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
			jContentPane.add(getScrollPane());
			jContentPane.add(getImportInternalNodeNamesPanel(), null);
			jContentPane.add(getButtonsPanel(), null);
		}
		return jContentPane;
	}

	
	private JPanel getImportInternalNodeNamesPanel() {
		if (importInternalNodeNamesPanel == null) {
			importInternalNodeNamesPanel = new JPanel();
			importInternalNodeNamesPanel.setLayout(new GridBagLayout());
		}
		return importInternalNodeNamesPanel;
	}
}

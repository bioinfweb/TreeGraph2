package info.bioinfweb.treegraph.gui.dialogs.io.ancestralstate;


import info.bioinfweb.treegraph.Main;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeNameAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.PieChartLabelAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.VoidNodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.file.ancestralstate.AncestralStateData;
import info.bioinfweb.treegraph.document.undo.file.ancestralstate.AncestralStateImportParameters;
import info.bioinfweb.treegraph.gui.dialogs.io.AssignImportColumnsDialog;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NewNodeBranchDataInput;




import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
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
	
	private int bottomY;


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
		importCharacterDataLabelGBC.weighty = 2.0;
		importCharacterDataLabel = new JLabel();
		importCharacterDataLabel.setText("Import character data");
		getImportPanel().add(importCharacterDataLabel, importCharacterDataLabelGBC);
		
		Iterator<String> keySetIterator = data.getCharacterMap().keySet().iterator();
		bottomY = 1;
		while (keySetIterator.hasNext()) {
			CharacterInput input = new CharacterInput(getImportPanel(), bottomY, data, tree, keySetIterator.next());
			bottomY = input.getBottomY();
			characterInputs.add(input);
		}

		pack();
		setLocationRelativeTo(getOwner());
		
		// Make sure the task bar does not overlap with the dialog:
		Rectangle bounds = getBounds();
		setBounds(bounds.x, bounds.y, bounds.width, Math.min(bounds.height, 
				Math.round((float)Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.9f)));
	}
	
	
	public boolean execute(AncestralStateImportParameters parameters, AncestralStateData data, Tree tree) {
		this.tree = tree;
		this.parameters = parameters;
		this.data = data;

		createInputs(data);
		internalNodeNames.setAdapters(tree, false, true, false, false, true, "Do not import internal node names");
		internalNodeNames.setSelectedAdapter(NodeNameAdapter.class);
		internalNodeNames.setID("");

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
		
		int importAdapterCount = parameters.getImportAdapters().length;
		int pieChartLabelCount = parameters.getPieChartLabelIDs().length;
		NodeBranchDataAdapter[] allAdapters = new NodeBranchDataAdapter[1 + importAdapterCount + pieChartLabelCount];
		allAdapters[0] = parameters.getInternalNodeNamesAdapter();
		for (int i = 0; i < importAdapterCount; i++) {
			allAdapters[i + 1] = parameters.getImportAdapters()[i];
		}
		for (int i = 0; i < pieChartLabelCount; i++) {
			String pieChartLabelID = parameters.getPieChartLabelIDs()[i];
			if (pieChartLabelID != null) {
				allAdapters[i + importAdapterCount + 1] = new PieChartLabelAdapter(pieChartLabelID);
			}
			else {
				allAdapters[i + importAdapterCount + 1] = new VoidNodeBranchDataAdapter("");
			}
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
		setSize(380, 400);
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
			jContentPane.setLayout(new BoxLayout(jContentPane, BoxLayout.Y_AXIS));
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
			GridBagConstraints importInternalNodeNamesLabelGBC = new GridBagConstraints();	
			importInternalNodeNamesLabelGBC.gridx = 0;
			importInternalNodeNamesLabelGBC.anchor = GridBagConstraints.WEST;
			importInternalNodeNamesLabelGBC.gridy =  bottomY;
			importInternalNodeNamesLabelGBC.insets = new Insets(4, 6, 4, 0);
			importInternalNodeNamesLabelGBC.gridwidth = GridBagConstraints.RELATIVE;
			importInternalNodeNamesLabel = new JLabel();
			importInternalNodeNamesLabel.setText("Import internal node names");
			importInternalNodeNamesPanel.add(importInternalNodeNamesLabel, importInternalNodeNamesLabelGBC);
			
			internalNodeNames = new NewNodeBranchDataInput(importInternalNodeNamesPanel, 1, bottomY + 1, true);
		}
		return importInternalNodeNamesPanel;
	}
}

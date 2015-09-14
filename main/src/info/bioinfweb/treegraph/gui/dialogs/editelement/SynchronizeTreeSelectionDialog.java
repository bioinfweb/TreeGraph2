package info.bioinfweb.treegraph.gui.dialogs.editelement;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import info.bioinfweb.treegraph.document.nodebranchdata.NodeNameAdapter;
import info.bioinfweb.treegraph.document.undo.ImportTextElementDataParameters;
import info.bioinfweb.treegraph.gui.dialogs.EditDialog;
import info.bioinfweb.treegraph.gui.dialogs.ImportTextElementDataParametersPanel;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NewNodeBranchDataInput;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;

public class SynchronizeTreeSelectionDialog extends EditDialog {	
	private JPanel jContentPane = null;
	private JPanel inputAdaptersPanel = null;
	private JLabel inputAdaptersLabel = null;
	private NewNodeBranchDataInput inputAdapters = null;
	private ImportTextElementDataParametersPanel textElementDataParametersPanel = null;
	private ImportTextElementDataParameters textElementDataParameters = null;
	
	
	public SynchronizeTreeSelectionDialog(MainFrame mainFrame) {
		super(mainFrame);
//		setHelpCode(); //TODO set correct help code
		initialize();
		setLocationRelativeTo(mainFrame);
	}

	@Override
	protected boolean onExecute() {
		MainFrame.getInstance().getT
		getTextElementDataParameters().setCaseSensitive(getTextElementDataParametersPanel().getCaseCheckBox().isEnabled());
		getTextElementDataParameters().setDistinguishSpaceUnderscore(getTextElementDataParametersPanel().getDistinguishSpaceUnderscoreCheckBox().isEnabled());
		getTextElementDataParameters().setIgnoreWhitespace(getTextElementDataParametersPanel().getIgnoreWhitespaceCheckBox().isEnabled());
		getTextElementDataParameters().setParseNumericValues(getTextElementDataParametersPanel().getParseNumericValuesCheckBox().isEnabled());
		return true;
	}

	@Override
	protected boolean apply() {
		// TODO Auto-generated method stub
		return true;
	}
	
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setTitle("Synchronize Tree Selection");
		setContentPane(getJContentPane());
		pack();
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
			jContentPane.add(getInputAdaptersPanel(), null);
			jContentPane.add(getTextElementDataParametersPanel(), null);
			jContentPane.add(getButtonsPanel(), null);
			getApplyButton().setVisible(false);
		}
		return jContentPane;
	}
	
	
	private JPanel getInputAdaptersPanel() {
		if (inputAdaptersPanel == null) {
			inputAdaptersPanel = new JPanel();
			inputAdaptersPanel.setLayout(new GridBagLayout());
			GridBagConstraints inputAdaptersGBC = new GridBagConstraints();	
			inputAdaptersGBC.gridx = 0;
			inputAdaptersGBC.anchor = GridBagConstraints.WEST;
			inputAdaptersGBC.gridy =  1;
			inputAdaptersGBC.insets = new Insets(4, 6, 4, 0);
			inputAdaptersGBC.gridwidth = GridBagConstraints.RELATIVE;
			inputAdaptersLabel = new JLabel();
			inputAdaptersLabel.setText("Input Adapters");
			inputAdaptersPanel.add(inputAdaptersLabel, inputAdaptersGBC);
			
			inputAdapters = new NewNodeBranchDataInput(inputAdaptersPanel, 0, 2, true);
			inputAdapters.setAdapters(null, false, true, false, false, true, "");
			inputAdapters.setSelectedAdapter(NodeNameAdapter.class);
			inputAdapters.setID("");
		}
		return inputAdaptersPanel;
	}	
	
	
	private ImportTextElementDataParametersPanel getTextElementDataParametersPanel() {
		if (textElementDataParametersPanel == null) {
			textElementDataParametersPanel = new ImportTextElementDataParametersPanel();
		}
		return textElementDataParametersPanel;
	}
	

	public ImportTextElementDataParameters getTextElementDataParameters() {
		if (textElementDataParameters == null) {
			textElementDataParameters = new ImportTextElementDataParameters();
		}
		return textElementDataParameters;
	}	
}

package info.bioinfweb.treegraph.gui.dialogs.editelement;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import info.bioinfweb.treegraph.gui.dialogs.EditDialog;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NodeBranchDataInput;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;



public class DefaultDocumentAdapterDialog extends EditDialog {
	private JPanel jContentPane = null;
	private JPanel defaultDocumentAdaptersPanel = null;
	private JLabel defaultSupportAdapterLabel = null;
	private NodeBranchDataInput defaultLeafAdapter = null;
	private NodeBranchDataInput defaultSupportAdapter = null;
	
	
	public DefaultDocumentAdapterDialog(MainFrame mainFrame) {
		super(mainFrame);
		initialize();
		setLocationRelativeTo(mainFrame);
	}
	

	@Override
	protected boolean onExecute() {
		defaultLeafAdapter.setSelectedAdapter(getDocument().getDefaultLeafAdapter());
		defaultSupportAdapter.setSelectedAdapter(getDocument().getDefaultSupportAdapter());
		return true;
	}

	
	@Override
	protected boolean apply() {
		getDocument().setDefaultLeafAdapter(defaultLeafAdapter.getSelectedAdapter());
		System.out.println(getDocument().getDefaultLeafAdapter());
		getDocument().setDefaultSupportAdapter(defaultSupportAdapter.getSelectedAdapter());
		System.out.println(getDocument().getDefaultSupportAdapter());
		return true;
	}
	
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
//	setHelpCode(); //TODO set correct help code
		setTitle("Set default document adapters");
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
			jContentPane.add(getDefaultDocumentAdaptersPanel(), null);
			jContentPane.add(getButtonsPanel(), null);
			getApplyButton().setVisible(false);
		}
		return jContentPane;
	}
	
	
	private JPanel getDefaultDocumentAdaptersPanel() {
		if (defaultDocumentAdaptersPanel == null) {
			defaultDocumentAdaptersPanel = new JPanel();
			defaultDocumentAdaptersPanel.setLayout(new GridBagLayout());
			
			GridBagConstraints defaultLeafAdapterLabelGBC = new GridBagConstraints();	
			defaultLeafAdapterLabelGBC.gridx = 0;
			defaultLeafAdapterLabelGBC.anchor = GridBagConstraints.WEST;
			defaultLeafAdapterLabelGBC.gridy =  0;
			defaultLeafAdapterLabelGBC.insets = new Insets(4, 6, 4, 0);
			defaultLeafAdapterLabelGBC.gridwidth = GridBagConstraints.RELATIVE;
			defaultSupportAdapterLabel = new JLabel();
			defaultSupportAdapterLabel.setText("Set default leaf adapter:");
			defaultDocumentAdaptersPanel.add(defaultSupportAdapterLabel, defaultLeafAdapterLabelGBC);
			
			defaultLeafAdapter = new NodeBranchDataInput(defaultDocumentAdaptersPanel, 0, 1);
			defaultLeafAdapter.setAdapters(null, true, true, false, false, false, "");			
			
			GridBagConstraints defaultSupportAdapterLabelGBC = new GridBagConstraints();	
			defaultSupportAdapterLabelGBC.gridx = 0;
			defaultSupportAdapterLabelGBC.anchor = GridBagConstraints.WEST;
			defaultSupportAdapterLabelGBC.gridy =  2;
			defaultSupportAdapterLabelGBC.insets = new Insets(4, 6, 4, 0);
			defaultSupportAdapterLabelGBC.gridwidth = GridBagConstraints.RELATIVE;
			defaultSupportAdapterLabel = new JLabel();
			defaultSupportAdapterLabel.setText("Set default support adapter:");
			defaultDocumentAdaptersPanel.add(defaultSupportAdapterLabel, defaultSupportAdapterLabelGBC);
			
			defaultSupportAdapter = new NodeBranchDataInput(defaultDocumentAdaptersPanel, 0, 3);
			defaultSupportAdapter.setAdapters(null, false, true, true, true, false, "No support values available");			
		}
		return defaultDocumentAdaptersPanel;
	}	
}

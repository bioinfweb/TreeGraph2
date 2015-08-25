package info.bioinfweb.treegraph.gui.dialogs.io;


import info.bioinfweb.treegraph.document.IDManager;
import info.bioinfweb.treegraph.document.io.MultipleDocumentIterator;
import info.bioinfweb.treegraph.document.io.ReadWriteFactory;
import info.bioinfweb.treegraph.document.io.ReadWriteFormat;
import info.bioinfweb.treegraph.document.io.nexus.NexusFilter;
import info.bioinfweb.treegraph.document.nodebranchdata.BranchLengthAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NewNodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeNameAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.TextElementDataAdapter;
import info.bioinfweb.treegraph.document.undo.file.NodeSupportEdit;
import info.bioinfweb.treegraph.gui.dialogs.io.loadlogger.LoadLoggerDialog;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NewNodeBranchDataInput;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NodeBranchDataInput;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;



import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import javax.swing.border.TitledBorder;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Dimension;
import java.io.File;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.JLabel;



/**
 * Dialog used to calculate node frequencies from a set of tree topologies. 
 * 
 * @author Ben St&ouml;ver
 */
public class NodeSupportDialog extends FileDialog {
	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JPanel fileChooserPanel = null;
	private JFileChooser fileChooser = null;
	private NexusFilter nexusFilter = null;
	private JPanel nodeBranchDataPanel = null;
	private JPanel normalisationPanel = null;
	private JCheckBox normalisationCheckBox = null;
	private JTextField normalisationTextField = null;
	private NewNodeBranchDataInput destInput = null;
	private NodeBranchDataInput terminalInput = null;
	private JLabel destInputLabel = null;
	private JLabel terminalsInputLabel = null; 
	private double normalisationBorder = 0; 
	

	/**
	 * @param owner
	 */
	public NodeSupportDialog(Frame owner) {
		super(owner, FileDialog.Option.FILE_MUST_EXIST);
		initialize();
		setHelpCode(59);
		
	}

	
	@Override
	protected boolean onApply(File file) {
		boolean result = true; 
		NodeBranchDataAdapter adapter = getDestInput().getSelectedAdapter(); 
		if (normalisationCheckBox.isSelected()){
			normalisationBorder =  Double.valueOf(normalisationTextField.getText());
		}
		if (!normalisationCheckBox.isSelected())
		{
			normalisationBorder = -1; 
		}
		if (adapter instanceof NewNodeBranchDataAdapter){
			String id = ((NewNodeBranchDataAdapter)adapter).getID();
			if (IDManager.idExistsInSubtree(getDocument().getTree().getPaintStart(), id)){
				JOptionPane.showMessageDialog(this, "The ID " + id + " already exists", "Error", JOptionPane.ERROR_MESSAGE);
				result = false; 
			}
			
		}
		if (result) {
			NodeSupportEdit edit = new NodeSupportEdit(getDocument(), 
					(TextElementDataAdapter)getTerminalInput().getSelectedAdapter(),
					getDestInput().getSelectedAdapter(), NodeNameAdapter.getSharedInstance(), false, 
					new MultipleDocumentIterator(getFileChooser().getSelectedFiles(), LoadLoggerDialog.getInstance(),
					    NodeNameAdapter.getSharedInstance(), BranchLengthAdapter.getSharedInstance(), false), normalisationBorder);
			getDocument().executeEdit(edit);
			
		}
		return result;
		
	}


	@Override
	protected boolean onExecute() {
		if (!getDocument().getTree().isEmpty()) {
			getDestInput().setAdapters(getDocument().getTree(), true, false, false, true, false, false);
			getTerminalInput().setAdapters(getDocument().getTree(), true, false, false, false, true, false);
			return true;
		}
		else {
			JOptionPane.showMessageDialog(MainFrame.getInstance(), 
					"The document cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}


	
	private NewNodeBranchDataInput getDestInput() {
		getNodeBranchDataPanel();
		return destInput;
	}


	private NodeBranchDataInput getTerminalInput() {
		getNodeBranchDataPanel();
		return terminalInput;
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(600, 500);
		this.setContentPane(getJContentPane());
		getApplyButton().setVisible(false);
		getCancelButton().setVisible(true);
	}
	

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getFileChooserPanel() {
		if (fileChooserPanel == null) {
			fileChooserPanel = new JPanel();
			fileChooserPanel.setLayout(new BoxLayout(getFileChooserPanel(), BoxLayout.Y_AXIS));
			fileChooserPanel.setBorder(BorderFactory.createTitledBorder(null, "Source file", 
					TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
					new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			fileChooserPanel.add(getFileChooser(), null);
		}
		return fileChooserPanel;
	}
	
	
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BoxLayout(getJContentPane(), BoxLayout.Y_AXIS));
			jContentPane.setPreferredSize(new Dimension(520, 402));
			jContentPane.add(getFileChooserPanel(), null);
			jContentPane.add(getNodeBranchDataPanel(), null);
			jContentPane.add(getNormalisationPanel(), null);
			jContentPane.add(getButtonsPanel(), null);
			
		}
		return jContentPane;
	}

	
	/**
	 * This method initializes fileChooserPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	@Override
	protected JFileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new JFileChooser();
			fileChooser.setMultiSelectionEnabled(true);
			fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
			fileChooser.setControlButtonsAreShown(false);
			
			nexusFilter = new NexusFilter();
			if (fileChooser.getFileFilter() != null) {  // "Alle Datein"-Filter entfernen
				fileChooser.removeChoosableFileFilter(fileChooser.getFileFilter());
			}
			fileChooser.addChoosableFileFilter(nexusFilter);
			fileChooser.addChoosableFileFilter(ReadWriteFactory.getInstance().getFilter(ReadWriteFormat.NEWICK));
			fileChooser.setFileFilter(nexusFilter);
			
		}
		return fileChooser;
	}


	/**
	 * This method initializes nodeBranchDataPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getNodeBranchDataPanel() {
		if (nodeBranchDataPanel == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.gridy = 2;
			terminalsInputLabel = new JLabel();
			terminalsInputLabel.setText("Terminals identification colum: ");
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 1;
			destInputLabel = new JLabel();
			destInputLabel.setText("Node frequency colum: ");
			nodeBranchDataPanel = new JPanel();
			nodeBranchDataPanel.setLayout(new GridBagLayout());
			nodeBranchDataPanel.setBorder(BorderFactory.createTitledBorder(null, "Import column", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			nodeBranchDataPanel.add(destInputLabel, gridBagConstraints2);
			nodeBranchDataPanel.add(terminalsInputLabel, gridBagConstraints3);
			destInput = new NewNodeBranchDataInput(nodeBranchDataPanel, 1, 1, true);
			terminalInput = new NodeBranchDataInput(nodeBranchDataPanel, 1, 2);
			//TODO Sicherstellen das terminalImput nur TextElementAdapter zurückgeben kann.
			
		}
		return nodeBranchDataPanel;
	}


	/**
	 * This method initializes normalisationPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getNormalisationPanel() {
		if (normalisationPanel == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.anchor = GridBagConstraints.CENTER;
			gridBagConstraints1.ipadx = 0;
			gridBagConstraints1.gridwidth = 1;
			gridBagConstraints1.gridx = 1;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.ipadx = 0;
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			gridBagConstraints.weightx = 0.0;
			gridBagConstraints.gridy = 0;
			normalisationPanel = new JPanel();
			normalisationPanel.setLayout(new GridBagLayout());
			normalisationPanel.setBorder(BorderFactory.createTitledBorder(null, "Normalisation", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			normalisationPanel.add(getNormalisationCheckBox(), gridBagConstraints);
			normalisationPanel.add(getNormalisationTextField(), gridBagConstraints1);
		}
		return normalisationPanel;
	}


	/**
	 * This method initializes normalisationCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getNormalisationCheckBox() {
		if (normalisationCheckBox == null) {
			normalisationCheckBox = new JCheckBox();
			normalisationCheckBox.setText("Normalise to specific border:");
			normalisationCheckBox.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					
					getNormalisationTextField().setEnabled(getNormalisationCheckBox().isSelected());
					
					}
			});
		}
		return normalisationCheckBox;
	}


	/**
	 * This method initializes normalisationTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getNormalisationTextField() {
		if (normalisationTextField == null) {
			normalisationTextField = new JTextField();
			normalisationTextField.setEnabled(false);
		}
		return normalisationTextField;
	}
}
package info.bioinfweb.treegraph.gui.dialogs.editelement;



import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import info.bioinfweb.treegraph.document.undo.ImportTextElementDataParameters;
import info.bioinfweb.treegraph.gui.dialogs.EditDialog;
import info.bioinfweb.treegraph.gui.dialogs.ImportTextElementDataParametersPanel;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;



public class SynchronizeTreeSelectionDialog extends EditDialog {
	private JPanel jContentPane = null;
//	private JPanel compareParametersPanel = null;
//	JLabel textElementDataParametersLabel = null;
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
		//MainFrame.getInstance().getT
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
		setTitle("Tree selection synchronization compare parameters");
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
			jContentPane.add(getTextElementDataParametersPanel(), null);
			jContentPane.add(getButtonsPanel(), null);
			getApplyButton().setVisible(false);
		}
		return jContentPane;
	}
	
	
	private ImportTextElementDataParametersPanel getTextElementDataParametersPanel() {
		if (textElementDataParametersPanel == null) {
			textElementDataParametersPanel = new ImportTextElementDataParametersPanel();
			textElementDataParametersPanel.setBorder(BorderFactory.createTitledBorder(null, "", 
					TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
					new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
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

package info.bioinfweb.treegraph.gui.dialogs.io.ancestralstate;


import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.nodebranchdata.NewHiddenBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.file.ancestralstate.AncestralStateData;
import info.bioinfweb.treegraph.document.undo.file.ancestralstate.AncestralStateImportParameters;


import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NewNodeBranchDataInput;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;



/**
 * Collection of GUI elements used to defined the import target for information associated with character
 * in ancestral state reconstruction.
 * 
 * @author Sarah Wiechers 
 * @author Ben St&ouml;ver
 * @since 2.5.0 
 */
public class CharacterInput {
	private static final int ROWS_PER_CHARACTER = 7;
	private static final int HEADING_ROWS_COUNT = 2;
	
	
	private List<NewNodeBranchDataInput> stateInputs = new ArrayList<NewNodeBranchDataInput>();
	
	private JCheckBox importCharacterCheckbox = null;
	private JLabel prefixLabel = null;
	private JTextField prefixIDTextField = null;
	
	private JLabel typeHeadingLabel = null;
	private JLabel idHeadingLabel = null;
	
	private JCheckBox pieChartLabelCheckbox = null;
	private JTextField pieChartLabelIDTextField = null;
	

	public CharacterInput(JPanel panel, int x, int y, AncestralStateImportParameters parameters, AncestralStateData data, Tree tree) {
		super();		
		panel.removeAll();
		stateInputs.clear();
		createCharacterInput(panel, x, y, parameters, data, tree);
	}
	
	
	private void createCharacterInput(JPanel panel, int x, int y, AncestralStateImportParameters parameters, 
					AncestralStateData data, Tree tree) {
		
		GridBagConstraints importCharacterCheckboxGBC = new GridBagConstraints();
		importCharacterCheckboxGBC.gridx = 0;
		importCharacterCheckboxGBC.anchor = GridBagConstraints.WEST;
		importCharacterCheckboxGBC.gridy = y * ROWS_PER_CHARACTER;
		importCharacterCheckboxGBC.insets = new Insets(0, 0, 8, 0);
		importCharacterCheckbox = new JCheckBox();
		importCharacterCheckbox.setSelected(true); // TODO add listener and method setContentsEnabled()
		GridBagConstraints prefixGBC = new GridBagConstraints();	
		prefixGBC.gridx = 1;
		prefixGBC.anchor = GridBagConstraints.WEST;
		prefixGBC.gridy = y * ROWS_PER_CHARACTER;
		prefixGBC.insets = new Insets(0, 0, 8, 0);
		prefixLabel = new JLabel();
		prefixLabel.setText("Prefix ID:");
		GridBagConstraints prefixIDGBC = new GridBagConstraints();
		prefixIDGBC.gridx = 2;
		prefixIDGBC.anchor = GridBagConstraints.WEST;
		prefixIDGBC.gridy = y * ROWS_PER_CHARACTER;
		prefixIDGBC.insets = new Insets(0, 0, 8, 0);
		prefixIDTextField = new JTextField();
		prefixIDTextField.setText("default prefix"); //TODO set prefix
		GridBagConstraints typeHeadingGBC = new GridBagConstraints();
		typeHeadingGBC.gridx = 1;
		typeHeadingGBC.anchor = GridBagConstraints.WEST;
		typeHeadingGBC.gridy = y * ROWS_PER_CHARACTER + 1;
		typeHeadingGBC.insets = new Insets(0, 0, 8, 0);
		typeHeadingLabel = new JLabel();
		typeHeadingLabel.setText("Node data type:");
		GridBagConstraints idHeadingGBC = new GridBagConstraints();
		idHeadingGBC.gridx = 2;
		idHeadingGBC.gridy = y * ROWS_PER_CHARACTER + 1;
		idHeadingGBC.anchor = GridBagConstraints.WEST;
		idHeadingGBC.insets = new Insets(0, 0, 8, 0);
		idHeadingLabel = new JLabel();
		idHeadingLabel.setText("ID:                ");  // increase widths of text fields		
		panel.add(importCharacterCheckbox, importCharacterCheckboxGBC);
		panel.add(prefixLabel, prefixGBC);
		panel.add(prefixIDTextField, prefixIDGBC);
		panel.add(typeHeadingLabel, typeHeadingGBC);
		panel.add(idHeadingLabel, idHeadingGBC);			
	
		for (int j = 1; j <= data.getCharacterStateCount(); j++) {
			NewNodeBranchDataInput input = new NewNodeBranchDataInput(panel, 1, y * ROWS_PER_CHARACTER + j + HEADING_ROWS_COUNT - 1, true);
			input.setAdapters(tree, false, false, false, false, true, false);
			input.setSelectedAdapter(NewHiddenBranchDataAdapter.class);
			input.setID("default ID");
			stateInputs.add(input);
		}
		
		GridBagConstraints pieChartLabelCheckboxGBC = new GridBagConstraints();
		pieChartLabelCheckboxGBC.gridx = 1;
		pieChartLabelCheckboxGBC.anchor = GridBagConstraints.WEST;
		pieChartLabelCheckboxGBC.gridy = ROWS_PER_CHARACTER * (y + 1) - 1;
		pieChartLabelCheckboxGBC.insets = new Insets(0, 0, 8, 0);
		pieChartLabelCheckbox = new JCheckBox();
		pieChartLabelCheckbox.setText("Pie chart ID:");
		pieChartLabelCheckbox.setSelected(true);
		GridBagConstraints pieChartLabelIDGBC = new GridBagConstraints();
		pieChartLabelIDGBC.gridx = 2;
		pieChartLabelIDGBC.anchor = GridBagConstraints.WEST;
		pieChartLabelIDGBC.gridy = ROWS_PER_CHARACTER * (y + 1) - 1;
		pieChartLabelIDGBC.insets = new Insets(0, 0, 8, 0);
		pieChartLabelIDTextField = new JTextField();
		pieChartLabelIDTextField.setText("default pie chart label"); //TODO set default label
		panel.add(pieChartLabelCheckbox, pieChartLabelCheckboxGBC);
		panel.add(pieChartLabelIDTextField, pieChartLabelIDGBC);
	}
}

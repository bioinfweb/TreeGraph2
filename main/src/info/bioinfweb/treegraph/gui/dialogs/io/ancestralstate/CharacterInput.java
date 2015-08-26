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



public class CharacterInput {
	private static final int ROW_COUNT = 7;
	private static final int USED_ROWS = 2;
	
	private List<NewNodeBranchDataInput> inputs = new ArrayList<NewNodeBranchDataInput>();
	
	private JCheckBox prefixCheckbox = null;
	private JLabel prefixLabel = null;
	private JTextField prefixIDTextField = null;
	
	private JLabel typeLabel = null;
	private JLabel idLabel = null;
	
	private JCheckBox pieChartLabelCheckbox = null;
	private JTextField pieChartLabelIDTextField = null;
	
	

	public CharacterInput(JPanel panel, int x, int y, AncestralStateImportParameters parameters, AncestralStateData data, Tree tree ) {
		super();		
		panel.removeAll();
		inputs.clear();
		createCharacterInput(panel, x, y, parameters, data, tree);
	}
	
	
	private void createCharacterInput(JPanel panel, int x, int y, AncestralStateImportParameters parameters, AncestralStateData data, Tree tree) {
		GridBagConstraints prefixCheckboxGBC = new GridBagConstraints();
		prefixCheckboxGBC.gridx = 0;
		prefixCheckboxGBC.anchor = GridBagConstraints.WEST;
		prefixCheckboxGBC.gridy = y * ROW_COUNT;
		prefixCheckboxGBC.insets = new Insets(0, 0, 8, 0);
		prefixCheckbox = new JCheckBox();
		prefixCheckbox.setSelected(true); // TODO add listener and method setContentsEnabled()
		GridBagConstraints prefixGBC = new GridBagConstraints();	
		prefixGBC.gridx = 1;
		prefixGBC.anchor = GridBagConstraints.WEST;
		prefixGBC.gridy = y * ROW_COUNT;
		prefixGBC.insets = new Insets(0, 0, 8, 0);
		prefixLabel = new JLabel();
		prefixLabel.setText("Prefix ID:");
		GridBagConstraints prefixIDGBC = new GridBagConstraints();
		prefixIDGBC.gridx = 2;
		prefixIDGBC.anchor = GridBagConstraints.WEST;
		prefixIDGBC.gridy = y * ROW_COUNT;
		prefixIDGBC.insets = new Insets(0, 0, 8, 0);
		prefixIDTextField = new JTextField();
		prefixIDTextField.setText("default prefix"); //TODO set prefix
		GridBagConstraints typeGBC = new GridBagConstraints();
		typeGBC.gridx = 1;
		typeGBC.anchor = GridBagConstraints.WEST;
		typeGBC.gridy = y * ROW_COUNT + 1;
		typeGBC.insets = new Insets(0, 0, 8, 0);
		typeLabel = new JLabel();
		typeLabel.setText("Node data type:");
		GridBagConstraints idGBC = new GridBagConstraints();
		idGBC.gridx = 2;
		idGBC.gridy = y * ROW_COUNT + 1;
		idGBC.anchor = GridBagConstraints.WEST;
		idGBC.insets = new Insets(0, 0, 8, 0);
		idLabel = new JLabel();
		idLabel.setText("ID:                ");  // increase widths of text fields		
		panel.add(prefixCheckbox, prefixCheckboxGBC);
		panel.add(prefixLabel, prefixGBC);
		panel.add(prefixIDTextField, prefixIDGBC);
		panel.add(typeLabel, typeGBC);
		panel.add(idLabel, idGBC);			
	
		for (int j = 1; j <= data.getCharacterStateCount(); j++) {
			NewNodeBranchDataInput input = new NewNodeBranchDataInput(panel, 1, y * ROW_COUNT + j + USED_ROWS - 1, true);
			input.setAdapters(tree, false, false, false, false, true, false);
			input.setSelectedAdapter(NewHiddenBranchDataAdapter.class);
			input.setID("default ID");
			inputs.add(input);
		}
		
		GridBagConstraints pieChartLabelCheckboxGBC = new GridBagConstraints();
		pieChartLabelCheckboxGBC.gridx = 1;
		pieChartLabelCheckboxGBC.anchor = GridBagConstraints.WEST;
		pieChartLabelCheckboxGBC.gridy = ROW_COUNT * (y + 1) - 1;
		pieChartLabelCheckboxGBC.insets = new Insets(0, 0, 8, 0);
		pieChartLabelCheckbox = new JCheckBox();
		pieChartLabelCheckbox.setText("Pie chart ID:");
		pieChartLabelCheckbox.setSelected(true);
		GridBagConstraints pieChartLabelIDGBC = new GridBagConstraints();
		pieChartLabelIDGBC.gridx = 2;
		pieChartLabelIDGBC.anchor = GridBagConstraints.WEST;
		pieChartLabelIDGBC.gridy = ROW_COUNT * (y + 1) - 1;
		pieChartLabelIDGBC.insets = new Insets(0, 0, 8, 0);
		pieChartLabelIDTextField = new JTextField();
		pieChartLabelIDTextField.setText("default pie chart label"); //TODO set default label
		panel.add(pieChartLabelCheckbox, pieChartLabelCheckboxGBC);
		panel.add(pieChartLabelIDTextField, pieChartLabelIDGBC);
	}
}

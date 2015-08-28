package info.bioinfweb.treegraph.gui.dialogs.io.ancestralstate;


import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.nodebranchdata.NewHiddenBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.VoidNodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.undo.file.ancestralstate.AncestralStateData;


import info.bioinfweb.treegraph.document.undo.file.ancestralstate.AncestralStateImportParameters;
import info.bioinfweb.treegraph.gui.dialogs.nodebranchdatainput.NewNodeBranchDataInput;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;



/**
 * Collection of GUI elements used to define the import target for information associated with character
 * in ancestral state reconstruction.
 * 
 * @author Sarah Wiechers 
 * @author Ben St&ouml;ver
 * @since 2.5.0 
 */
public class CharacterInput {
	private static final int FIXED_ROWS_PER_CHARACTER = 3;
	private static final int HEADING_ROWS_COUNT = 2;
	
	private int bottomY = 0;	
	private List<NewNodeBranchDataInput> stateInputs = new ArrayList<NewNodeBranchDataInput>();
	
	private JCheckBox importCharacterCheckbox = null;
	private JLabel prefixLabel = null;
	private JTextField prefixIDTextField = null;
	
	private JLabel typeHeadingLabel = null;
	private JLabel idHeadingLabel = null;
	
	private JCheckBox pieChartLabelCheckbox = null;
	private JTextField pieChartLabelIDTextField = null;
	

	public CharacterInput(JPanel panel, int y, 
			AncestralStateData data, Tree tree, String prefixIDText) {
		
		super();		
		bottomY = createCharacterInput(panel, y, data, tree, prefixIDText);
	}
	
	
	private int createCharacterInput(JPanel panel, int y, AncestralStateData data, Tree tree, String prefixIDText) {
		int usedRows = y + FIXED_ROWS_PER_CHARACTER + data.getStateCountPerCharacter(prefixIDText);
		
		GridBagConstraints importCharacterCheckboxGBC = new GridBagConstraints();
		importCharacterCheckboxGBC.gridx = 0;
		importCharacterCheckboxGBC.anchor = GridBagConstraints.WEST;
		importCharacterCheckboxGBC.gridy = y;
		importCharacterCheckboxGBC.insets = new Insets(0, 0, 8, 0);
		importCharacterCheckbox = new JCheckBox();
		importCharacterCheckbox.setSelected(true);
		GridBagConstraints prefixGBC = new GridBagConstraints();	
		prefixGBC.gridx = 1;
		prefixGBC.anchor = GridBagConstraints.WEST;
		prefixGBC.gridy = y;
		prefixGBC.insets = new Insets(0, 0, 8, 0);
		prefixLabel = new JLabel();
		prefixLabel.setText("Prefix ID:");
		GridBagConstraints prefixIDGBC = new GridBagConstraints();
		prefixIDGBC.gridx = 2;
		prefixIDGBC.anchor = GridBagConstraints.WEST;
		prefixIDGBC.gridy = y;
		prefixIDGBC.insets = new Insets(0, 0, 8, 0);
		prefixIDGBC.fill = GridBagConstraints.HORIZONTAL;
		prefixIDTextField = new JTextField();
		prefixIDTextField.setText(prefixIDText);
		GridBagConstraints typeHeadingGBC = new GridBagConstraints();
		typeHeadingGBC.gridx = 1;
		typeHeadingGBC.anchor = GridBagConstraints.WEST;
		typeHeadingGBC.gridy = y + 1;
		typeHeadingGBC.insets = new Insets(0, 0, 8, 0);
		typeHeadingLabel = new JLabel();
		typeHeadingLabel.setText("Node data type:");
		GridBagConstraints idHeadingGBC = new GridBagConstraints();
		idHeadingGBC.gridx = 2;
		idHeadingGBC.gridy = y + 1;
		idHeadingGBC.anchor = GridBagConstraints.WEST;
		idHeadingGBC.insets = new Insets(0, 0, 8, 0);
		idHeadingLabel = new JLabel();
		idHeadingLabel.setText("ID:");	
		panel.add(importCharacterCheckbox, importCharacterCheckboxGBC);
		panel.add(prefixLabel, prefixGBC);
		panel.add(prefixIDTextField, prefixIDGBC);
		panel.add(typeHeadingLabel, typeHeadingGBC);
		panel.add(idHeadingLabel, idHeadingGBC);			
		
		Iterator<String> keySetIterator = data.getCharacterMap().get(prefixIDText).keySet().iterator();
		int counter = 1;
		while (keySetIterator.hasNext()) {	
			NewNodeBranchDataInput input = new NewNodeBranchDataInput(panel, 1, y + counter + HEADING_ROWS_COUNT - 1, true);
			input.setAdapters(tree, false, false, false, false, true, "");
			input.setSelectedAdapter(NewHiddenBranchDataAdapter.class);
			input.setID(prefixIDText + "." + keySetIterator.next());
			stateInputs.add(input);
			counter += 1;
		}
		
		GridBagConstraints pieChartLabelCheckboxGBC = new GridBagConstraints();
		pieChartLabelCheckboxGBC.gridx = 1;
		pieChartLabelCheckboxGBC.anchor = GridBagConstraints.WEST;
		pieChartLabelCheckboxGBC.gridy = usedRows - 1;
		pieChartLabelCheckboxGBC.insets = new Insets(0, 0, 8, 0);
		pieChartLabelCheckbox = new JCheckBox();
		pieChartLabelCheckbox.setText("Create pie chart label with ID:");
		pieChartLabelCheckbox.setSelected(true);
		GridBagConstraints pieChartLabelIDGBC = new GridBagConstraints();
		pieChartLabelIDGBC.gridx = 2;
		pieChartLabelIDGBC.anchor = GridBagConstraints.WEST;
		pieChartLabelIDGBC.gridy = usedRows - 1;
		pieChartLabelIDGBC.insets = new Insets(0, 0, 8, 0);
		pieChartLabelIDGBC.fill = GridBagConstraints.HORIZONTAL;
		pieChartLabelIDTextField = new JTextField();
		pieChartLabelIDTextField.setText(prefixIDText + ".label");
		panel.add(pieChartLabelCheckbox, pieChartLabelCheckboxGBC);
		panel.add(pieChartLabelIDTextField, pieChartLabelIDGBC);
			 
	  importCharacterCheckbox.addItemListener(new ItemListener() { 
					public void itemStateChanged(ItemEvent e) { 
						setContentsEnabled(importCharacterCheckbox.isSelected()); 
					}
				});
	    
	  pieChartLabelCheckbox.addItemListener(new ItemListener() { 
			  	public void itemStateChanged(ItemEvent e) {
			  		pieChartLabelIDTextField.setEnabled(pieChartLabelCheckbox.isSelected());
			  	} 
			  });
	  
	  return usedRows;
	}
	
	
	public void assignParameters(AncestralStateImportParameters parameters, int importAdapterIndex, int labelIDsIndex) {
		NodeBranchDataAdapter[] importAdapters = parameters.getImportAdapters();
		VoidNodeBranchDataAdapter voidAdapter = new VoidNodeBranchDataAdapter("");
		for (int i = 0; i < stateInputs.size(); i++) {
			importAdapters[importAdapterIndex + i] = importCharacterCheckbox.isSelected() ? stateInputs.get(i).getSelectedAdapter() : voidAdapter;
		}
		parameters.setImportAdapters(importAdapters);
		
		String[] labelIDs = parameters.getPieChartLabelIDs();
		labelIDs[labelIDsIndex] = pieChartLabelCheckbox.isSelected() ? pieChartLabelIDTextField.getText() : null;
		parameters.setPieChartLabelIDs(labelIDs);
	}
	
	
	public int getBottomY() {
		return bottomY;
	}
	

	private void setContentsEnabled(boolean enabled) {
		prefixLabel.setEnabled(enabled);
		prefixIDTextField.setEnabled(enabled);	
		typeHeadingLabel.setEnabled(enabled);
		idHeadingLabel.setEnabled(enabled);
		for (int i = 0; i < stateInputs.size(); i++) {
			stateInputs.get(i).setEnabled(enabled);
		}
		pieChartLabelCheckbox.setEnabled(enabled);
		pieChartLabelIDTextField.setEnabled(enabled);
	}
}

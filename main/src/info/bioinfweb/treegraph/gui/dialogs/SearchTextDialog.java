/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2015-2016  Ben Stöver, Sarah Wiechers, Kai Müller
 * <http://treegraph.bioinfweb.info/>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.treegraph.gui.dialogs;


import javax.swing.JPanel;
import java.awt.Frame;
import javax.swing.BoxLayout;
import java.awt.GridBagLayout;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import javax.swing.JCheckBox;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import java.awt.Font;
import java.awt.Color;



public class SearchTextDialog extends EditDialog {
	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JPanel searchPanel = null;
	private JTextField searchTextField = null;
	private JCheckBox caseSensitiveCheckBox = null;
	private JCheckBox includeHiddenCheckBox = null;
	private JCheckBox wordsOnlyCheckBox = null;


	/**
	 * @param owner
	 */
	public SearchTextDialog(Frame owner) {
		super(owner);
		setHelpCode(13);
		initialize();
		setLocationRelativeTo(owner);
	}


	@Override
	protected boolean onExecute() {
  	getSearchTextField().requestFocus();
		getSearchTextField().selectAll();
		return true;
	}


	@Override
	protected boolean apply() {
		return true;
	}
	
	
	public String getText() {
		return getSearchTextField().getText();
	}
	
	
	public boolean caseSensitive() {
		return getCaseSensitiveCheckBox().isSelected();
	}


	public boolean wordsOnly() {
		return getWordsOnlyCheckBox().isSelected();
	}


	public boolean includeInternalNodeNames() {
		return getIncludeHiddenCheckBox().isSelected();
	}


	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setTitle("Search text");
		this.setContentPane(getJContentPane());
		getApplyButton().setVisible(false);
		this.pack();
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
			jContentPane.add(getSearchPanel(), null);
			jContentPane.add(getButtonsPanel(), null);
		}
		return jContentPane;
	}


	/**
	 * This method initializes searchPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getSearchPanel() {
		if (searchPanel == null) {
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.anchor = GridBagConstraints.WEST;
			gridBagConstraints11.gridy = 2;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			gridBagConstraints2.gridy = 3;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.gridy = 1;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.weightx = 1.0;
			searchPanel = new JPanel();
			searchPanel.setLayout(new GridBagLayout());
			searchPanel.setBorder(BorderFactory.createTitledBorder(null, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			searchPanel.add(getSearchTextField(), gridBagConstraints);
			searchPanel.add(getCaseSensitiveCheckBox(), gridBagConstraints1);
			searchPanel.add(getIncludeHiddenCheckBox(), gridBagConstraints2);
			searchPanel.add(getWordsOnlyCheckBox(), gridBagConstraints11);
		}
		return searchPanel;
	}


	/**
	 * This method initializes searchTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getSearchTextField() {
		if (searchTextField == null) {
			searchTextField = new JTextField();
		}
		return searchTextField;
	}


	/**
	 * This method initializes caseSensitiveCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getCaseSensitiveCheckBox() {
		if (caseSensitiveCheckBox == null) {
			caseSensitiveCheckBox = new JCheckBox();
			caseSensitiveCheckBox.setText("Case sensitive");
		}
		return caseSensitiveCheckBox;
	}


	/**
	 * This method initializes includeHiddenCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getIncludeHiddenCheckBox() {
		if (includeHiddenCheckBox == null) {
			includeHiddenCheckBox = new JCheckBox();
			includeHiddenCheckBox.setText("Include internal node names");
		}
		return includeHiddenCheckBox;
	}


	/**
	 * This method initializes wordsOnlyCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getWordsOnlyCheckBox() {
		if (wordsOnlyCheckBox == null) {
			wordsOnlyCheckBox = new JCheckBox();
			wordsOnlyCheckBox.setText("Words only");
		}
		return wordsOnlyCheckBox;
	}

}
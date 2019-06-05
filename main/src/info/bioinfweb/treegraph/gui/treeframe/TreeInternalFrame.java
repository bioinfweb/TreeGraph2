/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2019  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.gui.treeframe;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.table.TableColumnModel;

import info.bioinfweb.commons.swing.TableColumnModelAdapter;
import info.bioinfweb.commons.swing.scrollpaneselector.ExtendedScrollPaneSelector;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.format.GlobalFormats;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.gui.CurrentDirectoryModel;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;



/**
 * The internal frame displaying a document.
 *  
 * @author Ben St&ouml;ver
 */
public class TreeInternalFrame extends JInternalFrame {
	public static final double TREE_WIDTH_RATIO = 0.5;
	public static final int DATA_COLUMN_WIDTH = 135;  //  @jve:decl-index=0:
	public static final int DECIMAL_COLUMN_WIDTH = 35;  //  @jve:decl-index=0:
	
	
	private Document document = null;
	private InternalFrameListener internalFrameListener = null;  //  @jve:decl-index=0:
	private JPanel jContentPane = null;
	private JSplitPane documentSplitPane = null;
	private TreeScrollPane treeScrollPane = null;
	private JScrollPane tableScrollPane = null;
	private JTable table = null;
	
	
	/**
	 * Creates a new instance of this class.
	 */
	public TreeInternalFrame(Document document) {
		super("", true, true, true);
		this.document = document;
		
		document.setFrame(this);
		addInternalFrameListener(getInternalFrameListener());
		initialize();
	}
	
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setSize(500, 400);
		setIconifiable(true);
		setContentPane(getJContentPane());
	}


	private InternalFrameListener getInternalFrameListener() {
		if (internalFrameListener == null) {
			internalFrameListener = new InternalFrameAdapter() {
					@Override
					public void internalFrameActivated(InternalFrameEvent e) {
						MainFrame.getInstance().updateMenues();
						getTreeViewPanel().requestFocus();
					}
					
				  @Override
					public void internalFrameClosing(InternalFrameEvent e) {
						if (document.askToSave()) {
							CurrentDirectoryModel.getInstance().removeFileChooser(getDocument().getFileChooser());
							ExtendedScrollPaneSelector.uninstallScrollPaneSelector(getTreeScrollPane());
							setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
						}
						else {
							setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
						}
					}
					
					@Override
					public void internalFrameClosed(InternalFrameEvent e) {
						MainFrame.getInstance().updateMenues();
					}
				};
		}
		return internalFrameListener;
	}


	public Document getDocument() {
		return document;
	}
	
	
	public Rectangle getDocumentRect() {
		return getTreeScrollPane().getViewportBorderBounds();
	}


	public void setScrollPaneBgColor(Color color) {
		getTreeScrollPane().getViewport().setBackground(color);
	}
	
	
	private void setColumnWidths() {
		TableColumnModel model = getTable().getColumnModel();
		for (int i = 0; i < getTable().getColumnCount(); i++) {
			if (i % 2 == 0) {
				model.getColumn(i).setPreferredWidth(DATA_COLUMN_WIDTH);
			}
			else {
				model.getColumn(i).setPreferredWidth(DECIMAL_COLUMN_WIDTH);
			}
		}
	}
	
	
	/**
	 * Returns the selected adapter in the node/branch data table.
	 * @return the adapter or <code>null</code> if no column is selected
	 */
	public NodeBranchDataAdapter getSelectedAdapter() {
		int pos = getTable().getSelectedColumn();
		if (pos != -1) {
			return getTableModel().getAdapter(pos);
		}
		else {
			return null;
		}
	}
	
	
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getDocumentSplitPane(), BorderLayout.CENTER);
		}
		return jContentPane;
	}


	/**
	 * This method initializes documentSplitPane	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
	private JSplitPane getDocumentSplitPane() {
		if (documentSplitPane == null) {
			documentSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
					getTreeScrollPane(), getTableScrollPane());
			documentSplitPane.setDividerSize(8);
			documentSplitPane.setOneTouchExpandable(true);
			documentSplitPane.setResizeWeight(TREE_WIDTH_RATIO);
			//documentSplitPane.setDividerLocation(TREE_WIDTH_RATIO);
			documentSplitPane.setDividerLocation(Integer.MAX_VALUE);
		}
		return documentSplitPane;
	}
	
	
	public TreeViewPanel getTreeViewPanel() {
		return getTreeScrollPane().getTreeViewPanel();
	}


	/**
	 * This method initializes scrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private TreeScrollPane getTreeScrollPane() {
		if (treeScrollPane == null) {
			treeScrollPane = new TreeScrollPane(getDocument());
			
			ExtendedScrollPaneSelector.installScrollPaneSelector(treeScrollPane);
			treeScrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, new HelpButton(44));
			treeScrollPane.getViewport().setBackground(GlobalFormats.DEFAULT_BACKGROUNG_COLOR);
		}
		return treeScrollPane;
	}


	/**
	 * This method initializes tableScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getTableScrollPane() {
		if (tableScrollPane == null) {
			tableScrollPane = new JScrollPane();
			tableScrollPane.setViewportView(getTable());
			ExtendedScrollPaneSelector.installScrollPaneSelector(tableScrollPane);
			tableScrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, new HelpButton(45));
    }
		return tableScrollPane;
	}


	private void decorateDefaultCellRenderer(Class<?> valueClass) {
		table.setDefaultRenderer(valueClass, new DocumentTableCellRenderer(table.getDefaultRenderer(valueClass), 
				(DocumentTableModel)table.getModel(), getTreeViewPanel().getSelection(), getTreeViewPanel().getHighlighting()));
	}
	
	
	/**
	 * This method initializes table	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getTable() {
		if (table == null) {
			table = new JTable(new DocumentTableModel(getDocument()));
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			table.setSurrendersFocusOnKeystroke(true);
			table.setColumnSelectionAllowed(true);
			table.setRowSelectionAllowed(true);
			table.getTableHeader().setReorderingAllowed(false);
			table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
			decorateDefaultCellRenderer(Object.class);
			decorateDefaultCellRenderer(Double.class);
			decorateDefaultCellRenderer(Boolean.class);  // One shared renderer class cannot be used since the boolean renderer differs from the others.
			// Add more default renderers here if more data types are added in the future.
			
			ListSelectionListener listener = new ListSelectionListener() {  // Sets the new table selection to the tree.
					  public void valueChanged(ListSelectionEvent e) {
				  		if (!e.getValueIsAdjusting() && 
				  				(table.getSelectedRowCount() > 0) && (table.getSelectedColumnCount() > 0)) {
				  			
					  		TreeSelection selection = getTreeViewPanel().getSelection();
					  		try {
					  			selection.setValueIsAdjusting(true);
					  			selection.clear();
					  			for (int selCol : table.getSelectedColumns()) {  // Usually only one column will be selected.
						  			for (int selRow : table.getSelectedRows()) {
								  		getTreeViewPanel().getSelection().add(getTableModel().getTreeElement(selRow, selCol));
										}
									}
					  		}
					  		finally {
					  			selection.setValueIsAdjusting(false);
					  		}
					  	}
					  }
				  };
			table.getSelectionModel().addListSelectionListener(listener);  // Registering here is necessary to be informed on selection changes when the previous and newly selected cell are in the same column.
			table.getColumnModel().getSelectionModel().addListSelectionListener(listener);  // Registering here is necessary to be informed on selection changes when the previous and newly selected cell are in the same row.
			
			table.getColumnModel().addColumnModelListener(new TableColumnModelAdapter() {
						@Override
						public void columnAdded(TableColumnModelEvent e) {
							setColumnWidths();
						}
					});
			
			getTreeViewPanel().addTreeViewPanelListener(new TreeViewPanelListener() {  // Sets the new tree selection to the table.
						public void selectionChanged(ChangeEvent e) {
							getTable().repaint();  // Necessary to update possible color highlighting.
						}
		
						@Override
						public void highlightingChanged(ChangeEvent e) {
							getTable().repaint();  // Necessary to update possible color highlighting.
						}

						@Override
						public void sizeChanged(ChangeEvent e) {}
		
						@Override
						public void zoomChanged(ChangeEvent e) {}
		  		});
			
			setColumnWidths();
		}
		return table;
	}
	
	
	private DocumentTableModel getTableModel() {
		return (DocumentTableModel)getTable().getModel();
	}
}
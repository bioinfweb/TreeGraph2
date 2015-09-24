/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2015  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.gui.mainframe;


import info.bioinfweb.treegraph.Main;
import info.bioinfweb.treegraph.document.*;
import info.bioinfweb.treegraph.document.io.DocumentReader;
import info.bioinfweb.treegraph.document.io.ReadWriteFactory;
import info.bioinfweb.treegraph.document.io.ReadWriteParameterMap;
import info.bioinfweb.treegraph.document.nodebranchdata.NewTextLabelAdapter;
import info.bioinfweb.treegraph.graphics.positionpaint.PositionPaintFactory;
import info.bioinfweb.treegraph.graphics.positionpaint.PositionPaintType;
import info.bioinfweb.treegraph.gui.CurrentDirectoryModel;
import info.bioinfweb.treegraph.gui.actions.ActionManagement;
import info.bioinfweb.treegraph.gui.actions.window.SelectFrameAction;
import info.bioinfweb.treegraph.gui.dialogs.io.loadlogger.LoadLoggerDialog;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.commons.swing.ExtendedDesktopPane;

import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPanel;
import javax.swing.JMenu;



/**
 * The main frame of TreeGraph 2.
 * 
 * @author Ben St&ouml;ver
 */
public class MainFrame extends JFrame implements Runnable {
	private static final long serialVersionUID = 1L;
	private static final int WINDOW_MENU_ITEMS_COUNT = 3;
	
	
	private static MainFrame firstInstance = null;
	
	private TreeSelectionSynchronizer treeSelectionSynchronizer = new TreeSelectionSynchronizer(new TreeViewPanelIterator());
	private ActionManagement actionManagement = new ActionManagement(this);
	private WindowListener windowListener = null;
	private JMenuBar mainMenu = null;
	private JMenu fileMenu = null;
	private JMenu editMenu = null;
	private JMenu viewMenu = null;
	private JMenu helpMenu = null;
	private JPanel jContentPane = null;
	private ExtendedDesktopPane desktopPane = null;
	private JMenu windowMenu = null;
	private JMenu formatMenu = null;
	private JMenu selectMenu = null;
	private JPanel toolBarPanel = null;
	private JMenu undoMenu = null;
	private JMenu redoMenu = null;
	private JMenu newMenu = null;
	private JMenu nodeBranchDataMenu = null;
	
	
	/**
	 * This is the default constructor
	 */
	private MainFrame() {
		super();
		initialize();
	}
	
	
	public static MainFrame getInstance() {
		if (firstInstance == null) {
			firstInstance = new MainFrame();
		}
		return firstInstance;
	}


	private void openInitialFile() {
		File file = Main.getInstance().getCmdProcessor().getInitialFile();
		if ((file != null)) {
			if (file.canRead()) {
				DocumentReader reader = ReadWriteFactory.getInstance().getReader(file);
				if (reader != null) {
					try {
						ReadWriteParameterMap parameterMap = new ReadWriteParameterMap();
						parameterMap.putApplicationLogger(LoadLoggerDialog.getInstance());
						parameterMap.put(ReadWriteParameterMap.KEY_INTERNAL_NODE_NAMES_ADAPTER, 
								new NewTextLabelAdapter("internalNodeNames", new DecimalFormat()));
						addInternalFrame(reader.read(file, parameterMap));
						CurrentDirectoryModel.getInstance().setCurrentDirectory(file.getParentFile());
						LoadLoggerDialog.getInstance().display();
					}
					catch (Exception e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(null, "The error \"" + e.getMessage() + 
								"\" occured when trying to open the file \"" + file.getAbsolutePath() + 
								"\"", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
				else {
					JOptionPane.showMessageDialog(this, "The file \"" + file.getAbsolutePath() + 
							"\" does not have a supported format.", "Format error", 
							JOptionPane.ERROR_MESSAGE);
				}
			}
			else {
				JOptionPane.showMessageDialog(this, "The file \"" + file.getAbsolutePath() + 
						"\" cannot be opened.", "File error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	
	public void run() {
		setVisible(true);
		openInitialFile();
	}
	
	
	/**
	 * Asks the user whether to save all opened documents and closes the application if the user did not
	 * cancel the process. 
	 */
	public void close() {
		processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}


	public void addInternalFrame(JInternalFrame iFrame) {
		getDesktopPane().add(iFrame);
		try {
			iFrame.setMaximum(true);  //TODO Why is this ignored although no exception is thrown?
			iFrame.setSelected(true);
		}
		catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		//updateMenues();
	}
	
	
  public TreeInternalFrame addInternalFrame(Document document) {
		TreeInternalFrame result = new TreeInternalFrame(document);
		result.setVisible(true);
		addInternalFrame(result);
		return result;
  }
  
  
  public void addChildWindowListener(ContainerListener listener) {
  	getDesktopPane().addContainerListener(listener);
  }
  
  
  public void removeChildWindowListener(ContainerListener listener) {
  	getDesktopPane().removeContainerListener(listener);
  }
    
  
	/**
	 * Returns the currently active Frame where a tree is displayed.
	 * 
	 * @return the tree or null of no frame is active or the active frame is 
	 *         not of type {@link TreeInternalFrame}
	 */
	public TreeInternalFrame getActiveTreeFrame() {
		if ((getDesktopPane().getSelectedFrame() != null) && (getDesktopPane().getSelectedFrame() instanceof TreeInternalFrame)) {
			return (TreeInternalFrame)getDesktopPane().getSelectedFrame();
		}
		else {
			return null;
		}
	}
	
	
	/**
	 * Returns an iterator over all currently available tree frames. (There is one tree frame for each currently opened 
	 * document.)
	 * 
	 * @return an iterator over all tree frames
	 * @since 2.2.0
	 */
	public Iterator<TreeInternalFrame> treeFrameIterator() {
		final JInternalFrame[] frames = getDesktopPane().getAllFrames();
		return new Iterator<TreeInternalFrame>() {
					private int pos = 0; 
					
					@Override
		      public boolean hasNext() {
			      return pos < frames.length;
		      }
		
					@Override
		      public TreeInternalFrame next() {
						pos++;
			      return (TreeInternalFrame)frames[pos - 1];
		      }
				};
	}
	
	
	public int getTreeFrameCount() {
		return getDesktopPane().getAllFrames().length;
	}

	
	public JInternalFrame selectFrame(boolean forward) {
		return getDesktopPane().selectFrame(forward);
	}
	
	
	public TreeInternalFrame getInternalFrameByFile(File file) {
		JInternalFrame[] frames = getDesktopPane().getAllFrames();
		for (int i = 0; i < frames.length; i++) {
			if (frames[i] instanceof TreeInternalFrame) {
				Document doc = ((TreeInternalFrame)frames[i]).getDocument();
				if (doc.hasFile() && (file.equals(doc.getFile()))) {
					return ((TreeInternalFrame)frames[i]);
				}
			}
		}
		return null;
	}
	
	
	public void tileInternalFramesVertical() {
		getDesktopPane().tileVertical();
	}
	
	
	public void tileInternalFramesHorizontal() {
		getDesktopPane().tileHorizontal();
	}
	
	
	public void cascadeInternalFrames() {
		getDesktopPane().cascade();
	}
	
	
	public TreeSelectionSynchronizer getTreeSelectionSynchronizer() {
		return treeSelectionSynchronizer;
	}


	public ActionManagement getActionManagement() {
		return actionManagement;
	}


	private void updateWindowMenu() {
		while (getWindowMenu().getItemCount() > WINDOW_MENU_ITEMS_COUNT) {
			getWindowMenu().remove(getWindowMenu().getItemCount() - 1);
		}
		JInternalFrame[] frames = getDesktopPane().getAllFrames();
		if (frames.length > 0) {
			getWindowMenu().addSeparator();
			for (int i = 0; i < frames.length; i++) {
				getWindowMenu().add(new SelectFrameAction(frames[i], i));
			}
		}
	}
	
	
	public void updateMenues() {
		updateWindowMenu();
		getActionManagement().refreshActionStatus();
	}
	
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setSize(800, 800);  // Tutorial
		//setSize(800, 600);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setContentPane(getJContentPane());
		setJMenuBar(getMainMenu());
		setTitle("TreeGraph 2");
		addWindowListener(getWindowListener());
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		loadIcons();
		updateMenues();
	}
	
	
	private void loadIcons() {
		List<BufferedImage> icons = new Vector<BufferedImage>(7, 1);
		try {
			icons.add(ImageIO.read(Object.class.getResource("/resources/symbols/TreeGraph16.png")));
			icons.add(ImageIO.read(Object.class.getResource("/resources/symbols/TreeGraph20.png")));
			icons.add(ImageIO.read(Object.class.getResource("/resources/symbols/TreeGraph22.png")));
			icons.add(ImageIO.read(Object.class.getResource("/resources/symbols/TreeGraph24.png")));
			icons.add(ImageIO.read(Object.class.getResource("/resources/symbols/TreeGraph32.png")));
			icons.add(ImageIO.read(Object.class.getResource("/resources/symbols/TreeGraph48.png")));
			icons.add(ImageIO.read(Object.class.getResource("/resources/symbols/TreeGraph64.png")));
			icons.add(ImageIO.read(Object.class.getResource("/resources/symbols/TreeGraph256.png")));
			setIconImages(icons);
		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(this, "The application symbols could not be loaded.", 
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	
	private WindowListener getWindowListener() {
		if (windowListener == null) {
			windowListener = new WindowAdapter() {
				  @Override
				  public void windowClosing(WindowEvent e) {
				  	JInternalFrame[] frames = getDesktopPane().getAllFrames();
				  	for (int i = 0; i < frames.length; i++) {
							frames[i].doDefaultCloseAction();
							if (!frames[i].isClosed()) {
								return;  //Programm nicht beenden.
							}
						}

				  	System.exit(0);
				  }
			};
		}
		return windowListener;
	}


	/**
	 * This method initializes mainMenu	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */
	private JMenuBar getMainMenu() {
		if (mainMenu == null) {
			mainMenu = new JMenuBar();
			mainMenu.add(getFileMenu());
			mainMenu.add(getSelectMenu());
			mainMenu.add(getEditMenu());
			mainMenu.add(getFormatMenu());
			mainMenu.add(getViewMenu());
			mainMenu.add(getWindowMenu());
			mainMenu.add(getHelpMenu());
		}
		return mainMenu;
	}


	/**
	 * This method initializes fileMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setText("File");
			fileMenu.setMnemonic(KeyEvent.VK_F);
			fileMenu.add(getActionManagement().get("file.newDocument"));
			fileMenu.add(getActionManagement().get("file.open"));
			fileMenu.add(getActionManagement().get("file.save"));
			fileMenu.add(getActionManagement().get("file.saveAs"));
			fileMenu.add(getActionManagement().get("file.close"));
			fileMenu.addSeparator();
			fileMenu.add(getActionManagement().get("file.addSupportValues"));
			fileMenu.add(getActionManagement().get("file.importTable"));
			fileMenu.add(getActionManagement().get("file.importBayesTraitsData"));
			fileMenu.addSeparator();
			fileMenu.add(getActionManagement().get("file.exportGraphic"));
			fileMenu.add(getActionManagement().get("file.exportNewickNexus"));
			fileMenu.add(getActionManagement().get("file.exportTable"));
			fileMenu.addSeparator();
			//fileMenu.add(getActionManagement().get("file.generateBayesTraitsCommandFile"));
			//fileMenu.addSeparator();
			fileMenu.add(getActionManagement().get("file.exit"));
		}
		return fileMenu;
	}


	/**
	 * This method initializes editMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getEditMenu() {
		if (editMenu == null) {
			editMenu = new JMenu("Edit");
			editMenu.setMnemonic(KeyEvent.VK_E);
			editMenu.add(getUndoMenu());
			editMenu.add(getRedoMenu());
			editMenu.addSeparator();
			editMenu.add(getNewMenu());
			editMenu.add(getActionManagement().get("edit.copyElement"));
			editMenu.add(getActionManagement().get("edit.copyAllLabels"));
			editMenu.add(getActionManagement().get("edit.cut"));
			editMenu.add(getActionManagement().get("edit.paste"));
			editMenu.add(getActionManagement().get("edit.delete"));
			editMenu.addSeparator();
			editMenu.add(getNodeBranchDataMenu());
			editMenu.add(getActionManagement().get("edit.editText"));
			editMenu.add(getActionManagement().get("edit.branchLength"));
			editMenu.add(getActionManagement().get("edit.changeLabelID"));
			editMenu.add(getActionManagement().get("edit.pieChartIDs"));
			editMenu.addSeparator();
			editMenu.add(getActionManagement().get("edit.search"));
			editMenu.add(getActionManagement().get("edit.replaceInNodeData"));
			editMenu.addSeparator();
			editMenu.add(getActionManagement().get("edit.moveSubtreeUp"));
			editMenu.add(getActionManagement().get("edit.moveSubtreeDown"));
			editMenu.add(getActionManagement().get("edit.ladderizeUp"));
			editMenu.add(getActionManagement().get("edit.ladderizeDown"));
			editMenu.add(getActionManagement().get("edit.sortLeafs"));
			editMenu.add(getActionManagement().get("edit.reroot"));
			editMenu.add(getActionManagement().get("edit.rerootByLeafSet"));
			editMenu.addSeparator();
			editMenu.add(getActionManagement().get("edit.deleteSubelements"));
			editMenu.add(getActionManagement().get("edit.collapseNode"));
			editMenu.add(getActionManagement().get("edit.collapseNodesBySupport"));
			editMenu.add(getActionManagement().get("edit.separateBranch"));
		}
		return editMenu;
	}


	/**
	 * This method initializes viewMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getViewMenu() {
		if (viewMenu == null) {
			viewMenu = new JMenu();
			viewMenu.setText("View");
			viewMenu.setMnemonic(KeyEvent.VK_V);
			viewMenu.add(getActionManagement().get("view.setZoomToOriginal"));
			viewMenu.add(getActionManagement().get("view.fitZoomToWidthHeight"));
			viewMenu.add(getActionManagement().get("view.fitZoomToWidth"));
			viewMenu.add(getActionManagement().get("view.fitZoomToHeight"));
			viewMenu.add(getActionManagement().get("view.setUserZoom"));
			viewMenu.addSeparator();
			for (PositionPaintType type: PositionPaintType.values()) {
				viewMenu.add(getActionManagement().get("view.setPainterID_" + PositionPaintFactory.getInstance().getName(type)));
			}
		}
		return viewMenu;
	}


	/**
	 * This method initializes helpMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getHelpMenu() {
		if (helpMenu == null) {
			helpMenu = new JMenu();
			helpMenu.setText("Help");
			helpMenu.setMnemonic(KeyEvent.VK_H);
			helpMenu.add(getActionManagement().get("help.contents"));
			helpMenu.add(getActionManagement().get("help.index"));
			helpMenu.add(getActionManagement().get("help.aboutMenu"));
			helpMenu.addSeparator();
			helpMenu.add(getActionManagement().get("help.homepage"));
			helpMenu.add(getActionManagement().get("help.about"));
		}
		return helpMenu;
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
			jContentPane.add(getToolBarPanel(), BorderLayout.PAGE_START);
			jContentPane.add(getDesktopPane(), BorderLayout.CENTER);
		}
		return jContentPane;
	}


	/**
	 * This method initializes desktopPane	
	 * 	
	 * @return javax.swing.JDesktopPane	
	 */
	private ExtendedDesktopPane getDesktopPane() {
		if (desktopPane == null) {
			desktopPane = new ExtendedDesktopPane();
		}
		return desktopPane;
	}


	/**
	 * This method initializes windowMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getWindowMenu() {
		if (windowMenu == null) {
			windowMenu = new JMenu();
			windowMenu.setText("Window");
			windowMenu.setMnemonic(KeyEvent.VK_W);
			windowMenu.add(getActionManagement().get("window.tileVertical"));
			windowMenu.add(getActionManagement().get("window.tileHorizontal"));
			windowMenu.add(getActionManagement().get("window.cascade"));
		}
		return windowMenu;
	}


	/**
	 * This method initializes formatMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getFormatMenu() {
		if (formatMenu == null) {
			formatMenu = new JMenu();
			formatMenu.setText("Format");
			formatMenu.setMnemonic(KeyEvent.VK_O);
			formatMenu.add(getActionManagement().get("format.editDocumentFormats"));
			formatMenu.add(getActionManagement().get("format.editElementFormats"));
			formatMenu.add(getActionManagement().get("format.scaleBranchLengths"));
			formatMenu.add(getActionManagement().get("format.reanchorLegend"));
			formatMenu.add(getActionManagement().get("format.autoPositionLabels"));
			formatMenu.addSeparator();
			formatMenu.add(getActionManagement().get("format.colorsNodeData"));
			formatMenu.add(getActionManagement().get("format.branchWidthsNodeData"));
			formatMenu.add(getActionManagement().get("format.scaleDistanceValues"));
		}
		return formatMenu;
	}


	/**
	 * This method initializes selectMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getSelectMenu() {
		if (selectMenu == null) {
			selectMenu = new JMenu();
			selectMenu.setText("Select");
			selectMenu.setMnemonic(KeyEvent.VK_S);
			selectMenu.add(getActionManagement().get("select.all"));
			selectMenu.add(getActionManagement().get("select.invert"));
			selectMenu.add(getActionManagement().get("select.wholeSubtree"));
			selectMenu.add(getActionManagement().get("edit.search"));
			selectMenu.addSeparator();
			selectMenu.add(getActionManagement().get("select.typeInDocument"));
			selectMenu.add(getActionManagement().get("select.typeInSubtree"));
			selectMenu.addSeparator();
			selectMenu.add(getActionManagement().get("select.leafsInDocument"));
			selectMenu.add(getActionManagement().get("select.leafsInSubtree"));
			selectMenu.addSeparator();
			selectMenu.add(getActionManagement().get("select.labelsWithID"));
			selectMenu.add(getActionManagement().get("select.labelsInSubtree"));
			selectMenu.addSeparator();
			selectMenu.add(getActionManagement().get("select.allLegends"));
			selectMenu.add(getActionManagement().get("select.legendsWithPosIndex"));
			selectMenu.add(getActionManagement().get("select.legendsInSubtree"));
			selectMenu.addSeparator();
			selectMenu.add(new JCheckBoxMenuItem(getActionManagement().get("select.synchronizeTreeSelection")));
			selectMenu.add(getActionManagement().get("select.synchronizeTreeSelectionOptions"));
		}
		return selectMenu;
	}


	/**
	 * This method initializes toolBarPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getToolBarPanel() {
		if (toolBarPanel == null) {
			toolBarPanel = new ToolBarPanel(this);
		}
		return toolBarPanel;
	}


	/**
	 * This method initializes undoMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	public JMenu getUndoMenu() {
		if (undoMenu == null) {
			undoMenu = new JMenu();
			undoMenu.setText("Undo");
			undoMenu.setMnemonic(KeyEvent.VK_U);
			undoMenu.setIcon(new ImageIcon(Object.class.getResource("/resources/symbols/Undo16.png")));
		}
		return undoMenu;
	}


	/**
	 * This method initializes redoMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	public JMenu getRedoMenu() {
		if (redoMenu == null) {
			redoMenu = new JMenu();
			redoMenu.setText("Redo");
			redoMenu.setMnemonic(KeyEvent.VK_R);
			redoMenu.setIcon(new ImageIcon(Object.class.getResource("/resources/symbols/Redo16.png")));
		}
		return redoMenu;
	}


	/**
	 * This method initializes newMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getNewMenu() {
		if (newMenu == null) {
			newMenu = new JMenu();
			newMenu.setText("New");
			newMenu.setMnemonic(KeyEvent.VK_N);
			newMenu.add(getActionManagement().get("edit.newNode"));
			newMenu.add(getActionManagement().get("edit.newTextLabel"));
			newMenu.add(getActionManagement().get("edit.newIconLabels"));
			newMenu.add(getActionManagement().get("edit.newPieChartLabels"));
			newMenu.add(getActionManagement().get("edit.newLegend"));
		}
		return newMenu;
	}


	/**
	 * This method initializes nodeBranchDataMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getNodeBranchDataMenu() {
		if (nodeBranchDataMenu == null) {
			nodeBranchDataMenu = new JMenu();
			nodeBranchDataMenu.setText("Node/branch data");
			nodeBranchDataMenu.setMnemonic(KeyEvent.VK_D);
			nodeBranchDataMenu.add(getActionManagement().get("edit.columnToDecimalType"));
			nodeBranchDataMenu.add(getActionManagement().get("edit.columnToTextType"));
			nodeBranchDataMenu.add(getActionManagement().get("edit.renameID"));
			nodeBranchDataMenu.addSeparator();
			nodeBranchDataMenu.add(getActionManagement().get("edit.copyColumn"));
			nodeBranchDataMenu.add(getActionManagement().get("edit.deleteColumn"));
			nodeBranchDataMenu.add(getActionManagement().get("edit.deleteOutsideInterval"));
			nodeBranchDataMenu.add(getActionManagement().get("edit.calculate"));
			nodeBranchDataMenu.addSeparator();
			nodeBranchDataMenu.add(getActionManagement().get("edit.defaultDocumentAdapters"));
		}
		return nodeBranchDataMenu;
	}
}
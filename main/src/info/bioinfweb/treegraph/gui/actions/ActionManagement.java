/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2017  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.gui.actions;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.undo.UndoableEdit;

import info.bioinfweb.treegraph.document.*;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.graphics.positionpaint.PositionPaintFactory;
import info.bioinfweb.treegraph.graphics.positionpaint.PositionPaintType;
import info.bioinfweb.treegraph.gui.actions.edit.*;
import info.bioinfweb.treegraph.gui.actions.file.*;
import info.bioinfweb.treegraph.gui.actions.format.*;
import info.bioinfweb.treegraph.gui.actions.help.*;
import info.bioinfweb.treegraph.gui.actions.select.*;
import info.bioinfweb.treegraph.gui.actions.view.*;
import info.bioinfweb.treegraph.gui.actions.window.*;
import info.bioinfweb.treegraph.gui.mainframe.MainFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.bioinfweb.treegraph.gui.treeframe.TreeSelection;
import info.bioinfweb.commons.swing.AbstractUndoActionManagement;
import info.bioinfweb.commons.swing.AccessibleUndoManager;
import info.bioinfweb.commons.swing.actions.BioinfwebMainPageAction;
import info.bioinfweb.commons.swing.actions.TwitterAction;



/**
 * This class organizes the action objects of TreeGraph.
 * @author Ben St&ouml;ver
 */
public class ActionManagement extends AbstractUndoActionManagement {
	private MainFrame mainFrame = null;
  private Vector<Action> popupActions = new Vector<Action>();
  private JPopupMenu popupMenu = new JPopupMenu();
	
	
	public ActionManagement(MainFrame mainFrame) {
		super();
		this.mainFrame = mainFrame;
		fillMap();
		fillPopupActions();
	}
	
	
	/**
	 * All {@link Action} objects used in TreeGraph 2 are added to the {@link HashMap}
	 * in this method. New actions should be added here as well.
	 */
	protected void fillMap() {
		put("file.newDocument", new NewDocumentAction(mainFrame));
		put("file.open", new OpenAction(mainFrame));
		put("file.save", new SaveAction(mainFrame));
		put("file.saveAs", new SaveAsAction(mainFrame));
		put("file.close", new CloseAction(mainFrame));
		put("file.exportGraphic", new ExportToGraphicAction(mainFrame));
		put("file.exportPDF", new ExportToPDFAction(mainFrame));
		put("file.exportTreeFormat", new ExportToTreeFormatAction(mainFrame));
		put("file.addSupportValues", new AddSupportValuesAction(mainFrame));
		put("file.nodeFrequency", new NodeSupportAction(mainFrame));
		put("file.importTable", new ImportTableAction(mainFrame));
		put("file.exportTable", new ExportTableAction(mainFrame));
		put("file.generateBayesTraitsCommands", new GenerateBayesTraitsInputAction(mainFrame));
		put("file.importBayesTraitsData", new ImportBayesTraitsDataAction(mainFrame));
		put("file.createBranchLabelCaptionDocument", new CreateBranchLabelCaptionDocumentAction(mainFrame));
		put("file.exportPieChartLabelColors", new ExportPieChartLabelColorsAction(mainFrame));
		put("file.exit", new ExitAction(mainFrame));
		
		put("select.all", new SelectAllAction(mainFrame));
		put("select.invert", new InvertSelectionAction(mainFrame));
		put("select.wholeSubtree", new SelectSubtreeAction(mainFrame));
		put("select.typeInDocument", new SelectTypeInDocumentAction(mainFrame));
		put("select.typeInSubtree", new SelectTypeInSubtreeAction(mainFrame));
		put("select.leavesInDocument", new SelectLeavesInDocumentAction(mainFrame));
		put("select.leavesInSubtree", new SelectLeavesInSubtreeAction(mainFrame));
		put("select.labelsWithID", new SelectLabelsWithIDAction(mainFrame));
		put("select.labelsInSubtree", new SelectLabelsInSubtreeAction(mainFrame));
		put("select.allLegends", new SelectLegendsAction(mainFrame));
		put("select.legendsWithPosIndex", new SelectLegendsWithPosIndexAction(mainFrame));
		put("select.legendsInSubtree", new SelectLegendsInSubtreeAction(mainFrame));
		put("select.synchronizeTreeSelection", new TreeSelectionSynchronizeToggleAction(mainFrame));
		put("select.synchronizeTreeSelectionOptions", new TreeSelectionCompareParametersAction(mainFrame));
		
		put("edit.undo", new UndoAction(mainFrame));
		put("edit.redo", new RedoAction(mainFrame));
		put("edit.copyElement", new CopyElementAction(mainFrame));
		put("edit.copyAllLabels", new CopyAllLabelsAction(mainFrame));
		put("edit.cut", new CutAction(mainFrame));
		put("edit.paste", new PasteAction(mainFrame));
		put("edit.delete", new DeleteElementAction(mainFrame));
		put("edit.columnToDecimalType", new SetColumnToDecimalAction(mainFrame));
		put("edit.columnToTextType", new SetColumnToTextAction(mainFrame));
		put("edit.renameID", new RenameDataIDAction(mainFrame));
		put("edit.copyColumn", new CopyColumnAction(mainFrame));
		put("edit.deleteColumn", new DeleteColumnAction(mainFrame));
		put("edit.deleteOutsideInterval", new DeleteOutsideIntervalAction(mainFrame));
		put("edit.newNode", new NewNodeAction(mainFrame));
		put("edit.newTextLabel", new NewTextLabelAction(mainFrame));
		put("edit.newIconLabels", new NewIconLabelsAction(mainFrame));
		put("edit.newPieChartLabels", new NewPieChartLabelsAction(mainFrame));
		put("edit.newLegend", new NewLegendAction(mainFrame));
		put("edit.moveSubtreeUp", new MoveSubtreeUpAction(mainFrame));
		put("edit.moveSubtreeDown", new MoveSubtreeDownAction(mainFrame));
		put("edit.deleteSubelements", new DeleteSubelementsAction(mainFrame));
		put("edit.collapseNode", new CollapseNodeAction(mainFrame));
		put("edit.collapseNodesBySupport", new CollapseNodesBySupportAction(mainFrame));
		put("edit.separateBranch", new SeparateBranchAction(mainFrame));
		put("edit.ladderizeUp", new LadderizeUpAction(mainFrame));
		put("edit.ladderizeDown", new LadderizeDownAction(mainFrame));
		put("edit.sortLeaves", new SortLeavesAction(mainFrame));
		put("edit.reroot", new RerootAction(mainFrame));
		put("edit.rerootByLeafSet", new RerootByLeafSetAction(mainFrame));
		put("edit.editText", new EditTextElementAction(mainFrame));
		put("edit.branchLength", new EditBranchLengthAction(mainFrame));
		put("edit.changeLabelID", new ChangeLabelIDAction(mainFrame));
		put("edit.pieChartIDs", new EditPieChartLabelIDsAction(mainFrame));
		put("edit.search", new SearchTextAction(mainFrame));
		put("edit.replaceInNodeData", new ReplaceInNodeDataAction(mainFrame));
		put("edit.calculate", new CalculateColumnAction(mainFrame));
		put("edit.defaultDocumentAdapters", new DefaultDocumentAdapterAction(mainFrame));
		
		put("format.editDocumentFormats", new EditGlobalFormatsAction(mainFrame));
		put("format.showRooted", new ShowRootedAction(mainFrame));
		put("format.showScaleBar", new ShowHideScaleBarAction(mainFrame));
		put("format.editElementFormats", new EditElementFormatsAction(mainFrame));
		put("format.reanchorLegend", new ReanchorLegendAction(mainFrame));
		put("format.autoPositionLabels", new AutoPositionLabelsAction(mainFrame));
		put("format.branchWidthsNodeData", new DistanceValuesByNodeBranchDataAction(mainFrame));
		put("format.colorsNodeData", new ColorsByNodeBranchDataAction(mainFrame));
		put("format.scaleDistanceValues", new ScaleDistanceValuesAction(mainFrame));
		put("format.scaleBranchLengths", new ScaleBranchLengthsAction(mainFrame));
		
		put("view.fitZoomToWidthHeight", new FitZoomToWidthHeightAction(mainFrame));
		put("view.fitZoomToWidth", new FitZoomToWidthAction(mainFrame));
		put("view.fitZoomToHeight", new FitZoomToHeightAction(mainFrame));
		put("view.setZoomToOriginal", new SetZoomToOriginalAction(mainFrame));
		put("view.setUserZoom", new SetUserZoomAction(mainFrame));
		for (PositionPaintType type: PositionPaintType.values()) {
			put("view.setPainterID_" + PositionPaintFactory.getInstance().getName(type), new ChangePainterIDAction(mainFrame, type));
		}
		
		put("window.nextDocument", new NextDocumentAction(mainFrame));
		put("window.previousDocument", new PreviousDocumentAction(mainFrame));
		put("window.tileVertical", new TileVerticalAction());
		put("window.tileHorizontal", new TileHorizontalAction());
		put("window.cascade", new CascadeAction());
		put("window.preferences", new PreferencesAction());

		put("help.contents", new HelpContentsAction());
		put("help.index", new HelpMainPageAction());
		put("help.aboutMenu", new HelpAboutMenuAction());
		put("help.about", new AboutAction());
		put("help.privacyPolicy", new PrivacyPolicyAction());
		put("help.homepage", new TGMainPageAction());
		put("help.issues", new BugMainPageAction());
		put("help.bioinfweb", new BioinfwebMainPageAction());
		put("help.researchGate", new ResearchGateAction());
		put("help.twitter", new TwitterAction());
	}
	
	
	private void fillPopupActions() {
		popupActions.add(get("file.createBranchLabelCaptionDocument"));
		popupActions.add(get("file.exportPieChartLabelColors"));
		popupActions.add(null);  // becomes a separator
		popupActions.add(get("select.invert"));
		popupActions.add(get("select.wholeSubtree"));
		popupActions.add(get("select.typeInDocument"));
		popupActions.add(get("select.typeInSubtree"));
		popupActions.add(get("select.leavesInSubtree"));
		popupActions.add(get("select.labelsWithID"));
		popupActions.add(get("select.labelsInSubtree"));
		popupActions.add(get("select.legendsWithPosIndex"));
		popupActions.add(get("select.legendsInSubtree"));
		popupActions.add(null);
		popupActions.add(get("edit.copyElement"));
		popupActions.add(get("edit.copyAllLabels"));
		popupActions.add(get("edit.cut"));
		popupActions.add(get("edit.paste"));
		popupActions.add(get("edit.delete"));
		popupActions.add(null);  
		popupActions.add(get("edit.newNode"));
		popupActions.add(get("edit.newTextLabel"));
		popupActions.add(get("edit.newIconLabels"));
		popupActions.add(get("edit.newPieChartLabels"));
		popupActions.add(get("edit.newLegend"));
		popupActions.add(null);
		popupActions.add(get("edit.moveSubtreeUp"));
		popupActions.add(get("edit.moveSubtreeDown"));
		popupActions.add(get("edit.ladderizeUp"));
		popupActions.add(get("edit.ladderizeDown"));
		popupActions.add(get("edit.sortLeaves"));
		popupActions.add(get("edit.reroot"));
		popupActions.add(get("edit.rerootByLeafSet"));
		popupActions.add(null);
		popupActions.add(get("edit.deleteSubelements"));
		popupActions.add(get("edit.collapseNode"));
		popupActions.add(get("edit.collapseNodesBySupport"));
		popupActions.add(get("edit.separateBranch"));
		popupActions.add(null);
		popupActions.add(get("edit.editText"));
		popupActions.add(get("edit.branchLength"));
		popupActions.add(get("edit.changeLabelID"));
		popupActions.add(get("edit.pieChartIDs"));
		popupActions.add(null);
		popupActions.add(get("format.editElementFormats"));
		popupActions.add(get("format.reanchorLegend"));
		popupActions.add(get("format.autoPositionLabels"));
		popupActions.add(get("format.scaleDistanceValues"));	
	}
	

	private void setActionStatusBySelection(Document document, TreeSelection selection,
			NodeBranchDataAdapter tableAdapter) {
		
		Iterator<Action> iterator = getMap().values().iterator();
		while (iterator.hasNext()) {
			Action action = iterator.next();
			if (action instanceof DocumentAction) {
				((DocumentAction)action).setEnabled(document, selection, tableAdapter);
			}
		}
	}
	
	
	/**
	 * Enables or disables all action objects by calling their 
	 * <code>setEnabled(Document, TreeSelection)</code>-methods. Additionally the
	 * undo and redo actions are enabled depending on the contents of the undo manager
	 * and the action that would switch to the current view mode is disabled (which
	 * illustrated the current view mode to the user).
	 */
	public void refreshActionStatus() {
  	TreeInternalFrame frame = mainFrame.getActiveTreeFrame();
  	Document document = null;
		TreeSelection selection = null;
		NodeBranchDataAdapter tableAdapter = null;
		if (frame != null) {
			document = frame.getTreeViewPanel().getDocument();
			selection = frame.getTreeViewPanel().getSelection();
			tableAdapter = frame.getSelectedAdapter();
		}
		setActionStatusBySelection(document, selection, tableAdapter);
		
		if (frame != null) {
			editUndoRedoMenus();
			get("edit.undo").setEnabled(frame.getDocument().getUndoManager().canUndo());
			get("edit.redo").setEnabled(frame.getDocument().getUndoManager().canRedo());
	
			for (PositionPaintType type: PositionPaintType.values()) {
				Action action = get("view.setPainterID_" + 
						PositionPaintFactory.getInstance().getName(type));
				action.setEnabled(action.isEnabled() && 
						!type.equals(frame.getTreeViewPanel().getPainterType()));
			}
		}
		else {
			get("edit.undo").setEnabled(false);
			get("edit.redo").setEnabled(false);
	  	mainFrame.getUndoMenu().setEnabled(false);
  		mainFrame.getRedoMenu().setEnabled(false);
		}
	}
	

	@Override
	protected AccessibleUndoManager getUndoManager() {
		return mainFrame.getActiveTreeFrame().getDocument().getUndoManager();
	}


	@Override
	protected JMenu getUndoMenu() {
		return mainFrame.getUndoMenu();
	}


	@Override
	protected JMenu getRedoMenu() {
		return mainFrame.getRedoMenu();
	}


	@Override
	protected Action createUndoAction(UndoableEdit edit) {
		return new UndoToAction(mainFrame, edit);
	}


	@Override
	protected Action createRedoAction(UndoableEdit edit) {
		return new RedoToAction(mainFrame, edit);
	}


	public JPopupMenu getPopupMenu(PaintableElement selected) {
  	if (selected != null) {
  		popupMenu.removeAll();
			boolean separatorWasLast = true;  // Avoid multiple subsequent separators.
  		for (int i = 0; i < popupActions.size(); i++) {
  			Action action = popupActions.get(i);
  			if ((action == null) && !separatorWasLast) {
  				popupMenu.addSeparator();
  				separatorWasLast = true;
  			}
  			else if ((action != null) && action.isEnabled()) {
					popupMenu.add(action);
					separatorWasLast = false;
				}
			}
  		popupMenu.pack();
  		return popupMenu;
  	}
  	else {
  		return null;
  	}
  }
}
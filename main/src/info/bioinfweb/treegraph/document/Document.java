/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011  Ben Stöver, Kai Müller
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
package info.bioinfweb.treegraph.document;


import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;

import info.bioinfweb.treegraph.Main;
import info.bioinfweb.treegraph.document.io.ReadWriteFactory;
import info.bioinfweb.treegraph.document.io.ReadWriteFormat;
import info.bioinfweb.treegraph.document.io.xtg.XTGFilter;
import info.bioinfweb.treegraph.document.undo.*;
import info.bioinfweb.treegraph.graphics.positionpaint.*;
import info.bioinfweb.treegraph.gui.*;
import info.bioinfweb.treegraph.gui.treeframe.TreeInternalFrame;
import info.webinsel.util.*;
import info.webinsel.util.io.*;
import info.webinsel.util.swing.AccessibleUndoManager;
import info.webinsel.util.swing.SwingSavable;
import info.webinsel.util.swing.SwingSaver;



/**
 * Instances of this class contain the data for a tree and its formats as well as
 * position-information for all <code>TreePositioner</code>s used with this 
 * <code>Document</code>.<br>
 * Furthermore instances of this class contain lists of <code>TreePositioner</code>s and
 * <code>TreeView</code>s which want to be alerted if the content of this 
 * <code>Document</code> is modified. <code>Document</code> acts as the model in MVC-
 * paradigm used in the user-interface of TreeGraph 2.
 * 
 * @author Ben St&ouml;ver
 */
public class Document extends SwingSaver 
    implements ChangeMonitorable, Savable, SwingSavable {
	
	private static final int VIEWS_CAPACITY = 4;
	private static final int VIEWS_CAPACITY_INCREMENT = 4;
	
	
  private FormatVersion version = null;
  private AccessibleUndoManager undoManager = new AccessibleUndoManager();
  private TreeInternalFrame frame = null;
  private Vector<DocumentListener> views = 
  	  new Vector<DocumentListener>(VIEWS_CAPACITY, VIEWS_CAPACITY_INCREMENT);
  private Tree tree = new Tree();
  private EnumMap<PositionPaintType, Boolean> positioners = 
  	  new EnumMap<PositionPaintType, Boolean>(PositionPaintType.class);
  private EnumMap<PositionPaintType, Boolean> painters = 
  	  new EnumMap<PositionPaintType, Boolean>(PositionPaintType.class);
  
    
  /**
   * Creates a new Instance of <code>Document</code> with a default name. 
   */
  public Document() {
  	super();
  	init(true);
  }
  
  
  /**
   * Creates a new Instance of <code>Document</code>. 
   * @param name - the name prefix that should be used for the default name
   */
  public Document(String name) {
  	super();
  	DefaultNameManager nameManager = Main.getInstance().getNameManager();
  	setDefaultName(getDefaultName().replace(nameManager.getPrefix(), name));
  	init(true);
  }
  
  
  public Document(boolean registerFileChooser) {
  	super();
  	init(registerFileChooser);
  }
  
  
  private void init(boolean registerFileChooser) {
  	initSets();
  	getFileChooser().addChoosableFileFilter(new XTGFilter());
  	if (registerFileChooser) {
  		CurrentDirectoryModel.getInstance().addFileChooser(getFileChooser());  // Wird bei TreeInternalFrame.doDefaultCloseAction() wieder abgemeldet.
  	}
  	setDefaultExtension(XTGFilter.EXTENSION);
  	addFileExtension(XTGFilter.XML_EXTENSION);
  	setDefaultName(Main.getInstance().getNameManager().newDefaultName());
  }
  
  
  private void initSets() {
  	for (PositionPaintType type: PositionPaintType.values()) {
			positioners.put(type, false);;
		}
  	for (PositionPaintType type: PositionPaintType.values()) {
			painters.put(type, false);;
		}
  }
  
  
  /**
   * Returns the version this document has been loaded from.
   * @return the version or <code>null</code> if the document was not loaded from an XTG file or the version of the
   *         XTG file was unknown
   */
  public FormatVersion getVersion() {
	  return version;
  }


  public void setVersion(FormatVersion version) {
	  this.version = version;
  }


  public AccessibleUndoManager getUndoManager() {
		return undoManager;
	}


  public TreeInternalFrame getFrame() {
		return frame;
	}


	public void setFrame(TreeInternalFrame frame) {
		this.frame = frame;
  	updateFrame();
	}
	
	
  public boolean addView(DocumentListener view) {
		if (!views.contains(view)) {
			return views.add(view);
		}
		else {
			return false;
		}
  }
  
  
  public boolean removeView(DocumentListener view) {
  	return views.remove(view);
  }
  
  
  public void registerPositioner(PositionPaintType type) {
  	positioners.put(type, true);
  }
  
  
  public void unregisterPositioner(PositionPaintType type) {
  	painters.put(type, true);
  }
  
  
  public Tree getTree() {
	  return tree;
  }
  
  
  public void setTree(Tree tree) {
	  this.tree = tree;
  	registerChange();
  }
  
  
	private void updateFrame() {
		TreeInternalFrame frame = getFrame();
		if (frame != null) {
			String title = getDefaultNameOrPath();
			if (hasChanged()) {
				title = "*" + title;
			}
			frame.setTitle(title);
			
			frame.setScrollPaneBgColor(getTree().getFormats().getBackgroundColor());
		}
	}
  
  
	@Override
	public void setDefaultName(String name) {
		super.setDefaultName(name);
  	updateFrame();
	}


	@Override
	public void setFile(File file) {
		super.setFile(file);
  	updateFrame();
	}


	public void executeEdit(DocumentEdit edit) {
		if (!getUndoManager().addEdit(edit)) {  // Muss vor Ausführung erfolgen da sonst Undo-Schalter ggf. nicht aktiviert werden.
			throw new RuntimeException("The edit could not be executed.");
		}
		edit.redo();  // Tatsächlich ausführen.
	}
	
	
  /**
   * Alerts all registered positioners to reposition the tree elements becuase of made 
   * changes.
   */
  private void alertPositioners() {
  	for (PositionPaintType type: PositionPaintType.values()) {
  		if(positioners.get(type)) {
  			PositionPaintFactory.getInstance().getPositioner(type).positionAll(this, 1f);
  		}
  	}
  }


  /** Alerts all registered views to display made changes. */
  private void fireChangeHappened() {
  	for (int i = 0; i < views.size(); i++) {
  		views.get(i).changeHappened(new DocumentChangeEvent(this));
  	}
  }


	@Override
	public void registerChange() {
		super.registerChange();
		getTree().updateElementSet();
		alertPositioners();  // Positioners must be alerted first
		fireChangeHappened();
		updateFrame();
	}


  @Override
	public void reset() {
		super.reset();
		updateFrame();
	}


	@Override
	protected void saveDataToFile(File file) {
		try {
			ReadWriteFactory.getInstance().getWriter(ReadWriteFormat.XTG).write(this, file);
		}
		catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "The error \"" + e.getMessage() + "\" occured when writing to the file \"" + file.getAbsolutePath() + "\"", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
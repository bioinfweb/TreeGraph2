/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2014  Ben Stöver, Kai Müller
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
package info.bioinfweb.treegraph.document.undo.edit;


import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.undo.CannotRedoException;

import org.nfunk.jep.JEP;
import org.nfunk.jep.ParseException;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.IDManager;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextLabel;
import info.bioinfweb.treegraph.document.format.TextFormats;
import info.bioinfweb.treegraph.document.nodebranchdata.*;
import info.bioinfweb.treegraph.document.undo.NodeBranchDataBackup;
import info.bioinfweb.treegraph.document.undo.NodeBranchDataEdit;
import info.bioinfweb.treegraph.document.undo.edit.calculatecolumn.*;
import info.bioinfweb.treegraph.document.undo.edit.calculatecolumn.topology.IndexInParentFunction;
import info.bioinfweb.treegraph.document.undo.edit.calculatecolumn.topology.IsLeafFunction;
import info.bioinfweb.treegraph.document.undo.edit.calculatecolumn.topology.IsRootFunction;
import info.bioinfweb.treegraph.document.undo.edit.calculatecolumn.values.GetParentValueFunction;
import info.bioinfweb.treegraph.document.undo.edit.calculatecolumn.values.GetValueFunction;
import info.bioinfweb.treegraph.document.undo.edit.calculatecolumn.values.HasParentValueFunction;
import info.bioinfweb.treegraph.document.undo.edit.calculatecolumn.values.HasValueFunction;
import info.bioinfweb.treegraph.document.undo.edit.calculatecolumn.vararg.MaxFunction;
import info.bioinfweb.treegraph.document.undo.edit.calculatecolumn.vararg.MeanFunction;
import info.bioinfweb.treegraph.document.undo.edit.calculatecolumn.vararg.MinFunction;
import info.bioinfweb.treegraph.document.undo.edit.calculatecolumn.vararg.ProductFunction;
import info.bioinfweb.treegraph.document.undo.edit.calculatecolumn.vararg.SumFunction;
import info.bioinfweb.treegraph.document.undo.edit.calculatecolumn.vararg.VarArgFunction;



/**
 * Calculates a node/branch data column as specified by the passed expression.
 * 
 * @author Ben St&ouml;ver
 * @since 2.0.24
 */
public class CalculateColumnEdit extends NodeBranchDataEdit {
	public static final String CURRENT_VALUE_VAR = "THIS";
	public static final String UNIQUE_NODE_NAMES_VAR = "UNIQUE";
	public static final String NODE_NAMES_VAR = "NAME";
	public static final String BRANCH_LENGTH_VAR = "LENGTH";
	
	
  private String expression;
  private JEP parser;
  private Map<String, NodeBranchDataAdapter> adapterMap;
  private boolean isEvaluating = false;
  private boolean isEvaluatingDecimal = true;
  private Node position = null;
  private List<String> errors = new Vector<String>();
	
	
	public CalculateColumnEdit(Document document, NodeBranchDataAdapter targetAdapter, String expression) {
		super(document, targetAdapter);
		this.expression = expression;
		parser = createParser();
		adapterMap = createAdapterMap();
		backup = new NodeBranchDataBackup(targetAdapter, document.getTree().getPaintStart());
	}
	
	
	private void addFunction(JEP parser, AbstractFunction function) {
		parser.addFunction(function.getName(), function);
	}
	
	
	private void addVarArgFunction(JEP parser, VarArgFunction function) {
		addFunction(parser, function);
		addFunction(parser, function.createIDVersion());
	}
	
	
	private JEP createParser() {
		JEP result = new JEP();
		result.addStandardConstants();
		result.addStandardFunctions();
		
		result.addVariable(CURRENT_VALUE_VAR, adapter);
		result.addVariable(UNIQUE_NODE_NAMES_VAR, UniqueNameAdapter.getSharedInstance());
		result.addVariable(NODE_NAMES_VAR, NodeNameAdapter.getSharedInstance());
		result.addVariable(BRANCH_LENGTH_VAR, BranchLengthAdapter.getSharedInstance());
		
		addFunction(result, new GetValueFunction(this));
		addFunction(result, new HasValueFunction(this));
		addFunction(result, new GetParentValueFunction(this));
		addFunction(result, new HasParentValueFunction(this));
		
		addFunction(result, new IsRootFunction(this));
		addFunction(result, new IsLeafFunction(this));
		addFunction(result, new IndexInParentFunction(this));
		
		addVarArgFunction(result, new MinFunction(this));
		addVarArgFunction(result, new MaxFunction(this));
		addVarArgFunction(result, new SumFunction(this));
		addVarArgFunction(result, new ProductFunction(this));
		addVarArgFunction(result, new MeanFunction(this));

		return result;
	}
	
	
	private HashMap<String, NodeBranchDataAdapter> createAdapterMap() {
		HashMap<String, NodeBranchDataAdapter> result = new HashMap<String, NodeBranchDataAdapter>();
		
		String[] ids = IDManager.getLabelIDs(document.getTree().getPaintStart(), TextLabel.class);
		for (int i = 0; i < ids.length; i++) {
			result.put(ids[i], new TextLabelAdapter(ids[i], 
					new DecimalFormat(TextFormats.DEFAULT_DECIMAL_FORMAT_EXPR)));
		}
		
		ids = IDManager.getHiddenNodeDataIDs(document.getTree().getPaintStart());
		for (int i = 0; i < ids.length; i++) {
			result.put(ids[i], new HiddenNodeDataAdapter(ids[i])); 
		}
		
		ids = IDManager.getHiddenBranchDataIDs(document.getTree().getPaintStart());
		for (int i = 0; i < ids.length; i++) {
			result.put(ids[i], new HiddenBranchDataAdapter(ids[i])); 
		}
		
		return result;
	}
	
	
	/**
	 * Returns the node on which a value is currently calculated.
	 * 
	 * @return the current node if a concrete value is currently calculated or {@code null} if this instance is 
	 *         currently evaluating an expression
	 */
	public Node getPosition() {
		return position;
	}


	/**
	 * Allows to check if a function called by this step shall be executed or is just accessed during syntactical and 
	 * semantic evaluation.
	 * 
	 * @return {@code true} if the evaluation is currently ongoing, {@code false} if functions are executed to
	 *         calculate a concrete value
	 */
	public boolean isEvaluating() {
		return isEvaluating;
	}


	/**
	 * Evaluates the expression with one set of variable values.
	 * 
	 * @return the error information or {@code null} if no error occurred.
	 */
	private String evaluationStep() {
		String result = null;
		try {
			parser.evaluate(parser.parse(expression));
		}
		catch (ParseException e) {
			e.printStackTrace();
			result = e.getErrorInfo();
		}
		return result;
	}
	
	
	/**
	 * Evaluates the expression of this instance. Error can be obtained by calling 
	 * {@link CalculateColumnEdit#getErrors()}.
	 * 
	 * @return {@code true} if the expression contained no errors.
	 */
	public boolean evaluate() {
		isEvaluating = true;
		boolean result = false;
		try {
			errors.clear();
			isEvaluatingDecimal = true;
			String error = evaluationStep();
			result = (error == null);
			if (!result) {
				isEvaluatingDecimal = false;
				error = evaluationStep();
				result = error == null;
				if (!result) {
					errors.add(error);
				}
			}
		}
		finally {
			isEvaluating = false;
		}
		return result;
	}
	
	
	/**
	 * Returns the value of the specified column in the current line 
	 * (specified by {@link CalculateColumnEdit#position}).
	 * 
	 * @param adapter - defined the column to use
	 * @return a value of type {@link Double} or {@link String}
	 */
	public Object getValue(Node node, NodeBranchDataAdapter adapter) {
		if (isEvaluating) {
			if (isEvaluatingDecimal && !(adapter instanceof UniqueNameAdapter)) {
				return new Double(1);
			}
			else {
				return "a";
			}
		}
		else {
			if (adapter.isDecimal(node)) {
				return new Double(adapter.getDecimal(node));
			}
			else if (adapter.isString(node)) {
				return adapter.getText(node);
			}
			else {
				return null;
			}
		}
	}
	
	
	public boolean hasValue(Node node, NodeBranchDataAdapter adapter) {
		return isEvaluating || !adapter.isEmpty(node);
	}
	
	
	/**
	 * Returns the value of the current line in the column specified the passed id present.
	 * 
	 * @param id - the ID of the node/branch data column
	 * @return the value (as {@link Double}, {@link String}) or <code>null</code> if the specified column 
	 *         does not contain any value at the current position or a {@link Double} with the value 1 if 
	 *         {@link CalculateColumnEdit#isEvaluating} is <code>true</code>
	 * @throws UndefinedIDException - if no column with the specified ID exists 
	 */
	public Object getIDValue(Node node, String id) throws ParseException {
		NodeBranchDataAdapter adapter = adapterMap.get(id);
		if (adapter != null) {
			return getValue(node, adapter);
		}
		else {
			throw new UndefinedIDException("A node/branch data column with the ID \"" + id + 
					" \" does not exists.");
		}
	}
	
	
	public boolean hasIDValue(Node node, String id) {
		return hasValue(node, adapterMap.get(id));  // If adapter is undefined, false is returned.
	}
	
	
  private void calculateSubtree(Node root) {
  	position = root;
    parser.parseExpression(expression);
    if (parser.hasError()) {
    	errors.add(parser.getErrorInfo());
    }
    else {
    	Object result = parser.getValueAsObject();
    	if (result instanceof Double) {
    		adapter.setDecimal(root, (Double)result);
    	}
    	else if (result instanceof String) {
    		adapter.setText(root, (String)result);
    	}
    	else if (result instanceof Boolean) {
    		double value = 0d;
    		if ((Boolean)result) {
    			value = 1d;
    		}
    		adapter.setDecimal(root, value);
    	}
    	else {
    		adapter.delete(root);
    		errors.add("Invalid result type (Must be decimal or string.)");
    	}
    }
  	
  	for (int i = 0; i < root.getChildren().size(); i++) {
  		calculateSubtree(root.getChildren().get(i));
		}
  }
  
  
	@Override
	public void redo() throws CannotRedoException {
		errors.clear();
		calculateSubtree(document.getTree().getPaintStart());
		super.redo();
	}

	
	public String getPresentationName() {
		return "Calculate \"" + adapter.toString() + "\"";
	}
	
	
	/**
	 * Returns a description of the errors that occured during the last call of 
	 * {@link CalculateColumnEdit#redo()} or {@link CalculateColumnEdit#evaluate()}.
	 * 
	 * @return
	 */
	public String[] getErrors() {
		return errors.toArray(new String[errors.size()]);
	}
}
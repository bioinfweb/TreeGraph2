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
package info.bioinfweb.treegraph.document.undo.edit.calculatecolumn;


import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommandI;

import info.bioinfweb.commons.Math2;
import info.bioinfweb.treegraph.document.undo.edit.CalculateColumnEdit;



/**
 * Basic implementations for a custom JEP function.
 *  
 * @author Ben St&ouml;ver
 * @since 2.0.46 
 */
public abstract class AbstractFunction implements PostfixMathCommandI {
	private CalculateColumnEdit edit;
	private int curNumberOfParameters = 1;
	
	
	public AbstractFunction(CalculateColumnEdit edit) {
	  super();
	  this.edit = edit;
  }


	public abstract String getName();
	
	
	public CalculateColumnEdit getEdit() {
		return edit;
	}


  public static Double codeBoolean(boolean value) {
  	if (value) {
  		return new Double(1);
  	}
  	else {
  		return new Double(0);
  	}
  }
  
  
  public static double checkDouble(Object value) throws ParseException {
  	if (value instanceof Double) {
  		return (Double)value;
  	}
  	else {
  		throw new ParseException("Invalid parameter type. (Expected a numeric value.)");
  	}
  }
  
  
  public static long checkLong(Object value) throws ParseException {
  	double doubleValue = checkDouble(value);
  	if (Math2.isInt(doubleValue)) {
  		return (long)doubleValue;
  	}
  	else {
  		throw new ParseException("Invalid parameter type. (Expected an integer value.)");
  	}
  }
  
  
  public static int checkInteger(Object value) throws ParseException {
  	long longValue = checkLong(value);
  	if (Math2.isBetween(longValue, Integer.MIN_VALUE, Integer.MAX_VALUE)) {
  		return (int)longValue;
  	}
  	else {
  		throw new ParseException("Integer value out of range (" + longValue + "). Values must be between " + 
  				Integer.MIN_VALUE + " and " + Integer.MAX_VALUE + ".");
  	}
  }
  
  
  public static String checkString(Object value) throws ParseException {
  	if (value instanceof String) {
  		return (String)value;
  	}
  	else {
  		throw new ParseException("Invalid parameter type. (Expected a textual value.)");
  	}
  }
  
  
	@Override
	public void setCurNumberOfParameters(int n) {
		curNumberOfParameters = n;
	}


	protected int getCurNumberOfParameters() {
		return curNumberOfParameters;
	}
}

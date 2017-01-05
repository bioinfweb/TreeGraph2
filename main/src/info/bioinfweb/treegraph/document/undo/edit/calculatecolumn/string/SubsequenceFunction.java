/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2016  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.document.undo.edit.calculatecolumn.string;


import info.bioinfweb.treegraph.document.undo.edit.CalculateColumnEdit;

import org.nfunk.jep.ParseException;



public class SubsequenceFunction extends AbstractStringFunction {
	public SubsequenceFunction(CalculateColumnEdit edit) {
		super(edit);
	}


	@Override
	public boolean checkNumberOfParameters(int count) {
		return (count == 2) || (count == 3);
	}


	@Override
	public int getNumberOfParameters() {
		return -1;
	}

	
	@Override
	protected Object calculate(String text, Object[] additionalParameters) throws ParseException {
		try {
			if (additionalParameters.length == 2) {
				return text.substring(checkInteger(additionalParameters[0]), checkInteger(additionalParameters[1]));
			}
			else {
				return text.substring(checkInteger(additionalParameters[0]));
			}
		}
		catch (IndexOutOfBoundsException e) {
			if (getEdit().isEvaluating()) {  // During evaluation string parameters will always be "a" and are not necessarily long enough.
				return text;
			}
			else {
				throw new ParseException(e.getLocalizedMessage());
			}
		}
	}


	@Override
	public String getName() {
		return "subsequence";
	}
}

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
package info.bioinfweb.treegraph.document.io.jphyloio;


import info.bioinfweb.commons.text.StringUtils;
import info.bioinfweb.jphyloio.ReaderStreamDataProvider;
import info.bioinfweb.jphyloio.objecttranslation.implementations.IllegalArgumentExceptionSimpleValueTranslator;
import info.bioinfweb.treegraph.document.format.ScaleValue;
import info.bioinfweb.treegraph.document.io.xtg.XTGConstants;



public class ScaleValueTranslator extends IllegalArgumentExceptionSimpleValueTranslator<ScaleValue> {
	@Override
	public Class<ScaleValue> getObjectClass() {
		return ScaleValue.class;
	}

	
	@Override
	protected ScaleValue parseValue(String representation, ReaderStreamDataProvider<?> streamDataProvider)	throws IllegalArgumentException {
		representation = representation.trim();
		float value;
		boolean isInUnits;
		try {
			if (representation.endsWith(XTGConstants.MILLIMETERS)) {
				value = Float.parseFloat(StringUtils.cutEnd(representation, XTGConstants.MILLIMETERS.length()));
				isInUnits = false;
			}
			else if (representation.endsWith(XTGConstants.BRANCH_LENGTH_UNITS)) {
				value = Float.parseFloat(StringUtils.cutEnd(representation, XTGConstants.BRANCH_LENGTH_UNITS.length()));
				isInUnits = true;
			}
			else {
				throw new IllegalArgumentException("Scale values must end with a unit, either \"" + XTGConstants.BRANCH_LENGTH_UNITS +
						"\" or \"" + XTGConstants.MILLIMETERS + "\" (" + representation + ").");
			}
			
			ScaleValue result = new ScaleValue();
			if (isInUnits) {
				result.setInUnits(value);
			}
			else {
				result.setInMillimeters(value);
			}
			return result;
		}
		catch (NumberFormatException e) {
			throw new IllegalArgumentException(e);
		}
	}
}

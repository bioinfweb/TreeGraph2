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
package info.bioinfweb.treegraph.document.undo;

import info.bioinfweb.commons.Math2;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.io.newick.NewickStringChars;



/**
 * Stores user parameters used to import text element data.
 * 
 * @author Ben St&ouml;ver
 * @since 2.2.0
 */
public class ImportTextElementDataParameters {
  private boolean ignoreWhitespace = false;
  private boolean distinguishSpaceUnderscore = false;
  private boolean caseSensitive = false;
  private boolean parseNumericValues = true;

  
	public boolean isIgnoreWhitespace() {
		return ignoreWhitespace;
	}


	public void setIgnoreWhitespace(boolean ignoreWhitespace) {
		this.ignoreWhitespace = ignoreWhitespace;
	}


	public boolean isDistinguishSpaceUnderscore() {
		return distinguishSpaceUnderscore;
	}


	public void setDistinguishSpaceUnderscore(boolean distinguishSpaceUnderscore) {
		this.distinguishSpaceUnderscore = distinguishSpaceUnderscore;
	}


	public boolean isCaseSensitive() {
		return caseSensitive;
	}


	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}


	public boolean isParseNumericValues() {
		return parseNumericValues;
	}


	public void setParseNumericValues(boolean parseNumbericValues) {
		this.parseNumericValues = parseNumbericValues;
	}  
	
	
	/**
	 * Creates a {@link TextElementData} object from the specified string according the specifications
	 * in {@code parameters}.
	 */
	public TextElementData createEditedValue(String text) {
		if (text.equals("")) {
			return new TextElementData();  // return empty instance because the if the the TextElementData instance used to call this method was also empty
		}
		else {
			TextElementData result = new TextElementData(text);
			if (isParseNumericValues()) {  
				try {
					result.setDecimal(Math2.parseDouble(result.getText()));
				}
				catch (NumberFormatException e) {}  // nothing to do
			}
	
			if (result.isString()) {  // Not the case if the parsing above was successful.
				if (isIgnoreWhitespace()) {
					result.setText(result.getText().trim());
				}
				if (!isCaseSensitive()) {
					result.setText(result.getText().toLowerCase());
				}
				if (!isDistinguishSpaceUnderscore()) {
					result.setText(result.getText().replaceAll(Character.toString(NewickStringChars.FREE_NAME_BLANK), " "));
				}
			}
			return result;
		}
	}
}

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
}

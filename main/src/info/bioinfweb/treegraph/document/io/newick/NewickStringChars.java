/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2015  Ben Stöver, Kai Müller
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
package info.bioinfweb.treegraph.document.io.newick;



public class NewickStringChars {
	public static final char SUBTREE_START = '(';
	public static final char SUBTREE_END = ')';
	public static final char NAME_DELIMITER = '\'';
	public static final char LENGTH_SEPERATOR = ':'; 
	public static final char ELEMENT_SEPERATOR = ','; 
	public static final char TERMINAL_SYMBOL = ';';
	public static final char COMMENT_START = '[';
	public static final char COMMENT_END = ']';
	public static final char FREE_NAME_BLANK = '_';
	
	
	public static boolean isFreeNameChar(char c) {
		return isFreeNameFirstChar(c); // || Character.isDigit(c);
	}
	

	public static boolean isFreeNameFirstChar(char c) {
		return (c != SUBTREE_END) && (c != LENGTH_SEPERATOR) && (c != ELEMENT_SEPERATOR) && (c != COMMENT_START) && (c != TERMINAL_SYMBOL) && !Character.isWhitespace(c); 
		//return Character.isLetter(c) || (c == FREE_NAME_BLANK) || (c == '.') || (c == '-') || (c == '/') || Character.isDigit(c);
	}	
	
	
	public static boolean isCharAfterLength(char c) {
		return Character.isWhitespace(c) || (c == ELEMENT_SEPERATOR) || (c == SUBTREE_END) || (c == COMMENT_START) || (c == TERMINAL_SYMBOL);
	}
}
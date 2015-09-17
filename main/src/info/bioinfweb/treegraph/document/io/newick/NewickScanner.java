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
package info.bioinfweb.treegraph.document.io.newick;


import info.bioinfweb.treegraph.document.io.nexus.NexusParser;

import java.util.List;
import java.util.Vector;



public class NewickScanner extends NewickStringChars {
	private static NewickToken readFreeName(final String text, int start) {
		String result = "" + text.charAt(start);
		int pos = start + 1;
		while ((pos < text.length()) && isFreeNameChar((text.charAt(pos)))) {
			if (text.charAt(pos) == FREE_NAME_BLANK) {
				result += ' ';
			}
			else {
				result += text.charAt(pos);
			}
			pos++;
		}
		
		if (pos == text.length()) {
			throw NewickException.getUnterminatedNameException(start, text);
		}
		else {
			return new NewickToken(start, result, false);  //TODO end - 1?
		}
	}
	
	
	private static int readDelimitedName(final String text, int start, NewickToken token) {
		start++;  // NAME_DELIMITER am Anfang �berspringen.
		int pos = start;
		String result = "";
		do {
			while ((pos < text.length()) && (text.charAt(pos) != NAME_DELIMITER)) {
				result += text.charAt(pos);
				pos++;
			}
			if ((pos + 1 < text.length()) && (text.charAt(pos + 1) == NAME_DELIMITER)) {
				result += NAME_DELIMITER;  // Erm�glichen von 'abc'''
				pos +=2;  
			}
		} while (pos < text.length() && (text.charAt(pos) != NAME_DELIMITER));
		
		if (pos == text.length()) {
			throw NewickException.getUnterminatedNameException(start, text);
		}
		else {
			token.setText(result);
			token.setDelimited(true);
			return pos;
		}
	}
	
	
	/**
	 * Reads a length statement in an Newick string.
	 * @param token the token where the length value shall be stored
	 * @param text the Newick string
	 * @param start ste start position of the length statement
	 * @return the position to go on tokenizing
	 */
	private static int readBranchLength(final String text, int start, NewickToken token) {
		int end = start;
		while ((end < text.length()) && (!isCharAfterLength(text.charAt(end)))) {
			end++;
		}
		
		double value; 
		try {
			value = Double.parseDouble(text.substring(start, end));  //TODO end - 1?
		}
		catch (NumberFormatException e) {
			throw new NewickException(start, text, "Illegal length statement");
		}
		token.setLength(value);
		return end - 1; 
	}
	
	
	/**
	 * Reads a comment and adds it to last token in the passed list. If the comment is a rooted- or 
	 * unrooted-command the according token is added.
	 * @param text
	 * @param start
	 * @param tokenList
	 * @return
	 * @throws NewickException if the comment is unterminated
	 */
	private static int readComment(final String text, int start, List<NewickToken> tokenList) {
		start++;  // COMMENT_START am Anfang �berspringen.
		int pos = start;
		String result = "";
		while ((pos < text.length()) && (text.charAt(pos) != COMMENT_END)) {
			result += text.charAt(pos);
			pos++;
		}
		
		if (pos == text.length()) {
			throw NewickException.getUnterminatedCommentException(start, text);
		}
		else {
			if (result.toLowerCase().equals(NexusParser.UNROOTED_COMMAND)) {
				tokenList.add(new NewickToken(TokenType.UNROOTED_COMMAND, start));
			}
			else if (result.toLowerCase().equals(NexusParser.ROOTED_COMMAND)) {
				tokenList.add(new NewickToken(TokenType.ROOTED_COMMAND, start));
			}
			else {
				tokenList.get(tokenList.size() - 1).setComment(result);
			}
			return pos;
		}
	}
		
 	public static List<NewickToken> parse(final String text) {
		List<NewickToken> result = new Vector<NewickToken>();
		
		NewickToken token;
		int pos = 0;
  	while ((pos < text.length()) && (text.charAt(pos) != TERMINAL_SYMBOL)) {
  		switch (text.charAt(pos)) {
  		  case SUBTREE_START:
  		  	result.add(new NewickToken(TokenType.SUBTREE_START, pos));
  		  	break;
  		  case SUBTREE_END:
  		  	result.add(new NewickToken(TokenType.SUBTREE_END, pos));
  		  	break;
  		  case ELEMENT_SEPERATOR:
  		  	result.add(new NewickToken(TokenType.ELEMENT_SEPARATOR, pos));
  		  	break;
  		  case LENGTH_SEPERATOR:
  		  	token = new NewickToken(TokenType.LENGTH, pos);
  		  	pos++; // LENGTH_SEPERATOR selbst �berspringen
  		  	if (text.charAt(pos) == COMMENT_START) {  // Sonderfall: Kommentar zwischen LENGTH_SEPERATOR und L�ngenangabe 
    		  	pos = readComment(text, pos, result) + 1;  // Zus�tzlich COMMENT_END �berspringen
  		  	}
  		  	pos = readBranchLength(text, pos, token);
  		  	result.add(token);
  		  	break;
  		  case NAME_DELIMITER:
  		  	token = new NewickToken(TokenType.NAME, pos); 
  		  	pos = readDelimitedName(text, pos, token);
 		  		result.add(token);
  		  	break;
  		  case COMMENT_START:
  		  	pos = readComment(text, pos, result);
  		  	break;
  		  default:
  		    if (isFreeNameFirstChar(text.charAt(pos))) {
  		    	token = readFreeName(text, pos);
  		    	pos += token.getText().length() - 1;
  		    	result.add(token);
  		    }
  		}
  		pos++;
		}
  	if ((pos < text.length()) && (text.charAt(pos) == TERMINAL_SYMBOL)) {
	  	result.add(new NewickToken(TokenType.TERMNINAL_SYMBOL, pos));
  	}
		
		return result;
	}
}
/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2015-2016  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.document.io.nexus;


import info.bioinfweb.treegraph.document.io.newick.NewickStringChars;

import java.io.IOException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class NexusParser {
	public static final int MAX_EXPECTED_COMMAND_LENGTH = 512 * 1024;
	
	public static final String FIRST_LINE = "#nexus"; 
	public static final String BEGIN_COMMAND = "begin";
	public static final String END_COMMAND = "end";
	public static final char COMMAND_END = ';';
	public static final char COMMENT_START = '['; 
	public static final char COMMENT_END = ']'; 
	public static final char KEY_VALUE_SEPERATOR = '=';
	public static final char WORD_DELIMITER = '\'';
	public static final char WHITESPACE = ' ';
	
	public static final String BLOCK_NAME_TAXA = "taxa";
	public static final String BLOCK_NAME_TREES = "trees";
	
	public static final String DIMENSIONS_NAME = "dimensions";
	public static final String TAXLABELS_NAME = "taxlabels";
	public static final String TRANSL_TABLE_NAME = "translate";
	public static final String TREE_NAME = "tree";
	
	public static final String DIMENSIONS_SUBCOMMAND_NTAX = "ntax";
	
	public static final String ROOTED_HOT_COMMENT = "&r";
	public static final String UNROOTED_HOT_COMMENT = "&u";
	
	public static final Pattern BLOCK_BEGIN_PATTERN = Pattern.compile(BEGIN_COMMAND);
	public static final Pattern BLOCK_END_PATTERN = Pattern.compile("(" + END_COMMAND + "|endblock)");
	public static final Pattern COMMAND_PATTERN = Pattern.compile("" + COMMAND_END);
	public static final Pattern NAME_SEPARATOR_PATTERN = Pattern.compile("'");
	public static final Pattern ENTRY_PATTERN = Pattern.compile("\\s*(\\d*)\\s*(.*)\\s*");
	public static final Pattern ENCLOSED_NAME_PATTERN = Pattern.compile("\\s*(\\d*)\\s*'(.*)'\\s*");  // Can only be used for substrings containing not more than one name.
	public static final Pattern TREES_PATTERN = Pattern.compile("\\s*" + BLOCK_NAME_TREES + "\\s*");
	public static final Pattern TRANSL_TABLE_PATTERN = Pattern.compile(TRANSL_TABLE_NAME);
	public static final Pattern TRANSL_TABLE_SEPARATOR_PATTERN = Pattern.compile(","); 
	public static final Pattern TREE_COMMAND_PATTERN = Pattern.compile("(" + TREE_NAME + "|utree)");
	public static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");
	public static final Pattern EMPTY_PATTERN = Pattern.compile(".*", Pattern.DOTALL);
	 
	
	
  /**
   * Splits the passed text into Nexus commands. Comments are not removed.
   * 
   * @param content
   * @return
   */
  private static NexusCommand[] scan(String content) {
  	Vector<NexusCommand> result  = new Vector<NexusCommand>();
  	StringBuffer command = new StringBuffer(Math.max(MAX_EXPECTED_COMMAND_LENGTH, content.length())); 
  	for (int i = 0; i < content.length(); i++) {
  		switch (content.charAt(i)) {
  			case COMMENT_START:
  				i++;
  				command.append(COMMENT_START);
  				while ((i < content.length()) && (content.charAt(i) != COMMENT_END)) {
  					command.append(content.charAt(i));
  					i++;
  				}
  				command.append(COMMENT_END);
  				break;
  			case WORD_DELIMITER:
  				i++;
  				command.append(WORD_DELIMITER);
  				while ((i < content.length()) && (content.charAt(i) != WORD_DELIMITER)) {
  					command.append(content.charAt(i));
  					i++;
  				}
  				command.append(WORD_DELIMITER);
  				break;
  			case COMMAND_END:
  				result.add(new NexusCommand(command.toString()));
  				command.delete(0, command.length());  //TODO Warum gibt es kein clear()?
  				break;
  			default:
  				command.append(content.charAt(i));
  			  break;
  		}
		}
  	return result.toArray(new NexusCommand[result.size()]);
  }
  
  
  /**
   * Returns the index of the next occurrence of <code>character</code> after <code>start</code>
   * which is not located inside a comment.
   * 
   * @param text - the text to be searched
   * @param start - the position to start searching
   * @param character - the sought-after character
   * @return the position or -1 if the character was not found
   */
  public static int nextPosOutsideComment(String text, int start, char character) {
  	for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == COMMENT_START) {
				do {
					i++;
				} while ((i < text.length()) && (text.charAt(i) != COMMENT_END));
			}
			else if (text.charAt(i) == character) {
				return i;
			}
		}
  	return -1;
  }
  
  
  /**
   * Returns the specified string without any comments (separated by "[" and "]").
   * @param content
   * @return
   */
  public static String removeComments(String content) {
  	StringBuffer result = new StringBuffer(content.length());
  	for (int i = 0; i < content.length(); i++) {
  		if (content.charAt(i) == COMMENT_START) {
				do {
					i++;
				} while ((i < content.length()) && (content.charAt(i) != COMMENT_END));
  		}
  		else {
  			result.append(content.charAt(i));
  		}
		}
  	return result.toString();
  }
  
  
  /**
   * Searches for the first occurrence of the given command.
   * @param commands - the list of all commands
   * @param start - the command index to start the search
   * @param end - the command index to stop the search
   * @param namePattern - the name of the command (The pattern should describe all 
   *        possible names. Patterns are not matched case sensitive)
   * @param tokenPattern - describes how the token of the command should look like 
   *        (Patterns are not matched case sensitive)
   * @return the index of the first match in the given array of command or -1 if none was found
   */
  private static int findCommand(NexusCommand[] commands, int start, int end, 
  		Pattern namePattern, Pattern tokenPattern) {
  	
  	for (int i = start; i < Math.min(commands.length, end); i++) {
			if (namePattern.matcher(commands[i].getName().toLowerCase()).matches() && 
					tokenPattern.matcher(commands[i].getTokens().trim().toLowerCase()).matches()) {
				return i;
			}
		}
  	return -1;
  }
  
  
  private static int findBlockEnd(NexusCommand[] commands, int start, int end) {
  	return findCommand(commands, start, end, BLOCK_END_PATTERN, EMPTY_PATTERN);
  }
  
  
  private static String parseName(String name) {
  	if (name.contains(Character.toString(WORD_DELIMITER))) {
  		String result = "";
  		for (int i = 0; i < name.length(); i++) {
  			if (name.charAt(i) != WORD_DELIMITER) {
  				result += name.charAt(i);
  			}
  			if ((i + 1 < name.length()) && name.charAt(i + 1) == WORD_DELIMITER) {
  				result += WORD_DELIMITER;  // Ermöglichen von 'abc'''
  			}
  		}
  		return result;
  	}
  	else {
  		return name.replaceAll(Character.toString(NewickStringChars.FREE_NAME_BLANK), " ");
  	}
  }
  
  
  /**
   * Reads the content of the passed translation table from the passed command. The command 
   * tokens-string may contain comments.
   * 
   * @param command
   * @param translTable
   */
  private static void readTranslTable(NexusCommand command, TranslTable translTable) throws IOException {
  	String[] entrys = TRANSL_TABLE_SEPARATOR_PATTERN.split(removeComments(command.getTokens()));
  	Matcher entryPatternMatcher;
  	Matcher enclosedPatternMatcher;
  	String resultKey = "";
  	String resultName = "";
  	for (int i = 0; i < entrys.length; i++) {
  		entryPatternMatcher = ENTRY_PATTERN.matcher(entrys[i]);
  		enclosedPatternMatcher = ENCLOSED_NAME_PATTERN.matcher(entrys[i]);
  		if (entryPatternMatcher.matches() && !enclosedPatternMatcher.matches()) {
  			resultKey = entryPatternMatcher.group(1);
  			resultName = entryPatternMatcher.group(2);  			
  		}
  		else if (enclosedPatternMatcher.matches()) {
				resultKey = enclosedPatternMatcher.group(1);
  			resultName = enclosedPatternMatcher.group(2);
			}
			else {
				throw new IOException("Line " + (i + 1) + " of the translation table contains invalid entries.");
			}  			
			if (resultKey.isEmpty()) {
				resultKey = String.valueOf(i + 1);
			}
			translTable.add(parseName(resultKey), parseName(resultName));
		}
  }
  
  
  /**
   * Reads a tree from the passed Nexus command tokens and adds it to the passed document.
   * Comments are left inside both the name and the tree string.
   * 
   * @param tokens
   * @param document
   */
  private static boolean readTree(String tokens, NexusDocument document) {
  	int separatorPos = nextPosOutsideComment(tokens, 0, KEY_VALUE_SEPERATOR);
  	boolean result = separatorPos != -1; 
  	if (result) {
  		document.add(tokens.substring(0, separatorPos).trim(), 
  				tokens.substring(separatorPos + 1, tokens.length()).trim() + 
  				NewickStringChars.TERMINAL_SYMBOL);
  	}
  	return result;
  }
  
  
  public static NexusDocument parse(String content) throws NexusException, IOException {
  	content = content.trim();
  	if (content.toLowerCase().startsWith(FIRST_LINE)) {
  		NexusCommand[] commands = scan(content.substring(FIRST_LINE.length()));
  		int treesStart = findCommand(
  				commands, 0, commands.length, BLOCK_BEGIN_PATTERN, TREES_PATTERN);
  		if (treesStart != -1) {
  			int treesEnd = findBlockEnd(commands, treesStart + 1, commands.length);
  			if (treesEnd != -1) {
  				NexusDocument result = new NexusDocument();
  				int translPos = findCommand(
  						commands, treesStart, treesEnd, TRANSL_TABLE_PATTERN, EMPTY_PATTERN);
  				if (translPos != -1) {
  					readTranslTable(commands[translPos], result.getTranslTable());
  				}
  				
  				int treePos = findCommand(
  						commands, treesStart, treesEnd, TREE_COMMAND_PATTERN, EMPTY_PATTERN);
  				while (treePos != -1) {
  					readTree(commands[treePos].getTokens(), result);
    				treePos = findCommand(
    						commands, treePos + 1, treesEnd, TREE_COMMAND_PATTERN, EMPTY_PATTERN);
  				}
  				
  				return result;
  			}
  			else {
  	  		throw new NexusException(NexusError.TREES_UNTERMINATED);
  			}
  		}
  		else {
    		throw new NexusException(NexusError.NO_TREES);
  		}
  	}
  	else {
  		throw new NexusException(NexusError.NO_NEXUS);
  	}
  }
}
/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2013  Ben Stöver, Kai Müller
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


import java.util.regex.Pattern;



public class NexusCommand {
	private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");
	private String name = "";
	private String tokens = "";
	
	
  public NexusCommand(String content) {
  	content = content.trim();
  	int splitPos = 0;
  	while ((splitPos < content.length()) && !WHITESPACE_PATTERN.matcher(content.substring(splitPos)).lookingAt()) {
  		if (content.charAt(splitPos) == NexusParser.COMMENT_START) {
  			do {  // Kommentare überspringen
  				splitPos++;
  			} while ((splitPos < content.length()) && content.charAt(splitPos) != NexusParser.COMMENT_END);
  			splitPos++;  // Kommentarende überspringen
  	  	while ((splitPos < content.length()) && WHITESPACE_PATTERN.matcher(content.substring(splitPos)).lookingAt()) {
  	  		splitPos++;  // Whitespace hinter Kommentar überspringen
  	  	}
  		}
  		else {
  			splitPos++;
  		}
  	}
 		name = NexusParser.removeComments(content.substring(0, splitPos)).trim();
 		if (splitPos + 1 < content.length()) {
 			tokens = content.substring(splitPos + 1, content.length());
 		}
  }
	
	
  public NexusCommand(String name, String tokens) {
  	super();
  	this.name = name;
  	this.tokens = tokens;
  }


	public String getName() {
		return name;
	}


	public String getTokens() {
		return tokens;
	}
}
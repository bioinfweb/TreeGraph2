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



/**
 * An instance of this exception is thrown if an input newick string contains a syntactical error and 
 * cannot be read.
 * 
 * @author Ben St&ouml;ver
 */
public class NewickException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public static final int CHAR_COUNT_BEFORE = 20;
	public static final int CHAR_COUNT_AFTER = 30;
	
	
	private int position;
	private String sourceBefore;
	private String sourceAfter;


  private void storePositionInfo(int pos, final String source) {
		position = pos;
  	sourceBefore = source.substring(Math.max(0, pos - CHAR_COUNT_BEFORE), pos);
  	sourceAfter = source.substring(pos, Math.min(source.length(), pos + CHAR_COUNT_AFTER));
  }
	
	
  public NewickException(int pos, final String source, String msg) {
  	super(msg);
		storePositionInfo(pos, source);
  }
  
  
  public NewickException(int pos, final String source, TokenType expected, TokenType found) {
  	super("Token " + expected.toString() + " expected but token " + found.toString() + " found.");
		storePositionInfo(pos, source);
  }
  
  
  public NewickException(NewickToken token, final String source) {
  	super("Unexpected token " + token.getType().toString() + ".");
		storePositionInfo(token.getTextPos(), source);
  }
  
  
  public static NewickException getUnterminatedNameException(int start, final String source) {
  	return new NewickException(start, source, "Unterminated name");
  }


  public static NewickException getUnterminatedCommentException(int start, final String source) {
  	return new NewickException(start, source, "Unterminated comment");
  }


	/**
	 * The position of the error in the newick string.
	 * @return
	 */
	public int getPosition() {
		return position;
	}


	/**
	 * Returns a part of the newick source starting at the error position.
	 * @return
	 */
	public String getSourceAfter() {
		return sourceAfter;
	}


	/**
	 * Returns a part of the newick source ending before the error position.
	 * @return
	 */
	public String getSourceBefore() {
		return sourceBefore;
	}
}
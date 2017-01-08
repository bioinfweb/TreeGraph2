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
package info.bioinfweb.treegraph.document.io.newick;



public class NewickToken {
	private TokenType type = TokenType.SUBTREE_START;
	private int textPos;
	private String text = "";
	private double length = 0;
	private boolean delimited = false;
	private String comment = "";
	
	
	public NewickToken(TokenType type, int textPos) {
		this.type = type;
		this.textPos = textPos;
	}

	
	public NewickToken(int textPos, String text, boolean delimited) {
		this.type = TokenType.NAME;
		this.textPos = textPos;
		this.text = text;
		this.delimited = delimited;
	}
	
	
	public NewickToken(int textPos, double length) {
		this.type = TokenType.LENGTH;
		this.textPos = textPos;
		this.length = length;
	}


	public double getLength() {
		return length;
	}


	public String getText() {
		return text;
	}


	public TokenType getType() {
		return type;
	}


	public void setLength(double length) {
		this.length = length;
	}


	public void setText(String text) {
		this.text = text;
	}


	public int getTextPos() {
		return textPos;
	}


	public boolean wasDelimited() {
		return delimited;
	}


	public void setDelimited(boolean delimited) {
		this.delimited = delimited;
	}


	/**
	 * Returns the comment which was located behind this element in the Newick string (if there was 
	 * any).
	 * @return
	 */
	public String getComment() {
		return comment;
	}


	public void setComment(String comment) {
		this.comment = comment;
	}
}
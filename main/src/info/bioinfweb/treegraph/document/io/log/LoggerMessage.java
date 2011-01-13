/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011  Ben Stöver, Kai Müller
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
package info.bioinfweb.treegraph.document.io.log;


import info.bioinfweb.treegraph.document.io.DocumentReader;


/**
 * Represents a message send by a document reader (see {@link DocumentReader} to a {@link LoadLogger}. 
 * 
 * @since 2.0.42
 * @author Ben St&ouml;ver
 */
public class LoggerMessage {
	private LoggerMessageType type = null;
  private String message = "";
  private int helpCode = -1;
  
  
	public LoggerMessage(LoggerMessageType type, String message) {
		this(type, message, -1);
	}
  
  
	public LoggerMessage(LoggerMessageType type, String message, int helpCode) {
		super();
		this.type = type;
		this.message = message;
		this.helpCode = helpCode;
	}


	public LoggerMessageType getType() {
		return type;
	}


	public void setType(LoggerMessageType type) {
		this.type = type;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}


	public int getHelpCode() {
		return helpCode;
	}


	public void setHelpCode(int helpCode) {
		this.helpCode = helpCode;
	}


	@Override
	public String toString() {
		String result = getMessage();
		if (!getType().equals(LoggerMessageType.MESSAGE)) {
			result = getType().toString() + ": " + result;
		}
		return result;
	}
}
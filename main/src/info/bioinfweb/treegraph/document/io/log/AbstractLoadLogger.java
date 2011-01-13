/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011  Ben St�ver, Kai M�ller
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



/**
 * Implements basic functionality (especially method delegation) of a load logger.
 * 
 * @since 2.0.42
 * @author Ben St&ouml;ver
 */
public abstract class AbstractLoadLogger implements LoadLogger {
	@Override
	public void addMessage(String message, int helpCode) {
		addMessage(new LoggerMessage(LoggerMessageType.MESSAGE, message, helpCode));
	}
	

	@Override
	public void addMessage(String message) {
		addMessage(new LoggerMessage(LoggerMessageType.MESSAGE, message));
	}

	
	@Override
	public void addWarning(String message, int helpCode) {
		addMessage(new LoggerMessage(LoggerMessageType.WARNING, message, helpCode));
	}

	
	@Override
	public void addWarning(String message) {
		addMessage(new LoggerMessage(LoggerMessageType.WARNING, message));
	}
}
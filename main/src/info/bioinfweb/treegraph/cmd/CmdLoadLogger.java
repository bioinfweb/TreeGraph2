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
package info.bioinfweb.treegraph.cmd;


import info.webinsel.util.log.AbstractApplicationLogger;
import info.webinsel.util.log.ApplicationLogger;
import info.webinsel.util.log.ApplicationLoggerMessage;
import info.webinsel.util.log.ApplicationLoggerMessageType;



/**
 * Displays the messages of a <code>DocumentReader</code> class on the command line.
 * It is implemented as a singleton.
 * 
 * @since 2.0.42
 * @author Ben St&ouml;ver
 */
public class CmdLoadLogger extends AbstractApplicationLogger implements ApplicationLogger {
	public static final String WARNING_PREFIX = "WARNING: ";
	
	private static CmdLoadLogger firstInstace = null;
	
	
	protected CmdLoadLogger() {}
	
	
	public static CmdLoadLogger getInstance() {
		if (firstInstace == null) {
			firstInstace = new CmdLoadLogger();
		}
		return firstInstace;
	}
	
	
	@Override
	public void addMessage(ApplicationLoggerMessage message) {
		if (message.getType().equals(ApplicationLoggerMessageType.WARNING)) {
			System.out.print(WARNING_PREFIX);
		}
		System.out.println(message.getMessage());
	}
}
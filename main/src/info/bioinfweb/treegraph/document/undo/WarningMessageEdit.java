/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2015-2016  Ben Stöver, Sarah Wiechers
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
 * This interface should be implemented by all edit classes that could produce warning
 * messages.
 * <p>
 * Note that edit classes should never output warning messages directly
 * to the user. Instead action objects or other calling classes the display the messages
 * provided by the methods of this interface.
 * 
 * @author Ben St&ouml;ver
 */
public interface WarningMessageEdit {
	/**
	 * Returns a warning text, if the last call of {@link #redo()} produced warnings.
	 * @return the warning text or {@code null} if no warning occurred
	 */
	public String getWarningText();
  
	public boolean hasWarnings();
}
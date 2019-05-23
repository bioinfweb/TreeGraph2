/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2019  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph;



public interface PreferencesConstants {
	public static final String FIRST_RUN_PREF_KEY = "isFirstRun";
	public static final String LAST_VERSION_CHECK_PREF_KEY = "lastVersionCheck";
	public static final String DO_VERSION_CHECK_PREF_KEY = "doVersionCheck";
	public static final String DO_CHECK_SEL_SYNC_PREF_KEY = "doCheckAdaptersOnSelSync";

	public static final boolean DO_VERSION_CHECK_DEFAULT_VALUE = true;
	public static final boolean DO_CHECK_SEL_SYNC_DEFAULT_VALUE = true;
	public static final long VERSION_CHECK_INTERVAL_DEFAULT_VALUE = 1000 * 60 * 60;  // 1 h
}

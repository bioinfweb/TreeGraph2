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
package info.bioinfweb.treegraph.document.format;


import info.bioinfweb.treegraph.document.PieChartLabel;



/**
 * Determines what kind of data is shown in section captions of {@link PieChartLabel}s. 
 * 
 * @author Ben St&ouml;ver
 * @since 2.13.0
 */
public enum PieChartLabelCaptionContentType {
	NONE {
		@Override
		public String toString() {
			return "Do not show any captions";
		}
	},
	
	CAPTIONS {
		@Override
		public String toString() {
			return "Show section names";
		}
	},
	
	VALUES {
		@Override
		public String toString() {
			return "Show section values";
		}
	},
	
	BOTH {
		@Override
		public String toString() {
			return "Show section names and values";
		}
	};
	
	
	public boolean containsCaptions() {
		return (CAPTIONS.equals(this) || BOTH.equals(this));
	}
	
	
	public boolean containsValues() {
		return (VALUES.equals(this) || BOTH.equals(this));
	}
}

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
package info.bioinfweb.treegraph.document.format.operate;


import info.bioinfweb.treegraph.document.format.ElementFormats;
import info.bioinfweb.treegraph.document.format.PieChartLabelFormats;



/**
 * Format operator that is able to apply different text formats to the caption text formats of a pie chart label.
 * 
 * @author Ben St&ouml;ver
 * @since 2.13.0
 */
public class PieChartCaptionTextOperator extends AbstractPieChartLabelOperator {
	private AbstractTextOperator textOperator;
	
	
	public PieChartCaptionTextOperator(AbstractTextOperator textOperator) {
		super();
		this.textOperator = textOperator;
	}


	@Override
	protected void doApplyTo(ElementFormats format) {
		textOperator.applyTo(((PieChartLabelFormats)format).getCaptionsTextFormats());
	}
}

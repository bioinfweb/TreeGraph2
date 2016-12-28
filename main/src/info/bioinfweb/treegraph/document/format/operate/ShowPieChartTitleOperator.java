/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2016  Ben Stöver, Sarah Wiechers, Kai Müller
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
 * Format operator to set whether captions of pie chart labels shall be shown.
 * 
 * @author Ben St&ouml;ver
 * @since 2.13.0
 */
public class ShowPieChartTitleOperator extends AbstractPieChartLabelOperator {
  private boolean show;

  
	public ShowPieChartTitleOperator(boolean show) {
		super();
		this.show = show;
	}


	@Override
	protected void doApplyTo(ElementFormats format) {
		((PieChartLabelFormats)format).setShowTitle(show);
	}
}
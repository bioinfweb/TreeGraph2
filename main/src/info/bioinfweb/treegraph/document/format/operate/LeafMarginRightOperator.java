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
package info.bioinfweb.treegraph.document.format.operate;


import info.bioinfweb.treegraph.document.format.DistanceValue;
import info.bioinfweb.treegraph.document.format.Margin;



/**
 * @author Ben St&ouml;ver
 * @since 2.0.25
 */
public class LeafMarginRightOperator extends AbstractLeafMarginOperator implements FormatOperator {
	public LeafMarginRightOperator(DistanceValue value) {
		super(value);
	}

	
	@Override
	protected void applyTo(Margin margin) {
		margin.getRight().assign(getValue());
	}
}
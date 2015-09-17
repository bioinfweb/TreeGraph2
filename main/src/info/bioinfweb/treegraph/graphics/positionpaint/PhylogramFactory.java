/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2015  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.graphics.positionpaint;



/**
 * The factory class for the phylogram/chronogram view.
 * @author Ben St&ouml;ver
 */
class PhylogramFactory implements SinglePositionPaintFactory {
	public TreePositioner getPositioner() {
		return PhylogramPositioner.getInstance();
	}


	public TreePainter getPainter() {
		return PhylogramPainter.getInstance();
	}


	public boolean isPositioner(TreePositioner positioner) {
		return PhylogramPositioner.class.getName().equals(positioner.getClass().getName());
	}


	public boolean isPainter(TreePainter painter) {
		return PhylogramPainter.class.getName().equals(painter.getClass().getName());
	}


	public String name() {
		return "Phylogram/Chronogram";
	}


	public boolean needsBranchLengths() {
		return true;
	}
}
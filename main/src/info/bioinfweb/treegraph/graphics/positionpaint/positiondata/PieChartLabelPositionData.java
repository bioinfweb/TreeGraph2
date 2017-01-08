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
package info.bioinfweb.treegraph.graphics.positionpaint.positiondata;


import info.bioinfweb.treegraph.document.PieChartLabel;
import info.bioinfweb.treegraph.document.format.DistanceDimension;
import info.bioinfweb.treegraph.document.format.DistanceValue;

import java.util.ArrayList;
import java.util.List;



/**
 * The position data of a {@link PieChartLabel}.
 * 
 * @author Ben St&ouml;ver
 * @since 2.13.0
 */
public class PieChartLabelPositionData extends PositionData {
	/**
	 * Contains the position of a single caption of a pie chart label. The left and top values stored here are relative to the
	 * left and top value of the whole label. {@link #getLineStartX()} and {@link #getLineStartY()} are relative to the top left 
	 * position of the chart (not the whole label).
	 * 
	 * @author Ben St&ouml;ver
	 * @since 2.13.0
	 */
	public static class CaptionPositionData extends PositionData {
		private int captionIndex = 0;
		private DistanceValue lineStartX = new DistanceValue();
		private DistanceValue lineStartY = new DistanceValue();
		
		
		public CaptionPositionData(int captionIndex) {
			super();
			this.captionIndex = captionIndex;
		}


		/**
		 * Returns the index of the caption displayed at this position. The order of captions is determined by the location of
		 * their chart section for some link types.
		 * 
		 * @return the index of the captions (and its chart section) in the pie chart label
		 */
		public int getCaptionIndex() {
			return captionIndex;
		}
	
		
		public void setCaptionIndex(int captionIndex) {
			this.captionIndex = captionIndex;
		}


		/**
		 * Returns the x-coordinate of the point where a possible caption link line would start. 
		 * 
		 * @return a value relative to the left position of the chart
		 */
		public DistanceValue getLineStartX() {
			return lineStartX;
		}


		/**
		 * Returns the y-coordinate of the point where a possible caption link line would start.
		 * (Note that y-coordinates are screen coordinates and increase from top to bottom.) 
		 * 
		 * @return a value relative to the top position of the chart
		 */
		public DistanceValue getLineStartY() {
			return lineStartY;
		}
	}
	
	
	private PositionData chartPosition = new PositionData();
	private PositionData titlePosition = new PositionData();
	private List<CaptionPositionData> captionPositions = new ArrayList<CaptionPositionData>();
	
	
	/**
	 * Returns the position of the chart relative to the whole label.
	 * 
	 * @return the relative chart position
	 */
	public PositionData getChartPosition() {
		return chartPosition;
	}
	
	
	/**
	 * Returns the position of the chart title relative to the whole label if it is set to be visible.
	 * 
	 * @return the relative heading position
	 */
	public PositionData getTitlePosition() {
		return titlePosition;
	}


	public List<CaptionPositionData> getCaptionPositions() {
		return captionPositions;
	}
}

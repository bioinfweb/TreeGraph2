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
package info.bioinfweb.treegraph.document.format;


import info.bioinfweb.treegraph.document.Label;
import info.bioinfweb.treegraph.document.PieColorManager;

import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;



/**
 * Stores the formats of pie chart label.
 * 
 * @author Ben St&ouml;ver
 * @since 2.0.43
 */
public class PieChartLabelFormats extends GraphicalLabelFormats implements LineFormats, TextFormats {
	public static final boolean DEFAULT_SHOW_INTERNAL_LINES = true;
	public static final boolean DEFAULT_SHOW_LINES_FOR_ZERO = false;
	public static final boolean DEFAULT_SHOW_TITLE = false;
	public static final PieChartLabelCaptionContentType DEFAULT_CAPTIONS_CONTENT_TYPE = PieChartLabelCaptionContentType.NONE;
	public static final PieChartLabelCaptionLinkType DEFAULT_CAPTIONS_LINK_TYPE = PieChartLabelCaptionLinkType.STRAIGHT_LINES;
	
	
  private List<Color> pieColors = new ArrayList<Color>();
  private boolean showInternalLines = DEFAULT_SHOW_INTERNAL_LINES;
  private boolean showLinesForZero = DEFAULT_SHOW_LINES_FOR_ZERO;
  private boolean showTitle = DEFAULT_SHOW_TITLE;
  private PieChartLabelCaptionContentType captionsContentType = DEFAULT_CAPTIONS_CONTENT_TYPE;
  private PieChartLabelCaptionLinkType captionsLinkType = DEFAULT_CAPTIONS_LINK_TYPE;
	private TextFormats titleTextFormats = new ConcreteTextFormats();
	private TextFormats captionsTextFormats = new ConcreteTextFormats();


	public PieChartLabelFormats(Label owner, boolean above, int line, int linePosition) {
		super(owner, above, line, linePosition);
	}

	
	public PieChartLabelFormats(Label owner) {
		super(owner);
	}


  public boolean addPieColor(Color color) {
		return pieColors.add(color);
	}


  public void addPieColor(int index, Color color) {
		pieColors.add(index, color);
	}


	/**
	 * Returns the stored color with the specified index. If the index is out of the rage of the 
	 * currently stored field, new colors are added.
	 * 
	 * @param index the index of the color
	 * @return
	 */
	public Color getPieColor(int index) {
		if (pieColorCount() <= index) {
			PieColorManager.getInstance().addDefaultColors(this, index + 1);
		}
		return pieColors.get(index);
	}


	public Color setPieColor(int index, Color color) {
		if (pieColorCount() <= index) {
			PieColorManager.getInstance().addDefaultColors(this, index + 1);
		}
		return pieColors.set(index, color);
	}


	public int pieColorCount() {
		return pieColors.size();
	}


	public boolean isShowInternalLines() {
		return showInternalLines;
	}


	public void setShowInternalLines(boolean showInternalLines) {
		this.showInternalLines = showInternalLines;
	}


	public boolean isShowLinesForZero() {
		return showLinesForZero;
	}


	public void setShowLinesForZero(boolean showLinesForZero) {
		this.showLinesForZero = showLinesForZero;
	}


	public boolean isShowTitle() {
		return showTitle;
	}


	public void setShowTitle(boolean showTitle) {
		this.showTitle = showTitle;
	}


	public PieChartLabelCaptionContentType getCaptionsContentType() {
		return captionsContentType;
	}


	public void setCaptionsContentType(PieChartLabelCaptionContentType captionsContentType) {
		if (captionsContentType == null) {
			throw new NullPointerException("The caption content type must not be null.");
		}
		else {
			this.captionsContentType = captionsContentType;
		}
	}


	public PieChartLabelCaptionLinkType getCaptionsLinkType() {
		return captionsLinkType;
	}


	public void setCaptionsLinkType(PieChartLabelCaptionLinkType captionsLinkType) {
		if (captionsLinkType == null) {
			throw new NullPointerException("The caption link type must not be null.");
		}
		else {
			this.captionsLinkType = captionsLinkType;
		}
	}


	@Override
	public String getFontName() {
		return titleTextFormats.getFontName();
	}


	@Override
	public void setFontName(String fontName) {
		titleTextFormats.setFontName(fontName);
	}


	@Override
	public DistanceValue getTextHeight() {
		return titleTextFormats.getTextHeight();
	}


	@Override
	public float getDescent() {
		return titleTextFormats.getDescent();
	}


	@Override
	public boolean hasTextStyle(int style) {
		return titleTextFormats.hasTextStyle(style);
	}


	@Override
	public int getTextStyle() {
		return titleTextFormats.getTextStyle();
	}


	@Override
	public void setTextStyle(int style) {
		titleTextFormats.setTextStyle(style);
	}


	@Override
	public void addTextStyle(int style) {
		titleTextFormats.addTextStyle(style);
	}


	@Override
	public void removeTextStyle(int style) {
		titleTextFormats.removeTextStyle(style);
	}


	@Override
	public Font getFont(float pixelsPerMillimeter) {
		return titleTextFormats.getFont(pixelsPerMillimeter);
	}


	@Override
	public Color getTextColor() {
		return titleTextFormats.getTextColor();
	}


	@Override
	public void setTextColor(Color textColor) {
		titleTextFormats.setTextColor(textColor);
	}


	@Override
	public Locale getLocale() {
		return titleTextFormats.getLocale();
	}


	@Override
	public void setLocale(Locale locale) {
		titleTextFormats.setLocale(locale);
	}


	@Override
	public DecimalFormat getDecimalFormat() {
		return titleTextFormats.getDecimalFormat();
	}


	@Override
	public void setDecimalFormat(DecimalFormat decimalFormat, Locale locale) {
		titleTextFormats.setDecimalFormat(decimalFormat, locale);
	}


	public TextFormats getCaptionsTextFormats() {
		return captionsTextFormats;
	}


	@Override
	public void assignTextFormats(TextFormats other) {
		titleTextFormats.assignTextFormats(other);
	}
	
	
	public void assignPieChartLabelFormats(PieChartLabelFormats other) {
  	pieColors.clear();
  	pieColors.addAll(other.pieColors);
  	setShowInternalLines(other.isShowInternalLines());
  	setShowLinesForZero(other.isShowLinesForZero());
  	setShowTitle(other.isShowTitle());
  	setCaptionsContentType(other.getCaptionsContentType());
  	setCaptionsLinkType(other.getCaptionsLinkType());
		getCaptionsTextFormats().assignTextFormats(other.getCaptionsTextFormats());
  }
	
	
	public void assign(PieChartLabelFormats other) {
		assignLabelFormats(other);
		assignLineFormats(other);
		assignGraphicalLabelFormats(other);
		assignTextFormats(other);
		assignPieChartLabelFormats(other);
	}
	
	
	@Override
	public PieChartLabelFormats clone() {
		PieChartLabelFormats result = new PieChartLabelFormats(null);
		result.assign(this);
		return result;
	}
}
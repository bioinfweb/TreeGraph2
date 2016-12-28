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
package info.bioinfweb.treegraph.document.format;

import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;
import java.util.Locale;

import info.bioinfweb.treegraph.document.Label;



public class PieChartCaptionLabelFormats extends PieChartLabelFormats implements TextFormats {
	private TextFormats textFormats = new ConcreteTextFormats();
	
	
	public PieChartCaptionLabelFormats(Label owner, boolean above, int line, int linePosition) {
		super(owner, above, line, linePosition);
	}

	
	public PieChartCaptionLabelFormats(Label owner) {
		super(owner);
	}


	@Override
	public String getFontName() {
		return textFormats.getFontName();
	}


	@Override
	public void setFontName(String fontName) {
		textFormats.setFontName(fontName);
	}


	@Override
	public DistanceValue getTextHeight() {
		return textFormats.getTextHeight();
	}


	@Override
	public float getDescent() {
		return textFormats.getDescent();
	}


	@Override
	public boolean hasTextStyle(int style) {
		return textFormats.hasTextStyle(style);
	}


	@Override
	public int getTextStyle() {
		return textFormats.getTextStyle();
	}


	@Override
	public void setTextStyle(int style) {
		textFormats.setTextStyle(style);
	}


	@Override
	public void addTextStyle(int style) {
		textFormats.addTextStyle(style);
	}


	@Override
	public void removeTextStyle(int style) {
		textFormats.removeTextStyle(style);
	}


	@Override
	public Font getFont(float pixelsPerMillimeter) {
		return textFormats.getFont(pixelsPerMillimeter);
	}


	@Override
	public Color getTextColor() {
		return textFormats.getTextColor();
	}


	@Override
	public void setTextColor(Color textColor) {
		textFormats.setTextColor(textColor);
	}


	@Override
	public Locale getLocale() {
		return textFormats.getLocale();
	}


	@Override
	public void setLocale(Locale locale) {
		textFormats.setLocale(locale);
	}


	@Override
	public DecimalFormat getDecimalFormat() {
		return textFormats.getDecimalFormat();
	}


	@Override
	public void setDecimalFormat(DecimalFormat decimalFormat, Locale locale) {
		textFormats.setDecimalFormat(decimalFormat, locale);
	}


	@Override
	public void assignTextFormats(TextFormats other) {
		textFormats.assignTextFormats(other);
	}
	
	
	public void assign(PieChartCaptionLabelFormats other) {
		assignLabelFormats(other);
		assignLineFormats(other);
		assignGraphicalLabelFormats(other);
		assignPieChartLabelFormats(other);
		assignTextFormats(other);
	}
	
	
	@Override
	public PieChartCaptionLabelFormats clone() {
		PieChartCaptionLabelFormats result = new PieChartCaptionLabelFormats(null);
		result.assign(this);
		return result;
	}
}

/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2014  Ben Stöver, Kai Müller
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


import info.webinsel.util.graphics.FontCalculator;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;



public class ConcreteTextFormats implements ElementFormats, Cloneable, TextFormats {
	private String fontName = DEFAULT_FONT_NAME;
	private DistanceValue textHeight = new DistanceValue(DEFAULT_TEXT_HEIGHT_IN_MM);
	private int textStyle = PLAIN;
	private Color textColor = DEFAULT_TEXT_COLOR;
	private Locale locale;
	private DecimalFormat decimalFormat;
	
	
	public ConcreteTextFormats() {
		super();
		setDecimalFormat(new DecimalFormat(DEFAULT_DECIMAL_FORMAT_EXPR), DEFAULT_LOCALE);
	}


	/* (non-Javadoc)
	 * @see info.bioinfweb.treegraph.document.format.TextFormats#getFontName()
	 */
	public String getFontName() {
		return fontName;
	}


	/* (non-Javadoc)
	 * @see info.bioinfweb.treegraph.document.format.TextFormats#setFontName(java.lang.String)
	 */
	public void setFontName(String fontName) {
		this.fontName = fontName;
	}


	/* (non-Javadoc)
	 * @see info.bioinfweb.treegraph.document.format.TextFormats#getTextHeight()
	 */
	public DistanceValue getTextHeight() {
		return textHeight;
	}
	
	
	/* (non-Javadoc)
	 * @see info.bioinfweb.treegraph.document.format.TextFormats#getDescent()
	 */
	public float getDescent() {
		return FontCalculator.getInstance().getDescentToHeight(getFontName(), getTextStyle(), getTextHeight().getInMillimeters());
	}
	
	
	/* (non-Javadoc)
	 * @see info.bioinfweb.treegraph.document.format.TextFormats#hasTextStyle(int)
	 */
	public boolean hasTextStyle(int style) {
		return (textStyle & style) == style;
	}
	
	
	/* (non-Javadoc)
	 * @see info.bioinfweb.treegraph.document.format.TextFormats#getTextStyle()
	 */
	public int getTextStyle() {
		return textStyle;
	}
	
	
	/* (non-Javadoc)
	 * @see info.bioinfweb.treegraph.document.format.TextFormats#setTextStyle(int)
	 */
	public void setTextStyle(int style) {
		textStyle = style;
	}

	
	/* (non-Javadoc)
	 * @see info.bioinfweb.treegraph.document.format.TextFormats#addTextStyle(int)
	 */
	public void addTextStyle(int style) {
		textStyle |= style;
	}
	
	
	/* (non-Javadoc)
	 * @see info.bioinfweb.treegraph.document.format.TextFormats#removeTextStyle(int)
	 */
	public void removeTextStyle(int style) {
		textStyle &= ~style;
	}
	
	
	/* (non-Javadoc)
	 * @see info.bioinfweb.treegraph.document.format.TextFormats#getFont(float)
	 */
	public Font getFont(float pixelsPerMillimeter) {  
		//Die Angabe size von Font in Punkten ist ungleich der Angabe der Schrifthöhe in Punkten. 
		//Sie muss zur Umrechnung skaliert werden.
		
		final float factor = 0.8f;
		return new Font(getFontName(), textStyle & ~UNDERLINE, Math.round(getTextHeight().getInPixels(pixelsPerMillimeter) * factor));
	}


	/* (non-Javadoc)
	 * @see info.bioinfweb.treegraph.document.format.TextFormats#getTextColor()
	 */
	public Color getTextColor() {
		return textColor;
	}


	/* (non-Javadoc)
	 * @see info.bioinfweb.treegraph.document.format.TextFormats#setTextColor(java.awt.Color)
	 */
	public void setTextColor(Color textColor) {
		this.textColor = textColor;
	}
	
	
	/* (non-Javadoc)
	 * @see info.bioinfweb.treegraph.document.format.TextFormats#getLocale()
	 */
	public Locale getLocale() {
		return locale;
	}


	/* (non-Javadoc)
	 * @see info.bioinfweb.treegraph.document.format.TextFormats#setLocale(java.util.Locale)
	 */
	public void setLocale(Locale locale) {
		decimalFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(locale));
		this.locale = (Locale)locale.clone();
	}


	/* (non-Javadoc)
	 * @see info.bioinfweb.treegraph.document.format.TextFormats#getDecimalFormat()
	 */
	public DecimalFormat getDecimalFormat() {
		return decimalFormat;
	}


	/* (non-Javadoc)
	 * @see info.bioinfweb.treegraph.document.format.TextFormats#setDecimalFormat(java.text.DecimalFormat, java.util.Locale)
	 */
	public void setDecimalFormat(DecimalFormat decimalFormat, Locale locale) {
		if ((decimalFormat != null) && (locale != null)) {
			this.decimalFormat = (DecimalFormat)decimalFormat.clone();
			setLocale(locale);
		}
		else {
			throw new IllegalArgumentException("The decimal format or the locale cannot be null.");
		}
	}


	/* (non-Javadoc)
	 * @see info.bioinfweb.treegraph.document.format.TextFormats#assignTextFormats(info.bioinfweb.treegraph.document.format.TextFormats)
	 */
	public void assignTextFormats(TextFormats other) {
		setFontName(other.getFontName());
		getTextHeight().assign(other.getTextHeight());
		setTextStyle(other.getTextStyle());
		setTextColor(other.getTextColor());
		setDecimalFormat(other.getDecimalFormat(), other.getLocale());
	}


	@Override
	public ConcreteTextFormats clone() {
		ConcreteTextFormats result = new ConcreteTextFormats();
		result.assignTextFormats(this);
		return result;
	}
}
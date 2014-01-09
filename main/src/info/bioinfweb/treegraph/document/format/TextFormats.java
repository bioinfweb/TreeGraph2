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


import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;
import java.util.Locale;



public interface TextFormats extends ElementFormats {
	public static final String DEFAULT_FONT_NAME = "Arial"; //TODO sinnvollen Startwert wählen
	public static final float DEFAULT_TEXT_HEIGHT_IN_MM = 6;
	public static final Color DEFAULT_TEXT_COLOR = Color.BLACK;
	public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
	public static final String DEFAULT_DECIMAL_FORMAT_EXPR = "0.0#####";
	public static final int PLAIN = 0;
	public static final int BOLD = 1;
	public static final int ITALIC = 2;
	public static final int UNDERLINE = 4;

	
	public String getFontName();

	public void setFontName(String fontName);

	public DistanceValue getTextHeight();

	public float getDescent();

	public boolean hasTextStyle(int style);

	public int getTextStyle();

	public void setTextStyle(int style);

	public void addTextStyle(int style);

	public void removeTextStyle(int style);

	public Font getFont(float pixelsPerMillimeter);

	public Color getTextColor();

	public void setTextColor(Color textColor);

	/**
	 * Return the local object used in the decimal formats. 
	 * @return
	 */
	public Locale getLocale();

	/**
	 * Sets the local object which is used to
	 * @param locale
	 */
	public void setLocale(Locale locale);

	public DecimalFormat getDecimalFormat();

	/**
	 * Sets a new decimal format object. The provided local object will be used to determine
	 * the decimal format symbols. (This means that the decimal formats object may be 
	 * changed after being passed here.)
	 * @param decimalFormat - the new decimal format object
	 * @param locale - the locale object which was used to create the new decimal formats
	 * @throws IllegalArgumentException if one of the parameters is <code>null</code>
	 */
	public void setDecimalFormat(DecimalFormat decimalFormat, Locale locale);

	public void assignTextFormats(TextFormats other);
	
	public TextFormats clone();
}
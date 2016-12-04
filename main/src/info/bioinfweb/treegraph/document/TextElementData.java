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
package info.bioinfweb.treegraph.document;


import java.text.DecimalFormat;



/**
 * This class is used to store data of tree elements which can either be a string or a
 * decimal value.
 * 
 * @author Ben St&ouml;ver
 */
public class TextElementData implements Cloneable, Comparable<TextElementData> {
  private String text = null;
  private double decimal = Double.NaN;
  
  
	public TextElementData() {
		super();
	}


	public TextElementData(String string) {
		super();
		setText(string);
	}


	public TextElementData(double decimal) {
		super();
		setDecimal(decimal);
	}


	/**
	 * Return the stored decimal value or <code>Double.NaN</code> is no decimal is stored.
	 * @return
	 */
	public double getDecimal() {
		return decimal;
	}
	
	
	public void setDecimal(double doubleValue) {
		this.decimal = doubleValue;
		text = null;
	}
	
	
	/**
	 * Returns the stored string value or <code>null</code> is no string is stored.
	 * @return
	 */
	public String getText() {
		return text;
	}
	
	
	public void setText(String stringValue) {
		this.text = stringValue;
		decimal = Double.NaN;
	}
	
	
	/**
	 * Clears this object. If a string or a decimal value are stored they will be deleted.
	 * @see isEmpty()
	 */
	public void clear() {
		text = null;
		decimal = Double.NaN;
	}
	
	
	/**
	 * Return whether this object contains a string value. Not that the return of 
	 * <code>false</code> does not necessarily mean that a decimal value is contained. This
	 * object could also be empty. 
	 * @return <code>true</code> if the stored value is a string
	 * @see isDecimal()
	 * @see isEmpty()
	 */
	public boolean isString() {
		return text != null;
	}
	
	
	/**
	 * Return whether this object contains a decimal value. Not that the return of 
	 * <code>false</code> does not necessarily mean that a string value is contained. This
	 * object could also be empty. 
	 * @return <code>true</code> if the stored value is a decimnal
	 * @see isString()
	 * @see isEmpty()
	 */
	public boolean isDecimal() {
		return !Double.isNaN(decimal);
	}
	
	
	/**
	 * Return whether this object is empty. It that case is neither contains a string nor a
	 * decimal. 
	 * @return <code>true</code> if there is no value stored
	 * @see isDecimal()
	 * @see isString()
	 */
	public boolean isEmpty() {
		return !isDecimal() && (!isString());
	}
	
	
	/**
	 * Return the contained string or the contained formated decimal value or an empty 
	 * string is the object contains no data. 
	 * @param format - the format-object to format a possibly contained decimal value
	 * @return the (formatted) string representation of the stored value
	 */
	public String formatValue(DecimalFormat format) {
		if (isDecimal()) {
			return format.format(getDecimal());
		}
		else if (isString()) {
			return getText();
		}
		else {
			return ""; 
		}
	}
	
	
	/**
	 * Returns the formatted value of the given text element.
	 * @param element - the element to be formatted
	 * @return
	 */
	public static String formatTextElement(TextElement element) {
		return element.getData().formatValue(element.getFormats().getDecimalFormat());
	}


	/**
	 * Returns either the stored string, the decimal value as a sting or an empty string 
	 * if the object is empty.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (isDecimal()) {
			return "" + getDecimal();
		}
		else if (isString()) {
			return getText();
		}
		else {
			return "";
		}
	}


	public void assign(TextElementData other) {
		if (other.isDecimal()) {
			setDecimal(other.getDecimal());
		}
		else if (other.isString()) {
			setText(other.getText());
		}
		else {
			clear();
		}
	}
	
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof TextElementData) {
			TextElementData otherData = (TextElementData)other;
			if (isDecimal()) {
				return (otherData.getDecimal() == getDecimal());
			}
			else if (isString()) {
				return text.equals(otherData.getText());
			}
			else {
				return otherData.isEmpty();
			}
		}
		return false;
	}


	@Override
	public TextElementData clone() {
		TextElementData result = new TextElementData();
		result.assign(this);
		return result;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(decimal);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		return result;
	}


	/**
	 * Sorts in the following order: Empty objects, textual values according to their order, numerical values 
	 * according to their order 
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
  public int compareTo(TextElementData other) {
		if (isDecimal()) {
			if (other.isDecimal()) {
				return Double.compare(getDecimal(), other.getDecimal());
			}
			else {  // other is textual or empty
				return 1;
			}
		}
		else if (isString()) {
			if (other.isDecimal()) {
				return -1;
			}
			else if (other.isString()) {
				return getText().compareTo(other.getText());
			}
			else {  // other is empty
				return 1;
			}
		}
		else {  // this object is empty
			if (other.isEmpty()) {
				return 0;
			}
			else {
				return -1;
			}
		}
  }
}
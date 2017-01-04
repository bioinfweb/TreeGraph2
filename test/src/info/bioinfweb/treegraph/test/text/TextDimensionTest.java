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
package info.bioinfweb.treegraph.test.text;


import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import info.bioinfweb.commons.graphics.FontCalculator;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElement;
import info.bioinfweb.treegraph.document.format.DistanceDimension;
import info.bioinfweb.treegraph.graphics.positionpaint.PositionPaintUtils;



public class TextDimensionTest {
	private static void testFont(String fontName, float pixelsPerMillimeter) {
	  TextElement textElement = new Node();
	  textElement.getData().setText("ABC dkjelnwmd");
	  textElement.getFormats().getTextHeight().setInMillimeters(10);
	  textElement.getFormats().setFontName(fontName);

	  // Calculates width:
	  DistanceDimension dim = PositionPaintUtils.calculateTextDimension(textElement);
	  
	  // Painted width:
	  Graphics2D g = (Graphics2D)new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR).getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
	  float paintWidth = PositionPaintUtils.paintText(g, 
	  				pixelsPerMillimeter, textElement.getData().getText(), textElement.getFormats(), 1, 20);

	  // Width from scaled height:
	  float widthFromScaledHeight = FontCalculator.getInstance().getWidthToHeigth(textElement.getFormats().getFontName(), 
	  				textElement.getFormats().getTextStyle(), textElement.getData().getText(), 
	  				textElement.getFormats().getTextHeight().getInPixels(pixelsPerMillimeter) * 0.8f);
	  
	  System.out.println(fontName + " (" + pixelsPerMillimeter + "):  " + dim.getWidth().getInPixels(pixelsPerMillimeter) + " " + paintWidth + " " + widthFromScaledHeight);
	  
	  Font font = textElement.getFormats().getFont(pixelsPerMillimeter);
	  printFontInfo("Text", font, textElement, g);
	  printFontInfo("Calculated", font.deriveFont(512f), textElement, g);
	  
	  // Warum weichen Arial und Arial Black in verschiedene Richtungen ab?
	  // Grundsätzlich ist die Frage, warum es überhaupt eine Abweichung gibt? Gäbe es diese nicht, wäre das erste vermutlich auch kein Problem.
	  // Durch Verändern des Faktors 0.8 kann man die Differenz für eine Schriftart verkleinern, allerdings nicht beliebig, da dann ein Diskretisierungeffekt einsetzt (zwischen 0.871 und 0.872 gibt es einen Sprung in der resultierenden Breite)
	  // => Es muss irgendwo noch eine Diskretisierung durchgeführt werden. Wo? (Im eigenen Code oder funktioniert das setzen von Dezimalschrifthöhen nicht?)
	}
	
	
	private static void printFontInfo(String name, Font font, TextElement textElement, Graphics2D g) {
		Rectangle2D r = font.getStringBounds(textElement.getData().getText(), g.getFontRenderContext());
		double ratio = r.getWidth() / r.getHeight();
	  System.out.println(name + ": " + font.getSize2D() + " " + r.getHeight() + " " + r.getWidth() + " " + ratio);
	  double calcWidthSize = font.getSize2D() * ratio;
	  double calcWidthHeight = r.getHeight() * ratio;
	  System.out.println("  " + calcWidthSize + " " + calcWidthHeight);
	}
	
	
	public static void main(String[] args) {
		testFont("Arial", 1f);
		System.out.println();
		testFont("Arial Black", 1f);
		//testFont("Courier New", 1f);
  }
}

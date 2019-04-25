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
package info.bioinfweb.treegraph.test.text;


import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;



public class FontMetricsTest {
	public static void main(String[] args) {
//		Font font = new Font("Arial", Font.PLAIN, 12);
//		FontMetrics fm = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR).getGraphics().getFontMetrics(font);
//		FontRenderContext frc = new FontRenderContext(null, true, true);
		
//		System.out.println(fm.getHeight() + " " + fm.getAscent());
//		System.out.println(font.getStringBounds("A", frc).getHeight());
//		String s = "ABC dfqwoeön xcdewonö";
//		System.out.println(fm.stringWidth(s) + " " + font.getStringBounds(s, frc).getWidth());
		

//		Graphics2D g = (Graphics2D)new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR).getGraphics();
//		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
//		
//		double interval = 1.27;
//		for (double factor = interval; factor <= 4 * interval; factor += interval) {
//			Font font = new Font("Arial", Font.PLAIN, (int)Math.round(512 * factor));
//			FontMetrics fm = g.getFontMetrics(font);
//			double width = fm.getStringBounds("ABC fwoicn dewion ewdw", g).getWidth();
//	    System.out.println((12 * factor) + " " + width + " " + (width / (double)factor));
//			width = font.getStringBounds("ABC fwoicn dewion ewdw", new FontRenderContext(null, true, true)).getWidth();
//	    System.out.println((12 * factor) + " " + width + " " + (width / (double)factor));
//    }
//		
//		System.out.println();
		
//		Font font = new Font("Arial", Font.PLAIN, 512);
//		System.out.println(font.getStringBounds("Ö", new FontRenderContext(null, true, true)).getHeight());
//		System.out.println(font.getStringBounds(".", new FontRenderContext(null, true, true)).getHeight());

		
		Font font = new Font("Arial", Font.PLAIN, 12);
		System.out.println(font.getStringBounds("ABC fwoicn dewion ewdw", new FontRenderContext(null, true, true)));
		System.out.println(font.deriveFont(12.4f).getStringBounds("ABC fwoicn dewion ewdw", new FontRenderContext(null, true, true)));
	}
}

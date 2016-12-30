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
import java.awt.FontMetrics;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;



public class FontMetricsTest {
	public static void main(String[] args) {
		Font font = new Font("Arial", Font.PLAIN, 12);
		FontMetrics fm = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR).getGraphics().getFontMetrics(font);
		FontRenderContext frc = new FontRenderContext(null, true, true);
		
		System.out.println(fm.getHeight() + " " + fm.getAscent());
		System.out.println(font.getStringBounds("A", frc).getHeight());
		String s = "ABC dfqwoeön xcdewonö";
		System.out.println(fm.stringWidth(s) + " " + font.getStringBounds(s, frc).getWidth());
	}
}

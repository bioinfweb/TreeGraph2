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


import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;



public class TextSizeTest {
	private static final float PAINT_X = 3f;
	private static final String TEXT = "ABC Ögikl";
	private static final String FONT_NAME = "Arial";
	private static final int FONT_STYLE = Font.PLAIN;
	
	
	private JFrame frame;
	private JPanel outputPanel;

	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TextSizeTest window = new TextSizeTest();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	/**
	 * Create the application.
	 */
	public TextSizeTest() {
		initialize();
	}
	

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.getContentPane().add(getOutputPanel(), BorderLayout.CENTER);
	}
	
	
	private float paintText(Graphics2D g, float y, int height) {
		
//		FontRenderContext frt = new FontRenderContext(null, true, true);
//		
//		Font font = new Font(FONT_NAME, FONT_STYLE, height);
//		g.setFont(font);
//		FontMetrics fm = g.getFontMetrics();
//		fm.getH
//		g.drawString(TEXT, PAINT_X, y);
		return 0;
	}
	
	
	public JPanel getOutputPanel() {
		if (outputPanel == null) {
			outputPanel = new JPanel() {
				@Override
				public void paint(Graphics g1) {
					super.paint(g1);
					
					Graphics2D g = (Graphics2D)g1;
					g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
					g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
					
					Graphics2D scaledG = (Graphics2D)g.create();

					g.setFont(new Font(FONT_NAME, FONT_STYLE, 12 * 4));
					g.drawString(TEXT, 4, 75);
					
					scaledG.scale(4, 4);
					Font font = new Font(FONT_NAME, FONT_STYLE, 12);
					scaledG.setFont(font);
					scaledG.drawString(TEXT, 1, 150 / 4);
					
					GlyphVector gv = font.createGlyphVector(g.getFontRenderContext(), TEXT);
					scaledG.drawGlyphVector(gv, 1, 225 / 4);
					
					System.out.println("g: " + g.getFontMetrics().getStringBounds(TEXT, g).getWidth());
					double width = scaledG.getFontMetrics().getStringBounds(TEXT, scaledG).getWidth();
					System.out.println("scaledG: " + width + " " + (width * 4));
					
					g.setFont(font);
					width = g.getFontMetrics().getStringBounds(TEXT, g).getWidth();
					System.out.println("g small: " + width + " " + (width * 4));
//					Font font = new Font(FONT_NAME, FONT_STYLE, 12);
//					font.createGlyphVector(g.getFontRenderContext(), TEXT);
				}
			};
		}
		return outputPanel;
	}
}

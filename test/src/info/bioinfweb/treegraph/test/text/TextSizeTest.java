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

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.font.FontRenderContext;



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
					g.setFont(new Font(FONT_NAME, FONT_STYLE, 12));
					g.drawString("ABC Ögikl", 3, 25);
				}
			};
		}
		return outputPanel;
	}
}

/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2011, 2013-2017  Ben Stöver, Sarah Wiechers, Kai Müller
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
package info.bioinfweb.treegraph.document.io.jphyloio;


import info.bioinfweb.jphyloio.formats.xtg.XTGConstants;
import info.bioinfweb.jphyloio.objecttranslation.ObjectTranslatorFactory;
import info.bioinfweb.jphyloio.objecttranslation.implementations.EnumTranslator;
import info.bioinfweb.jphyloio.objecttranslation.implementations.HexadecimalColorTranslator;
import info.bioinfweb.treegraph.document.format.LegendStyle;
import info.bioinfweb.treegraph.document.format.PieChartLabelCaptionContentType;
import info.bioinfweb.treegraph.document.format.PieChartLabelCaptionLinkType;
import info.bioinfweb.treegraph.document.format.ScaleBarAlignment;
import info.bioinfweb.treegraph.document.format.TextOrientation;



/**
 * Tool class that provides additional functionality to process data from <i>TreeGraph 2</i> with <i>JPhyloIO</i>.
 * <p>
 * This class is used internally, but can also be used by third party developers working with <i>TreeGraph 2</i> 
 * and <i>JPhyloIO</i>
 * 
 * @author Ben St&ouml;ver
 * @since 2.15.0
 */
public class JPhyloIOTools {
	/**
	 * Adds object translators between all <i>TreeGraph 2</i> specific data types and their respective <i>Java</i> 
	 * classes implemented in <i>TreeGraph 2</i> to an object translator factory.
	 * 
	 * @param factory the <i>JPhyloIO</i> object translator factory to add data type support to
	 * @param asDefault Determines whether the added translators shall become the default translators for their data type, 
	 *        if another default instance is already registered. (If {@code true} is specified, previous defaults will be
	 *        overwritten. If {@code false} is specified, previous defaults will be maintained. In all cases previous entries
	 *        will remain in the factory, if they have a different object type or will be completely overwritten if they have 
	 *        the same.)  
	 */
	public static void addTreeGraphObjectTranslators(ObjectTranslatorFactory factory, boolean asDefault) {
		factory.addTranslator(new HexadecimalColorTranslator(), asDefault, XTGConstants.DATA_TYPE_COLOR);  //TODO Data type constants currently come from JPhyloIO. In the future both constant interfaces should be merged.
		factory.addTranslator(new EnumTranslator<TextOrientation>(TextOrientation.class), asDefault, 
				XTGConstants.DATA_TYPE_TEXT_ORIENTATION);
		factory.addTranslator(new EnumTranslator<LegendStyle>(LegendStyle.class), asDefault, 
				XTGConstants.DATA_TYPE_LEGEND_STYLE);
		factory.addTranslator(new EnumTranslator<ScaleBarAlignment>(ScaleBarAlignment.class), asDefault, 
				XTGConstants.DATA_TYPE_SCALE_BAR_ALIGNMENT);
		factory.addTranslator(new ScaleValueTranslator(), asDefault, XTGConstants.DATA_TYPE_SCALE_VALUE);
		factory.addTranslator(new EnumTranslator<PieChartLabelCaptionContentType>(PieChartLabelCaptionContentType.class), 
				asDefault, XTGConstants.DATA_TYPE_PIE_CHART_LABEL_CAPTION_TYPE);
		factory.addTranslator(new EnumTranslator<PieChartLabelCaptionLinkType>(PieChartLabelCaptionLinkType.class), 
				asDefault, XTGConstants.DATA_TYPE_PIE_CHART_LABEL_CAPTION_LINK_TYPE);
	}
}

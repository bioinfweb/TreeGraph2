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


import java.io.IOException;

import javax.xml.namespace.QName;

import info.bioinfweb.commons.IntegerIDManager;
import info.bioinfweb.commons.io.W3CXSConstants;
import info.bioinfweb.jphyloio.ReadWriteConstants;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.dataadapters.JPhyloIOEventReceiver;
import info.bioinfweb.jphyloio.dataadapters.ObjectListDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.TreeNetworkDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.implementations.NoSetsTreeNetworkDataAdapter;
import info.bioinfweb.jphyloio.events.ConcreteJPhyloIOEvent;
import info.bioinfweb.jphyloio.events.EdgeEvent;
import info.bioinfweb.jphyloio.events.LabeledIDEvent;
import info.bioinfweb.jphyloio.events.NodeEvent;
import info.bioinfweb.jphyloio.events.meta.ResourceMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.URIOrStringIdentifier;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.utils.JPhyloIOWritingUtils;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.Legend;
import info.bioinfweb.treegraph.document.Legends;
import info.bioinfweb.treegraph.document.LineElement;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElement;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.format.Margin;
import info.bioinfweb.treegraph.document.io.xtg.XTGConstants;



public class TreeDataAdapter extends NoSetsTreeNetworkDataAdapter implements TreeNetworkDataAdapter, XTGConstants, ReadWriteConstants {	
	private Tree treeModel;
	private ObjectListDataAdapter<NodeEvent> nodeList;
	private ObjectListDataAdapter<EdgeEvent> edgeList;
	
	
	public TreeDataAdapter(Tree treeModel) {		
		nodeList = new NodeListDataAdapter(treeModel);
		edgeList = new EdgeListDataAdapter(treeModel);
		this.treeModel = treeModel;
	}
	

	/**
	 * Creates ID to add to unique name of meta event.
	 * 
	 * @param id
	 * @param idManager
	 * @return
	 */
	public static String createMetaID(String id, IntegerIDManager idManager) {		
		return id + "_" + DEFAULT_META_ID_PREFIX + idManager.createNewID();
	}
	
	
	public static void writeMargin(JPhyloIOEventReceiver receiver, String id, IntegerIDManager idManager, Margin margin,
			QName predicateMarginLeft, QName predicatemarginTop, QName predicateMarginRight, QName predicateMarginBottom) throws IOException {
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
				predicateMarginLeft, W3CXSConstants.DATA_TYPE_FLOAT, margin.getLeft().getInMillimeters(), null);
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
				predicatemarginTop, W3CXSConstants.DATA_TYPE_FLOAT, margin.getTop().getInMillimeters(), null);
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
				predicateMarginRight, W3CXSConstants.DATA_TYPE_FLOAT, margin.getRight().getInMillimeters(), null);
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
				predicateMarginBottom, W3CXSConstants.DATA_TYPE_FLOAT, margin.getBottom().getInMillimeters(), null);
	}
	
	
	public static void writeLineAtrributes(JPhyloIOEventReceiver receiver, String id, LineElement element, IntegerIDManager idManager,
			QName predicateLineColor, QName predicateLineWidth )
			throws IOException {
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
			predicateLineColor, info.bioinfweb.jphyloio.formats.xtg.XTGConstants.DATA_TYPE_COLOR, 
			element.getFormats().getLineColor(), null);
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
			predicateLineWidth, W3CXSConstants.DATA_TYPE_FLOAT, 
			element.getFormats().getLineWidth().getInMillimeters(), null);
	}
	
	
	public static void writeTextAttributes(JPhyloIOEventReceiver receiver, String id, IntegerIDManager idManager, TextElement textElement, 
			QName predicateColor, QName predicateHeight, QName predicateStyle, QName predicateFontFamily, 
			QName predicateDecimal, QName predicateLang, QName predicateCountry, QName predicateVariant)
			throws IOException {
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
				predicateColor, info.bioinfweb.jphyloio.formats.xtg.XTGConstants.DATA_TYPE_COLOR, 
				textElement.getFormats().getTextColor(), null);
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
				predicateHeight, W3CXSConstants.DATA_TYPE_FLOAT, 
				textElement.getFormats().getTextHeight().getInMillimeters(), null);
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
				predicateStyle, W3CXSConstants.DATA_TYPE_INT, 
				textElement.getFormats().getTextStyle(), null);
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
				predicateFontFamily, W3CXSConstants.DATA_TYPE_STRING, 
				textElement.getFormats().getFontName(), null);
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
				predicateDecimal, W3CXSConstants.DATA_TYPE_DOUBLE, 
				textElement.getData().getDecimal(), null);
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
				predicateLang, W3CXSConstants.DATA_TYPE_STRING, 
				textElement.getFormats().getLocale().getLanguage(), null);
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
				predicateCountry, W3CXSConstants.DATA_TYPE_STRING, 
				textElement.getFormats().getLocale().getCountry(), null);
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
				predicateVariant, W3CXSConstants.DATA_TYPE_STRING, 
				textElement.getFormats().getLocale().getVariant(), null);
	}
	
	
	@Override
	public void writeMetadata(ReadWriteParameterMap parameters, JPhyloIOEventReceiver receiver) throws IOException {
		IntegerIDManager idManager = new IntegerIDManager();

		receiver.add(new ResourceMetadataEvent(TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null, new URIOrStringIdentifier(null, info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_GLOBAL_FORMATS), null, null));		
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_GLOBAL_FORMATS_ATTR_BG_COLOR, info.bioinfweb.jphyloio.formats.xtg.XTGConstants.DATA_TYPE_COLOR, 
				treeModel.getFormats().getBackgroundColor(), null);
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_GLOBAL_FORMATS_ATTR_BRANCH_LENGTH_SCALE, W3CXSConstants.DATA_TYPE_DOUBLE, 
				treeModel.getFormats().getBranchLengthScale().getInMillimeters(), null);
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_GLOBAL_FORMATS_ATTR_SHOW_SCALE_BAR, W3CXSConstants.DATA_TYPE_BOOLEAN, 
				treeModel.getFormats().getShowScaleBar(), null);
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_GLOBAL_FORMATS_ATTR_ALIGN_TO_SUBTREE, W3CXSConstants.DATA_TYPE_BOOLEAN, 
				treeModel.getFormats().getAlignLegendsToSubtree(), null);
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_GLOBAL_FORMATS_ATTR_POSITION_LABELS_TO_LEFT, W3CXSConstants.DATA_TYPE_BOOLEAN, 
				treeModel.getFormats().getPositionLabelsToLeft(), null);
		
		receiver.add(new ResourceMetadataEvent(TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null, new URIOrStringIdentifier(null, info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_DOCUMENT_MARGIN), null, null));		
		writeMargin(receiver, DEFAULT_TREE_ID_PREFIX, idManager, treeModel.getFormats().getDocumentMargin(), 
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_DOCUMENT_MARGIN_ATTR_LEFT,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_DOCUMENT_MARGIN_ATTR_TOP,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_DOCUMENT_MARGIN_ATTR_RIGHT,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_DOCUMENT_MARGIN_ATTR_BOTTOM);		
		receiver.add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.RESOURCE_META));
		
		receiver.add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.RESOURCE_META));
				

//		receiver.add(new ResourceMetadataEvent(TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null, new URIOrStringIdentifier(null, info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_NODE_BRANCH_DATA_ADAPTERS), null, null));	
//		
//		receiver.add(new ResourceMetadataEvent(TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null, new URIOrStringIdentifier(null, info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_NODE_BRANCH_DATA_ADAPTERS_ADAPTER), null, null));		
//		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null,
//				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_NODE_BRANCH_DATA_ADAPTERS_ADAPTER_ATTR_ID, W3CXSConstants.DATA_TYPE_STRING, , null);		
//			JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null,
//					info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_NODE_BRANCH_DATA_ADAPTERS_ADAPTER_ATTR_NAME, W3CXSConstants.DATA_TYPE_STRING, , null);
//			JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null,
//					info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_NODE_BRANCH_DATA_ADAPTERS_ADAPTER_ATTR_PURPOSE, W3CXSConstants.DATA_TYPE_STRING, , null);
//		receiver.add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.RESOURCE_META));
//		
//		receiver.add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.RESOURCE_META));
		
		
		if (!treeModel.getLegends().isEmpty()) {
			Legends legends = treeModel.getLegends();
			for (int index = 0; index < legends.size(); index++) {
				Legend legend = legends.get(index);
				
				TextElement textElement = ((TextElement)legend);
				
				receiver.add(new ResourceMetadataEvent(TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null, new URIOrStringIdentifier(null, info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LEGEND), null, null));
				JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null,
						info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LEGEND_ATTR_TEXT, W3CXSConstants.DATA_TYPE_STRING, 
						textElement.getData(), null);
				
				writeTextAttributes(receiver, DEFAULT_TREE_ID_PREFIX, idManager, textElement, 
						info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LEGEND_ATTR_TEXT_COLOR, 
						info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LEGEND_ATTR_TEXT_HEIGHT, 
						info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LEGEND_ATTR_TEXT_STYLE, 
						info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LEGEND_ATTR_FONT_FAMILY, 
						info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LEGEND_ATTR_DECIMAL_FORMAT, 
						info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LEGEND_ATTR_LOCALE_LANG, 
						info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LEGEND_ATTR_LOCALE_COUNTRY, 
						info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LEGEND_ATTR_LOCALE_VARIANT);						


				JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null,
						info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LEGEND_ATTR_ANCHOR_0, W3CXSConstants.DATA_TYPE_STRING, 
						legend.getFormats().getAnchor(0).getUniqueName(), null);
				if (!legend.getFormats().hasOneAnchor()) {
					JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null,
							info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LEGEND_ATTR_ANCHOR_1, W3CXSConstants.DATA_TYPE_STRING, 
							legend.getFormats().getAnchor(1).getUniqueName(), null);
				}
				JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null,
						info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LEGEND_ATTR_LEGEND_POSITION, W3CXSConstants.DATA_TYPE_INT, 
						legend.getFormats().getPosition(), null);
				JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null,
						info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LEGEND_ATTR_MIN_TREE_DISTANCE, W3CXSConstants.DATA_TYPE_FLOAT, 
						legend.getFormats().getMinTreeDistance().getInMillimeters(), null);
				JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null,
						info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LEGEND_ATTR_LEGEND_SPACING, W3CXSConstants.DATA_TYPE_FLOAT, 
						legend.getFormats().getSpacing().getInMillimeters(), null);
				JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null,
						info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LEGEND_ATTR_LEGEND_STYLE, W3CXSConstants.DATA_TYPE_STRING, 
						legend.getFormats().getLegendStyle().toString(), null);
				JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null,
						info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LEGEND_ATTR_ORIENTATION, W3CXSConstants.DATA_TYPE_STRING, 
						legend.getFormats().getOrientation().toString(), null);
				JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null,
						info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LEGEND_ATTR_EDGE_RADIUS, W3CXSConstants.DATA_TYPE_FLOAT, 
						legend.getFormats().getCornerRadius().getInMillimeters(), null);
				
				writeLineAtrributes(receiver, DEFAULT_TREE_ID_PREFIX, legend, idManager, 
						info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LEGEND_ATTR_LINE_COLOR,
						info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LEGEND_ATTR_LINE_WIDTH);
				
				receiver.add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.RESOURCE_META));
				
				receiver.add(new ResourceMetadataEvent(TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null, new URIOrStringIdentifier(null, info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LEGEND_MARGIN), null, null));
				writeMargin(receiver, DEFAULT_TREE_ID_PREFIX, idManager, legend.getFormats().getMargin(),
						info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LEGEND_MARGIN_ATTR_LEFT,
						info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LEGEND_MARGIN_ATTR_TOP,
						info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LEGEND_MARGIN_ATTR_RIGHT,
						info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LEGEND_MARGIN_ATTR_BOTTOM);

			}
		}
		
		receiver.add(new ResourceMetadataEvent(TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null, new URIOrStringIdentifier(null, info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_SCALE_BAR), null, null));
		writeTextAttributes(receiver, DEFAULT_TREE_ID_PREFIX, idManager, treeModel.getScaleBar(), 
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_SCALE_BAR_ATTR_TEXT_COLOR,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_SCALE_BAR_ATTR_TEXT_HEIGHT,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_SCALE_BAR_ATTR_TEXT_STYLE,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_SCALE_BAR_ATTR_FONT_FAMILY,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_SCALE_BAR_ATTR_DECIMAL_FORMAT,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_SCALE_BAR_ATTR_LOCALE_LANG,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_SCALE_BAR_ATTR_LOCALE_COUNTRY,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_SCALE_BAR_ATTR_LOCALE_VARIANT);
		
		writeLineAtrributes(receiver, DEFAULT_TREE_ID_PREFIX, treeModel.getScaleBar(), idManager, 
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_SCALE_BAR_ATTR_LINE_COLOR,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_SCALE_BAR_ATTR_LINE_WIDTH);
		
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_SCALE_BAR_ATTR_HEIGHT, W3CXSConstants.DATA_TYPE_FLOAT, 
				treeModel.getScaleBar().getFormats().getHeight().getInMillimeters(), null);
//		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null,
//				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_SCALE_BAR_ATTR_WIDTH, W3CXSConstants.DATA_TYPE_FLOAT, 
//				treeModel.getScaleBar().getFormats().getWidth().getInMillimeters(branchLengthScale), null);
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_SCALE_BAR_ATTR_ALIGN, W3CXSConstants.DATA_TYPE_STRING, 
				treeModel.getScaleBar().getFormats().getAlignment().toString(), null);
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_SCALE_BAR_ATTR_TREE_DISTANCE, W3CXSConstants.DATA_TYPE_FLOAT, 
				treeModel.getScaleBar().getFormats().getTreeDistance().getInMillimeters(), null);
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_SCALE_BAR_ATTR_SMALL_INTERVAL, W3CXSConstants.DATA_TYPE_FLOAT, 
				treeModel.getScaleBar().getFormats().getSmallInterval(), null);
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_SCALE_BAR_ATTR_LONG_INTERVAL, W3CXSConstants.DATA_TYPE_INT, 
				treeModel.getScaleBar().getFormats().getLongInterval(), null);
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_SCALE_BAR_ATTR_START_LEFT, W3CXSConstants.DATA_TYPE_BOOLEAN, 
				treeModel.getScaleBar().getFormats().isStartLeft(), null);
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(DEFAULT_TREE_ID_PREFIX, idManager), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_SCALE_BAR_ATTR_INCREASING, W3CXSConstants.DATA_TYPE_BOOLEAN, 
				treeModel.getScaleBar().getFormats().isIncreasing(), null);

	}
	
		
	
	@Override
	public LabeledIDEvent getStartEvent(ReadWriteParameterMap parameters) {
		return new LabeledIDEvent(EventContentType.TREE, DEFAULT_TREE_ID_PREFIX, null);
	}

	
	@Override
	public ObjectListDataAdapter<EdgeEvent> getEdges(ReadWriteParameterMap parameters) {
		return edgeList;
	}

	
	@Override
	public ObjectListDataAdapter<NodeEvent> getNodes(ReadWriteParameterMap parameters) {
		return nodeList;
	}

	
	@Override
	public boolean isTree(ReadWriteParameterMap parameters) {
		return true;
	}	
}

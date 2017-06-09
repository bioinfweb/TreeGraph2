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
import info.bioinfweb.jphyloio.ReadWriteParameterNames;
import info.bioinfweb.jphyloio.dataadapters.JPhyloIOEventReceiver;
import info.bioinfweb.jphyloio.events.ConcreteJPhyloIOEvent;
import info.bioinfweb.jphyloio.events.EdgeEvent;
import info.bioinfweb.jphyloio.events.meta.ResourceMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.URIOrStringIdentifier;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.utils.JPhyloIOWritingUtils;
import info.bioinfweb.treegraph.document.GraphicalLabel;
import info.bioinfweb.treegraph.document.IconLabel;
import info.bioinfweb.treegraph.document.Label;
import info.bioinfweb.treegraph.document.Labels;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.PieChartLabel;
import info.bioinfweb.treegraph.document.TextElement;
import info.bioinfweb.treegraph.document.TextLabel;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.io.xtg.XTGConstants;



public class EdgeListDataAdapter extends AbstractNodeEdgeListDataAdapter<EdgeEvent> implements ReadWriteConstants, ReadWriteParameterNames, XTGConstants  {
	public EdgeListDataAdapter(Tree treeModel) {
		super(DEFAULT_EDGE_ID_PREFIX, treeModel);
	}


	@Override
	protected EdgeEvent createEvent(String id, Node node) {
		String sourceID = null;
		if (node.getParent() != null) {
			sourceID = DEFAULT_NODE_ID_PREFIX + node.getParent().getUniqueName();
		}
		return new EdgeEvent(id, null, sourceID, DEFAULT_NODE_ID_PREFIX + node.getUniqueName(), Double.NaN);
	}	
		
	
	private void writeLabelDimensions(JPhyloIOEventReceiver receiver, String id, IntegerIDManager idManager, GraphicalLabel label,
			QName predicateWidth, QName predicateHeight)
			throws IOException {
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
				predicateWidth, W3CXSConstants.DATA_TYPE_FLOAT, 
				label.getFormats().getWidth().getInMillimeters(), null);
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
				predicateHeight, W3CXSConstants.DATA_TYPE_FLOAT, 
				label.getFormats().getHeight().getInMillimeters(), null);
	}
	
	
	private void writeLabelAttributes(JPhyloIOEventReceiver receiver, String id, IntegerIDManager idManager, Label label)
			throws IOException {
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_COLUMN_ID, W3CXSConstants.DATA_TYPE_STRING, label.getID(), null);	
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LABEL_ATTR_ABOVE, W3CXSConstants.DATA_TYPE_BOOLEAN, label.isAbove(), null);	
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LABEL_ATTR_LINE_NO, W3CXSConstants.DATA_TYPE_INT, label.getFormats().getLineNumber(), null);
		JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
				info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LABEL_ATTR_LINE_POS, W3CXSConstants.DATA_TYPE_DOUBLE, label.getFormats().getLinePosition(), null);
	}
	
	
	
	
	
	@Override
	protected void writeContentData(ReadWriteParameterMap parameters, JPhyloIOEventReceiver receiver, String id, Node node)
			throws IOException, IllegalArgumentException {		
		
		IntegerIDManager idManager = new IntegerIDManager();
		//Branch	
		TreeDataAdapter.writeLineAtrributes(receiver, id, node.getAfferentBranch(), idManager);
		
	JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
			info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_BRANCH_ATTR_CONSTANT_WIDTH, W3CXSConstants.DATA_TYPE_BOOLEAN, 
			node.getAfferentBranch().getFormats().isConstantWidth(), null);	
	JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
			info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_BRANCH_ATTR_MIN_LENGTH, W3CXSConstants.DATA_TYPE_FLOAT, 
			node.getAfferentBranch().getFormats().getMinLength().getInMillimeters(), null);
	JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
			info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_BRANCH_ATTR_MIN_SPACE_ABOVE, W3CXSConstants.DATA_TYPE_FLOAT, 
			node.getAfferentBranch().getFormats().getMinSpaceAbove().getInMillimeters(), null);
	JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
			info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_BRANCH_ATTR_MIN_SPACE_BELOW, W3CXSConstants.DATA_TYPE_FLOAT, 
			node.getAfferentBranch().getFormats().getMinSpaceBelow().getInMillimeters(), null);
	
			
		if (!node.getAfferentBranch().getLabels().isEmpty()) {
			Labels labels = node.getAfferentBranch().getLabels();
			//TODO initialize boolean above correctly.
			boolean above = true;			
	
			for (int lineNo = 0; lineNo < labels.lineCount(above); lineNo++) {
				for (int lineIndex = 0; lineIndex < labels.labelCount(above, lineNo); lineIndex++) {
					Label label = labels.get(above, lineNo, lineIndex);
					
					//Text labels
					if (label instanceof TextLabel) {
						TextElement textElement = ((TextElement)label);
						
						receiver.add(new ResourceMetadataEvent(TreeDataAdapter.createMetaID(id, idManager), null, new URIOrStringIdentifier(null, info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_TEXT_LABEL), null, null));
						JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
								info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_TEXT, W3CXSConstants.DATA_TYPE_STRING, 
								(textElement.getData()), null);
						TreeDataAdapter.writeTextAttributes(receiver, id, idManager, textElement);
						
						writeLabelAttributes(receiver, id, idManager, label);
						receiver.add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.RESOURCE_META));
					}
					
					//Icon labels
					else if (label instanceof IconLabel) {
						IconLabel iconLabel = ((IconLabel)label);
						
						receiver.add(new ResourceMetadataEvent(TreeDataAdapter.createMetaID(id, idManager), null, new URIOrStringIdentifier(null, info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_ICON_LABEL), null, null));
						TreeDataAdapter.writeLineAtrributes(receiver, id, iconLabel, idManager);
						
						JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
								info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_ICON_LABEL_ATTR_ICON, W3CXSConstants.DATA_TYPE_STRING, 
								iconLabel.getFormats().getIcon(), null);
						JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
								info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_ICON_LABEL_ATTR_ICON_FILLED, W3CXSConstants.DATA_TYPE_BOOLEAN, 
								iconLabel.getFormats().getIconFilled(), null);
						
						writeLabelDimensions(receiver, id, idManager, iconLabel, 
								info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_ICON_LABEL_ATTR_WIDTH, 
								info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_ICON_LABEL_ATTR_HEIGHT);
						
						writeLabelAttributes(receiver, id, idManager, label);
					}
					
					// Pie chart labels
					else if (label instanceof PieChartLabel) {
						PieChartLabel pieChartLabel = ((PieChartLabel)label);
						
						receiver.add(new ResourceMetadataEvent(TreeDataAdapter.createMetaID(id, idManager), null, new URIOrStringIdentifier(null, info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_PIE_CHART_LABEL), null, null));
						TreeDataAdapter.writeLineAtrributes(receiver, id, pieChartLabel, idManager);
						
						JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
								info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_PIE_CHART_LABEL_ATTR_INTERNAL_LINES, W3CXSConstants.DATA_TYPE_BOOLEAN, 
								pieChartLabel.getFormats().isShowInternalLines(), null);
						JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
								info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_PIE_CHART_LABEL_ATTR_NULL_LINES, W3CXSConstants.DATA_TYPE_BOOLEAN, 
								pieChartLabel.getFormats().isShowLinesForZero(), null);	
						receiver.add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.RESOURCE_META));

						receiver.add(new ResourceMetadataEvent(TreeDataAdapter.createMetaID(id, idManager), null, new URIOrStringIdentifier(null, info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_DATA_IDS), null, null));
						receiver.add(new ResourceMetadataEvent(TreeDataAdapter.createMetaID(id, idManager), null, new URIOrStringIdentifier(null, info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_DATA_ID), null, null));
						for (int index = 0; index < ((PieChartLabel)label).getSectionDataList().size(); index++) {
							JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
									info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_DATA_ID_ATTR_PIE_COLOR, info.bioinfweb.jphyloio.formats.xtg.XTGConstants.DATA_TYPE_COLOR, 
									pieChartLabel.getFormats().getPieColor(index), null);
							JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
									info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_DATA_ID_VALUE, W3CXSConstants.DATA_TYPE_STRING, 
									pieChartLabel.getValue(index), null);
							
							//TODO Predicates missing, need to be declared.
//							JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
//									PLACEHOLDER, W3CXSConstants.DATA_TYPE_STRING, 
//									pieChartLabel.getCaptionText(index), null);
//							JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
//									PLACEHOLDER, W3CXSConstants.DATA_TYPE_STRING, 
//									pieChartLabel.getFormats().getCaptionsTextFormats().toString(), null);
//							JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
//									PLACEHOLDER, W3CXSConstants.DATA_TYPE_STRING, 
//									pieChartLabel.getFormats().getCaptionsContentType().toString(), null);
//							JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, TreeDataAdapter.createMetaID(id, idManager), null,
//									PLACEHOLDER, W3CXSConstants.DATA_TYPE_STRING, 
//									pieChartLabel.getFormats().getCaptionsLinkType().toString(), null);						
							
							//TODO Attribute ShowTitle
							receiver.add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.RESOURCE_META));
							receiver.add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.RESOURCE_META));
						}
						writeLabelDimensions(receiver, id, idManager, pieChartLabel, 
								info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_PIE_CHART_LABEL_ATTR_WIDTH, 
								info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_PIE_CHART_LABEL_ATTR_HEIGHT);						
						writeLabelAttributes(receiver, id, idManager, label);						
					}
					receiver.add(new ResourceMetadataEvent(TreeDataAdapter.createMetaID(id, idManager), null, new URIOrStringIdentifier(null, info.bioinfweb.jphyloio.formats.xtg.XTGConstants.PREDICATE_LABEL_MARGIN), null, null));
					TreeDataAdapter.writeMargin(receiver, id, idManager, label.getFormats().getMargin());
					receiver.add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.RESOURCE_META));
				}
			}
		}
		//Hidden Data
		writeHiddenDataMap(receiver, id, node, idManager);		
	}









}

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
package info.bioinfweb.treegraph.document.io.jphyloio;


import info.bioinfweb.jphyloio.JPhyloIOEventReader;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.events.EdgeEvent;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.LabeledIDEvent;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;
import info.bioinfweb.jphyloio.events.NodeEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataContentEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.ResourceMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.URIOrStringIdentifier;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.events.type.EventTopologyType;
import info.bioinfweb.jphyloio.formats.nexml.NeXMLEventReader;
import info.bioinfweb.jphyloio.utils.JPhyloIOReadingUtils;
import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.HiddenDataMap;
import info.bioinfweb.treegraph.document.Node;
import info.bioinfweb.treegraph.document.TextElementData;
import info.bioinfweb.treegraph.document.Tree;
import info.bioinfweb.treegraph.document.io.AbstractDocumentReader;
import info.bioinfweb.treegraph.document.io.DocumentIterator;
import info.bioinfweb.treegraph.document.io.SingleDocumentIterator;
import info.bioinfweb.treegraph.document.nodebranchdata.BranchLengthAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeNameAdapter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;



public class NeXMLReader extends AbstractDocumentReader {
	private JPhyloIOEventReader reader;
	private JPhyloIOTreeReader treeReader = new JPhyloIOTreeReader();
	private List<Tree> trees = new ArrayList<Tree>();
	private List<String> names = new ArrayList<String>();
	

	public NeXMLReader() {
		super(false);
	}

	
	@Override
	public Document readDocument(BufferedInputStream stream) throws Exception {
		document = null;
		ReadWriteParameterMap parameters = new ReadWriteParameterMap();
		parameters.put(ReadWriteParameterMap.KEY_USE_OTU_LABEL, true);
		reader = new NeXMLEventReader(stream, parameters);  //TODO Use JPhyloIOReader for other formats (currently not possible, due to exceptions)
		
		try {
			JPhyloIOEvent event;
			while (reader.hasNextEvent()) {
				event = reader.next();
				switch (event.getType().getContentType()) {
					case DOCUMENT:
						if (event.getType().getTopologyType().equals(EventTopologyType.START)) {
							document = createEmptyDocument();
						}
						else if (event.getType().getTopologyType().equals(EventTopologyType.END)) {
							reader.close();

							if (!trees.isEmpty()) {
								Tree tree = trees.get(parameterMap.getTreeSelector().select(names.toArray(new String[names.size()]), trees));
								document.setTree(tree);
							}
							else {
								throw new IOException("The document did not contain any tree or no valid tree could be read from the document.");
							}

							return document;
						}
						break;
					case TREE_NETWORK_GROUP:
						readTreeNetworkGroup(event.asLinkedLabeledIDEvent());
						break;
					default:
						if (event.getType().getTopologyType().equals(EventTopologyType.START)) {  // Possible additional element, which is not read. SOLE and END events do not have to be processed here, because they have no further content.
							JPhyloIOReadingUtils.reachElementEnd(reader);
						}
						break;
				}
			}
		}
		finally {
			reader.close();
			stream.close();
		}		
		trees.clear();
		return null;
	}
	
	
	private void readTreeNetworkGroup(LinkedLabeledIDEvent treeGroupEvent) throws XMLStreamException, IOException {
    JPhyloIOEvent event = reader.next();   
    while (!event.getType().getTopologyType().equals(EventTopologyType.END)) {
    	if (event.getType().getContentType().equals(EventContentType.TREE)) { // Networks can not be displayed by TG and are therefore not read  		
    		readTree(event.asLabeledIDEvent());
    	}
    	else {  // Possible additional element, which is not read
      	if (event.getType().getTopologyType().equals(EventTopologyType.START)) {  // SOLE and END events do not have to be processed here, because they have no further content.
      		JPhyloIOReadingUtils.reachElementEnd(reader);
    		}
      }
      event = reader.next();
    }
  }
	
	
	private void readTree(LabeledIDEvent treeEvent) throws XMLStreamException, IOException {
		JPhyloIOTreeReader.TreeResult result = treeReader.readTree(treeEvent, reader, parameterMap.getApplicationLogger());
		
		String treeName;
		if ((result.getLabel() != null) && !result.getLabel().isEmpty()) {
			treeName = result.getLabel();
    }
		else {
			treeName = result.getID();
		}
		
    trees.add(result.getTree());
    names.add(treeName);    
	}
	
	
	@Override
	public DocumentIterator createIterator(BufferedInputStream stream) throws Exception {
		return new SingleDocumentIterator(read(stream));
	}
}

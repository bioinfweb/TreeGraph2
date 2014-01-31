package info.bioinfweb.treegraph.document.io;


import java.io.File;

import info.bioinfweb.treegraph.document.Document;
import info.bioinfweb.treegraph.document.nodebranchdata.NodeBranchDataAdapter;
import info.webinsel.util.log.ApplicationLogger;


/**
 * Iterates over all trees contained in multiple documents. The single documents may have different
 * supported formats.
 * 
 * @author Ben St&ouml;ver
 */
public class MultipleDocumentIterator extends AbstractDocumentIterator implements DocumentIterator {
	private File[] files;
	private int position = -1;
	private DocumentIterator documentIterator = null;
	
	
	public MultipleDocumentIterator(File[] files, ApplicationLogger loadLogger,
			NodeBranchDataAdapter internalAdapter,
			NodeBranchDataAdapter branchLengthsAdapter,
			boolean translateInternalNodes) {
		
		super(loadLogger, internalAdapter, branchLengthsAdapter, translateInternalNodes);
		this.files = files;
	}

	
	@Override
  protected Document readNext() throws Exception {
		Document result = null; 
		if (documentIterator != null){
			result = documentIterator.next();
		}
		
		if(result == null){
			position++;
			if (position < files.length){
				documentIterator = ReadWriteFactory.getInstance().getReader(files[position]).readAll(
						files[position], getParameterMap());
				return documentIterator.next();
			}
		}
		else {
			return result;
		}
		return null;
  }
}

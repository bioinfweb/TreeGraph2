/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2015  Ben Stöver, Kai Müller
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
package info.bioinfweb.treegraph.graphics.positionpaint;


import info.bioinfweb.treegraph.document.*;
import info.bioinfweb.treegraph.document.format.*;
import info.bioinfweb.treegraph.document.position.LegendPositionData;
import info.bioinfweb.treegraph.document.position.NodePositionData;
import info.bioinfweb.treegraph.document.position.PositionData;
import info.bioinfweb.commons.*;
import info.bioinfweb.commons.graphics.*;



public class RectangularCladogramPositioner implements TreePositioner {
	private static RectangularCladogramPositioner firstInstance = null;
	
	
	protected PositionPaintType type = PositionPaintFactory.getInstance().getType(this);
	protected Document document;
	private float maxLeafWidth = 0;  // Notwendig zur linksbündigen Positionierung der Blätter (wären sonst rechtsbündig)
	protected float rescalingFactorX = 1f;
	
	
	protected RectangularCladogramPositioner() {}
	
	
	public static RectangularCladogramPositioner getInstance() {
		if (firstInstance == null) {
			firstInstance = new RectangularCladogramPositioner();
		}
		return firstInstance;
	}

	
	/**
	 * Calculates the Dimensions of this text element. The height is already defined in
	 * by the provided formats and the width will calculated from the the text length scaled
	 * with the provided height. 
	 * @param text the text of the element which dimensions shall be calculated
	 * @param formats the text formats of the element
	 * @return the dimensions of the element not including optional margins
	 */
	private DistanceDimension calculateTextDimension(TextElement textElement) {
		DistanceDimension result = new DistanceDimension();
		
		TextFormats formats = textElement.getFormats();
		String text = textElement.getData().formatValue(formats.getDecimalFormat());
		float height = formats.getTextHeight().getInMillimeters();
		result.getHeight().setInMillimeters(height);
		result.getWidth().setInMillimeters(
			  FontCalculator.getInstance().getWidthToHeigth(
				formats.getFontName(), formats.getTextStyle() & ~TextFormats.UNDERLINE, 
        text, height));
		
		return result;		
	}
	
	
	private DistanceDimension calculateTaxonDimension(Node taxon) {
		DistanceDimension result = calculateTextDimension(taxon);
		Margin m = taxon.getFormats().getLeafMargin();
		result.getWidth().add(m.getLeft());
		result.getWidth().add(m.getRight());
		result.getHeight().add(m.getTop());
		result.getHeight().add(m.getBottom());
		return result;
	}
	
	
	private void calculateLabelBlockDimensions(Labels labels, boolean above) {
		for (int lineNo = 0; lineNo < labels.lineCount(above); lineNo++) {
			for (int pos = 0; pos < labels.labelCount(above, lineNo); pos++) {
				Label l = labels.get(above, lineNo, pos);
				PositionData pd = l.getPosition(type);
				
				if (l instanceof TextLabel) {
					DistanceDimension d = calculateTextDimension((TextLabel)l);
					pd.getWidth().assign(d.getWidth());
					pd.getHeight().assign(d.getHeight());
				}
				else {  // GraphicalLabel
					pd.getWidth().assign(((GraphicalLabel)l).getFormats().getWidth());
					pd.getHeight().assign(((GraphicalLabel)l).getFormats().getHeight());
				}
			}
		}
	}
	
	
	private float labelLineHeight(Labels labels, boolean above, int lineNo) {
		float result = 0;
		for (int lineIndex = 0; lineIndex < labels.labelCount(above, lineNo); lineIndex++) {
			Label l = labels.get(above, lineNo, lineIndex);
			Margin m = l.getFormats().getMargin();
			result = Math.max(result, m.getTop().getInMillimeters() + m.getBottom().getInMillimeters() + 
					l.getPosition(type).getHeight().getInMillimeters()); 			
		}
		return result;
	}
	
	
	/**
	 * Returns the overall height of all rows of labels above or below the branch.
	 * @return the width in in millimeters
	 */
	private float labelBlockHeight(Labels labels, boolean above) {
		float result = 0;
		for (int lineNo = 0; lineNo < labels.lineCount(above); lineNo++) {
			result += labelLineHeight(labels, above, lineNo);
		}
		return result;
	}


	private float labelLineWidth(Labels labels, boolean above, int lineNo) {
		float result = 0;
		for (int lineIndex = 0; lineIndex < labels.labelCount(above, lineNo); lineIndex++) {
			Label l = labels.get(above, lineNo, lineIndex);
			Margin m = l.getFormats().getMargin();
			result += l.getPosition(type).getWidth().getInMillimeters() + m.getLeft().getInMillimeters() + 
			    m.getRight().getInMillimeters(); 			
		}
		return result;
	}


	/**
	 * Returns the maximum width of all lines of labels above and below the branch. 
	 */
	protected float labelBlockWidth(Labels labels) {
		float result = 0;
		for (int lineNo = 0; lineNo < labels.lineCount(true); lineNo++) {
			result = Math.max(result, labelLineWidth(labels, true, lineNo));
		}
		for (int lineNo = 0; lineNo < labels.lineCount(false); lineNo++) {
			result = Math.max(result, labelLineWidth(labels, false, lineNo));
		}
		return result;
	}
	
	
	private float calculateWidthsHeights(Node root) {
	  float result = 0;  // breite = 0;
	  NodePositionData pd = root.getPosition(type);
	  pd.setHeightAbove(0);  // wurzel.höheOben = 0;
	  pd.setHeightBelow(0);  // wurzel.höheUnten = 0;
	  float branchHeight = 0;
	  if (root.hasAfferentBranch()) { 	// wenn (zuführender Ast vorhanden) dann
			branchHeight = root.getAfferentBranch().getFormats().getLineWidth().getInMillimeters();
			if (root.hasParent()) {
				branchHeight = Math.max(branchHeight, root.getParent().getFormats().getLineWidth().getInMillimeters());
				//TODO Wird die Dicke des Elternknotens schon wonanders berücksichtigt?
			}
	    root.getAfferentBranch().getPosition(type).getHeight().setInMillimeters(branchHeight);  // wurzel.elternast.höhe = wurzel.elternast.formate.liniendicke;
	    
			calculateLabelBlockDimensions(root.getAfferentBranch().getLabels(), true);  // wurzel.höheOben = (Höhen der oberen Labels bestimmen);
			pd.setHeightAbove(labelBlockHeight(root.getAfferentBranch().getLabels(), true) + 
					root.getAfferentBranch().getFormats().getMinSpaceAbove().getInMillimeters() + 
					0.5f * branchHeight);
			
			calculateLabelBlockDimensions(root.getAfferentBranch().getLabels(), false);  // nur Abmessungen der unteren Labels bestimmen
			pd.setHeightBelow(labelBlockHeight(root.getAfferentBranch().getLabels(), false) + 
					root.getAfferentBranch().getFormats().getMinSpaceBelow().getInMillimeters() + 
					0.5f * branchHeight);  // wurzel.höheUnten = (Höhen der unteren Labels bestimmen);  
	  }  // wenn_ende;
	  
		DistanceDimension d = calculateTaxonDimension(root);
	  if (root.getChildren().size() > 0) {  // wenn (Anzahl(Unteräste) > 0) dann
	    result = Math.max(result, calculateWidthsHeights(root.getChildren().get(0)));  // breite = max(breite, Schritt1(wurzel.ersterUnterknoten));
	    Node subnode = root.getChildren().get(0);
	    pd.setDifAbove(subnode.getPosition(type).getHeightAbove());  // wurzel.difOben = wurzel.ersterUnterast.höheOben;
	    if (root.getChildren().size() > 1) {  // wenn (Anzahl(Unteräste) > 1) dann
	      pd.getHeight().setInMillimeters(subnode.getPosition(type).getHeightBelow()); // wurzel.höhe = wurzel.ersterUnterast.höheUnten;
	      for (int i = 1; i <= root.getChildren().size() - 2; i++) {  // für alle Unteräste zwischen 1 und n-2  // wird ggf. gar nicht durchlaufen
	        subnode = root.getChildren().get(i);
					result = Math.max(result, calculateWidthsHeights(subnode));  // breite = max(breite, Schritt1(wurzel.unterknoten[i]));
	        pd.getHeight().add(subnode.getPosition(type).getHeightAbove() + root.getChildren().get(i).getPosition(type).getHeightBelow()); // wurzel.höhe += wurzel.unterast[i].höheOben + wurzel.unterast[i].höheUnten;
				}  // für_ende
			
        subnode = root.getChildren().get(root.getChildren().size() - 1);
	      result = Math.max(result, calculateWidthsHeights(subnode));  // breite = max(breite, Schritt1(wurzel.letzterUnterknoten));
	      pd.getHeight().add(subnode.getPosition(type).getHeightAbove());  // wurzel.höhe += wurzel.letzterUnterast.höheOben;
	    }
	    else {  // sonst
	      pd.getHeight().setInMillimeters(root.getFormats().getLineWidth().getInMillimeters());  // wurzel.höhe = 0 + Liniendicke;
	    }  // wenn_ende
	    pd.setDifBelow(subnode.getPosition(type).getHeightBelow());  // wurzel.difUnten = wurzel.letzterUnterast.höheUnten;
	    // subnode hat in beiden möglichen den Fällen hier den richtigen Wert.
	  }
	  else {  // sonst
	    pd.getHeight().assign(d.getHeight());  // wurzel.höhe = (Höhe von Wurzel als Blatt bestimmen)
	    pd.setDifAbove(0);
	    pd.setDifBelow(0);
	  }  // wenn_ende

	  float halfHeight = 0.5f * pd.getHeight().getInMillimeters() + pd.getDifAbove();  // HöheOben2 = 0,5 * wurzel.höhe + wurzel.difOben;
    if (pd.getHeightAbove() > halfHeight) {  // wenn (wurzel.höheOben > HöheOben2) dann
      pd.setDifAbove(pd.getHeightAbove() - 0.5f * pd.getHeight().getInMillimeters());  // wurzel.difOben = wurzel.höheOben - 0.5 * wurzel.höhe;
    }
    else {  // sonst
      pd.setHeightAbove(halfHeight);  // wurzel.höheOben = HöheOben2;
    }  // wenn_ende

    halfHeight = 0.5f * pd.getHeight().getInMillimeters() + pd.getDifBelow();  // HöheUnten2 = 0,5 * wurzel.höhe + wurzel.höheUnten;
    if (pd.getHeightBelow() > halfHeight) {  // wenn (wurzel.höheUnten > HöheUnten2) dann
      pd.setDifBelow(pd.getHeightBelow() - 0.5f * pd.getHeight().getInMillimeters());  // wurzel.difUnten = wurzel.höheUnten - 0.5 * wurzel.höhe;
    }
    else {  // sonst
      pd.setHeightBelow(halfHeight);  // wurzel.höheUnten = HöheUnten2;
    }  // wenn_ende
    
	  if (root.isLeaf()) {  // wenn (wurzel ist Blatt) dann
	    pd.getWidth().assign(d.getWidth());  // wurzel.breite = (Breite von Wurzel als Blatt bestimmen)
	    maxLeafWidth = Math.max(maxLeafWidth, pd.getWidth().getInMillimeters());
	  }
	  else {  // sonst
	    pd.getWidth().setInMillimeters(Math.min(root.getFormats().getCornerRadius().getInMillimeters() + root.getFormats().getLineWidth().getInMillimeters(), 0.5f * root.getPosition(type).getHeight().getInMillimeters()));  // wurzel.breite = (Breite von Wurzel als innerer Knoten bestimmen, dh. Liniendicke + REALER cornerRadius) 
		  result += pd.getWidth().getInMillimeters();  // breite = breite + wurzel.breite;
	  }  // wenn_ende
	  pd.getLeft().setInMillimeters(-result);  // wurzel.x = -breite;

	  if (root.hasAfferentBranch()) {  // wenn (zuführender Ast vorhanden) dann
	    //  (Breiten der Labels des zuführenden Asts bestimmen);  // bereits oben erfolgt
	  	Branch b = root.getAfferentBranch();
	    b.getPosition(type).getWidth().setInMillimeters(
	    		Math.max(labelBlockWidth(b.getLabels()), b.getFormats().getMinLength().getInMillimeters()));  // ast.breite = max(labels.blockWidth, minBranchWidth);  // Hier ggf. gespeicherte Astlänge berücksichtigen.
	    result += b.getPosition(type).getWidth().getInMillimeters();  // breite = breite + ast.breite;
	    b.getPosition(type).getLeft().setInMillimeters(-result);  // ast.x = -breite;
	  }  // wenn_ende

	  return result;  // rückgabe breite;
	}
	
	
	private void positionLabelBlockX(Branch branch, boolean above) {
		for (int lineNo = 0; lineNo < branch.getLabels().lineCount(above); lineNo++) {
			float lengthDif = branch.getPosition(type).getWidth().getInMillimeters() -
	        labelLineWidth(branch.getLabels(), above, lineNo);
			float left = branch.getPosition(type).getLeft().getInMillimeters();
			if ((lengthDif > 0) || !document.getTree().getFormats().getPositionLabelsToLeft()) {  // Ast länger
				left += 0.5f * lengthDif;
			}
			else {
				left += lengthDif;  // lengthDif ist negativ  //TODO LabelBlock könnte bei Phylogramm links über Dokumentenrand hinausgehen
			}
			
			for (int pos = 0; pos < branch.getLabels().labelCount(above, lineNo); pos++) {
				Label l = branch.getLabels().get(above, lineNo, pos);
				left += l.getFormats().getMargin().getLeft().getInMillimeters();
				PositionData pd = l.getPosition(type);
				pd.getLeft().setInMillimeters(left);
				left += pd.getWidth().getInMillimeters() + l.getFormats().getMargin().getRight().getInMillimeters();
			}
		}
	}
	
	
	private void positionLabelBlockY(Branch branch) {
	  // above:
    float y = branch.getPosition(type).getTop().getInMillimeters();
	  for (int lineNo = 0; lineNo < branch.getLabels().lineCount(true); lineNo++) {
		  int count = branch.getLabels().labelCount(true, lineNo);
		  if (count > 0) {
		  	float lineHeight = labelLineHeight(branch.getLabels(), true, lineNo);
			  y -= lineHeight;
				for (int pos = 0; pos < count; pos++) {
					Label l = branch.getLabels().get(true, lineNo, pos);
					PositionData pd = l.getPosition(type); 
					float height = pd.getHeight().getInMillimeters();
					Margin m = l.getFormats().getMargin();
					float top = Math.max(0.5f * (lineHeight - height), m.getTop().getInMillimeters());
					if (top + height + m.getBottom().getInMillimeters() > lineHeight) {
						top = lineHeight - height - m.getBottom().getInMillimeters();
					}
					pd.getTop().setInMillimeters(y + top); 
				}
		  }
		}
		
	  // below:
    y = branch.getPosition(type).getBottomInMillimeters();
	  for (int lineNo = 0; lineNo < branch.getLabels().lineCount(false); lineNo++) {
	  	int count = branch.getLabels().labelCount(false, lineNo);
	  	if (count > 0) {
		  	float lineHeight = labelLineHeight(branch.getLabels(), false, lineNo);
				for (int pos = 0; pos < count; pos++) {
					Label l = branch.getLabels().get(false, lineNo, pos);
					PositionData pd = l.getPosition(type);
					float height = pd.getHeight().getInMillimeters();
					Margin m = l.getFormats().getMargin();
					float top = Math.max(0.5f * (lineHeight - height), m.getTop().getInMillimeters());
					if (top + height + m.getBottom().getInMillimeters() > lineHeight) {
						top = lineHeight - height - m.getBottom().getInMillimeters();
					}
					pd.getTop().setInMillimeters(y + top); 
				}
			  y += lineHeight;
	  	}
		}
	}
	
	
	/**
	 * Returns the space between the right of the node and the real position of the node line
	 * dependant of the y position.
	 * 
	 * @param node 
	 * @param y - the absolute y position (not relative to the top of the node)
	 * @return the length in x (between 0 and cornerRadius)
	 */
	protected float calculateCornerRadiusShift(Node node, float y) {
		y = y - node.getPosition(type).getTop().getInMillimeters();
		float halfLineWidth = 0.5f * node.getFormats().getLineWidth().getInMillimeters();
		float cornerRadius = Math.min(node.getFormats().getCornerRadius().getInMillimeters(), 0.5f * node.getPosition(type).getHeight().getInMillimeters() - node.getFormats().getLineWidth().getInMillimeters());
		float nodeHeight = node.getPosition(type).getHeight().getInMillimeters();
		float yCircle = 0f;
		
		if (Math2.isBetween(y, halfLineWidth + cornerRadius, nodeHeight - halfLineWidth - cornerRadius)) {
			return -cornerRadius;
		}
		else if (Math2.isBetween(y, halfLineWidth, halfLineWidth + cornerRadius)) {
			yCircle = -(y - halfLineWidth - cornerRadius);
		}
		else if (Math2.isBetween(y, nodeHeight - halfLineWidth - cornerRadius, nodeHeight - halfLineWidth)) {
			yCircle = y - nodeHeight + halfLineWidth + cornerRadius; 
		}
		else {
			return 0;
		}
		return -(float)Math.sqrt(Math.pow(cornerRadius, 2) - Math.pow(yCircle, 2)) - 0.5f * node.getFormats().getLineWidth().getInMillimeters();  //In der Kurve wird bis zur Mitte der Astlinie gezeichnet um Lücken bei dicken Linien zu vermeiden.
	}
	
	
	/**
	 * Positions the subtree under root. (Widths ans heights must have been calculated 
	 * already.)
	 * 
	 * @param root - the root of the subtree to position
	 * @param overallWidth
	 * @param y0 - the start of the subtree on y
	 * @return the y coodinates untl which the positiones subtree reaches
	 */
	private float positionElements(Node root, float overallWidth, float y0) {
		NodePositionData pd = root.getPosition(type);
	  pd.getLeft().add(overallWidth);  // wurzel.x += gesamtbreite;
	
	  if (root.hasAfferentBranch()) {  // wenn (elternast vorhanden) dann
	  	PositionData branchPos = root.getAfferentBranch().getPosition(type);
	    branchPos.getTop().setInMillimeters(y0 + pd.getHeightAbove());  // wurzel.elternast.y = y0 + wurzel.höheOben;
	    if (root.hasParent()) {  // Verlägerung des Ast wg. cornerRadius
	    	branchPos.getTop().add(-0.5f * branchPos.getHeight().getInMillimeters());
	    	float cornerRadiusShift = calculateCornerRadiusShift(root.getParent(), branchPos.getTop().getInMillimeters() + .5f * branchPos.getHeight().getInMillimeters());
		    branchPos.getLeft().add(cornerRadiusShift);
		    branchPos.getWidth().add(-cornerRadiusShift);  // -cornerRadiusShift ist positiv
	    }

	    positionLabelBlockX(root.getAfferentBranch(), true);  // (x-Positionen der Labels in Abhängigkeit von unterast.x und zentriert über unterast.breite bestimmen);
	    positionLabelBlockX(root.getAfferentBranch(), false);
	    positionLabelBlockY(root.getAfferentBranch());  // (Labels von wurzel.elternast ebenfalls entsprechendes y zuweisen);
	  }  // wenn_ende
	  pd.getTop().setInMillimeters(y0 + pd.getDifAbove());  // wurzel.y = y0 + wuzel.difOben;  // Knoten.y ist oben und nicht in der Mitte des Knotens!
	
	  if (!root.isLeaf()) {  //wenn (wurzel hat unterknoten) dann
	  	float currentY0 = y0 + pd.getDifAbove() - root.getChildren().get(0).getPosition(type).getHeightAbove();  // aktY0 = y0 + wurzel.difOben - wurzel.ersterUnterast.höheOben
	    float newX = pd.getLeft().getInMillimeters() + pd.getWidth().getInMillimeters();  // neuesX = wurzel.x + wurzel.breite;
		  for (int i = 0; i < root.getChildren().size(); i++) {  // für alle Unteräste bzw. Unterknoten von wurzel
		    PositionData childPD = root.getChildren().get(i).getAfferentBranch().getPosition(type);  
		    childPD.getWidth().add(childPD.getLeft().getInMillimeters() + overallWidth - newX);  // ast.breite = ast.breite + ((ast.x + gesamtbreite) - neuesX);  // ! alteBreite <= neueBreite
		    childPD.getLeft().setInMillimeters(newX);  // ast.x = neuesX;
		    currentY0 = positionElements(root.getChildren().get(i), overallWidth, currentY0);  // aktY0 = Schritt2(unterknoten, gesamtbreite, aktY0);
			}  // für_ende
	  }  // wenn_ende
	
	  return y0 + pd.getHeightAbove() + pd.getHeightBelow();  // rückgabe y0 + wurzel.höheOben + wurzel.höheUnten;
	}
	
	
  protected void calculatePaintDimension(float overallWidth, float overallHeight) {
  	GlobalFormats gf = document.getTree().getFormats();
    document.getTree().getPaintDimension(type).getWidth().setInMillimeters(
				gf.getDocumentMargin().getLeft().getInMillimeters() + 
				overallWidth + 
				gf.getDocumentMargin().getRight().getInMillimeters());
    document.getTree().getPaintDimension(type).getHeight().setInMillimeters(
				gf.getDocumentMargin().getTop().getInMillimeters() + 
				overallHeight + 
				gf.getDocumentMargin().getBottom().getInMillimeters());
    //TODO Höhe der Maßstabsangaben hinzufg.
  }
	
	
  /**
   * This methode can be overwritten by extending classes to define a different rescaling
   * process. It could e.g. be used to reduce the corner radius of a node
   * @param node - the node to be rescaled
   * @param width - the old width of the node in millimeters
   * @return the new width of the node in millimeters
   */
  protected float rescaleNodeWidth(Node node, float width) {
  	return width;
  }
  
  
  /**
   * This methode can be overwritten by extending classes to define a different rescaling
   * process.
   * @param branch - the branch to be rescaled 
   * @param width - the old width of the branch in millimeters 
   * @return the new width of the branch in millimeters
   */
  protected float rescaleBranchWidth(Branch branch, float width) {
  	if (branch.getTargetNode().hasParent()) {
  		float nodeWidth = branch.getTargetNode().getParent().getPosition(type).getWidth().getInMillimeters();
  		return (width + nodeWidth) * rescalingFactorX - nodeWidth;
  	}
  	else {
  		return width * rescalingFactorX;
  	}
  }
  
  
	/**
	 * Rescales the subtree under root.
	 * @param root
	 * @param shift
	 * @return the maximal y cooronate for the end of a branch
	 */
	private float rescaleSubtree(Node root, float shift) {
		if (root.hasAfferentBranch()) {
			PositionData branchPD = root.getAfferentBranch().getPosition(type);
			branchPD.getLeft().add(shift);
			
			float oldWidth = branchPD.getWidth().getInMillimeters();
			float newWidth = rescaleBranchWidth(root.getAfferentBranch(), oldWidth);
			branchPD.getWidth().setInMillimeters(newWidth);
			positionLabelBlockX(root.getAfferentBranch(), true);
			positionLabelBlockX(root.getAfferentBranch(), false);
			shift += newWidth - oldWidth;
		}
		PositionData nodePD = root.getPosition(type); 
		nodePD.getLeft().add(shift);

		float oldWidth = nodePD.getWidth().getInMillimeters();
		float newWidth = rescaleNodeWidth(root, oldWidth);
		nodePD.getWidth().setInMillimeters(newWidth);
		shift += newWidth - oldWidth;
		
		float maxWidth = root.getAfferentBranch().getPosition(type).getRightInMillimeters();
		for (int i = 0; i < root.getChildren().size(); i++) {
			maxWidth = Math.max(maxWidth, rescaleSubtree(root.getChildren().get(i), shift));
		}
		return maxWidth;
	}
	
	
	private float getLegendStartX(LegendFormats f, float treeRight) {
		if (document.getTree().getFormats().getAlignLegendsToSubtree()) {
			f.sortAnchors(type);
			Node lowerAnchor = f.getAnchor(1);
			if (lowerAnchor == null) {
				lowerAnchor = f.getAnchor(0);
			}
			
			Node[] leafs = TreeSerializer.getLeafNodesBetween(f.getAnchor(0).getHighestChild(), 
					lowerAnchor.getLowestChild());
			float result = 0;
			for (int i = 0; i < leafs.length; i++) {
				result = Math.max(result, leafs[i].getPosition(type).getRightInMillimeters());
			}
			return result;
		}
		else {
			return treeRight;
		}
	}
	
	
	private float alignToOtherLegends(Legends legends, int start, int end, float minLeft, float top, 
			float bottom) {
		
		float leftMargin = legends.get(end + 1).getFormats().getMargin().getLeft().getInMillimeters();
		for (int j = start; j <= end; j++) {
			LegendPositionData otherPD = legends.get(j).getPosition(type);
			Margin m = legends.get(j).getFormats().getMargin();
			float otherTop = otherPD.getTop().getInMillimeters() - m.getTop().getInMillimeters();
			float otherBottom = otherPD.getBottomInMillimeters() + m.getBottom().getInMillimeters();
			if (Math2.overlapsNE(top, bottom, otherTop, otherBottom)) {
				minLeft = Math.max(minLeft, otherPD.getRightInMillimeters() + 
						leftMargin + m.getRight().getInMillimeters());
			}
		}
		return minLeft;
	}
	
	
	private float getLegendTop(LegendFormats f) {
		Node anchor = f.getAnchor(0).getHighestChild(); 
		return anchor.getPosition(type).getTop().getInMillimeters() + 
		    anchor.getFormats().getLeafMargin().getTop().getInMillimeters();
	}
	
	
	private float getLegendBottom(LegendFormats f) {
		Node anchor;
		if (f.hasOneAnchor()) {
			anchor = f.getAnchor(0).getLowestChild();
		}
		else {
			anchor = f.getAnchor(1).getLowestChild();
		}
		return anchor.getPosition(type).getBottomInMillimeters() - 
      anchor.getFormats().getLeafMargin().getBottom().getInMillimeters();
	}
	
	
	/**
	 * Positions all legends rigths if <code>startX</code>.
	 * @param startX - the left border of the legends sector in the document
	 * @return the right border of the legends sector (= new width of the document without 
	 * margin) 
	 */
	private float positionLegends(float startX) {
		float result = startX;
		Legends legends = document.getTree().getLegends();
		
		for (int i = 0; i < legends.size(); i++) {
			int lastPositionIndex = legends.get(i).getFormats().getPosition();
			int positionIndexStart = i;
			float positionLeft = 0;
			
		  while ((i < legends.size()) && (legends.get(i).getFormats().getPosition() == lastPositionIndex)) {
				Legend l = legends.get(i);
				LegendFormats f = l.getFormats();
				LegendPositionData pd = l.getPosition(type);
				Margin m = l.getFormats().getMargin();
				
				// Positionierung der Klammer auf y:
				f.sortAnchors(type);
				float top = getLegendTop(f);
				float bottom = getLegendBottom(f);
				pd.getLinePos().getTop().setInMillimeters(top);
				pd.getLinePos().getHeight().setInMillimeters(bottom - top);
				
				DistanceDimension textDim = calculateTextDimension(l);
				if (f.getOrientation().equals(TextOrientation.HORIZONTAL)) {
					pd.getTextPos().getWidth().assign(textDim.getWidth());
					pd.getTextPos().getHeight().assign(textDim.getHeight());
				}
				else {
					pd.getTextPos().getWidth().assign(textDim.getHeight());
					pd.getTextPos().getHeight().assign(textDim.getWidth());
				}
				
				// Höhe vergrößern falls Text höher als Klammer ist:
				float heightDif = pd.getTextPos().getHeight().getInMillimeters() - (bottom - top);  
				if (heightDif > 0) {
					heightDif /= 2f;
					top -= heightDif;
					pd.getTextPos().getTop().setInMillimeters(top);
					bottom += heightDif;
				}
				else {
					pd.getTextPos().getTop().setInMillimeters(top + 
							0.5f * pd.getLinePos().getHeight().getInMillimeters() - 
							0.5f * pd.getTextPos().getHeight().getInMillimeters());
				}
				
				// Calculation of left:
				top -= m.getTop().getInMillimeters();
				bottom += m.getBottom().getInMillimeters(); 
				positionLeft = Math.max(positionLeft, getLegendStartX(f, startX) + m.getLeft().getInMillimeters() + 
				    f.getMinTreeDistance().getInMillimeters());
				positionLeft = alignToOtherLegends(legends, 0, positionIndexStart - 1, positionLeft, top, bottom);
				
				i++;
			}
		  i--;
		  
		  for (int j = positionIndexStart; j <= i; j++) {
				Legend l = legends.get(j);
				LegendPositionData pd = l.getPosition(type);
				LegendFormats f = l.getFormats();
				Margin m = l.getFormats().getMargin();
				
				// Further calculation of left:
				float left = alignToOtherLegends(legends, positionIndexStart, 
						j - 1, positionLeft, getLegendTop(f) - m.getTop().getInMillimeters(), 
						getLegendBottom(f) + m.getBottom().getInMillimeters());
				pd.getLinePos().getLeft().setInMillimeters(left);
				
				// Calculation of width:
				float width = l.getFormats().getSpacing().getInMillimeters();
				if (l.getFormats().getLegendStyle().equals(LegendStyle.BRACE)) {
					width += Math.min(
							2f * f.getCornerRadius().getInMillimeters() + f.getLineWidth().getInMillimeters(), 
							0.5f * pd.getLinePos().getHeight().getInMillimeters());
				}
				else {  // l.getFormats().getLegendStyle().equals(LegendStyle.BRACKET)
					width += f.getCornerRadius().getInMillimeters() + f.getLineWidth().getInMillimeters();
				}
				pd.getLinePos().getWidth().setInMillimeters(width);
				pd.getTextPos().getLeft().setInMillimeters(left + width);
				 
	      width += pd.getTextPos().getWidth().getInMillimeters();
				result = Math.max(result, left + width + m.getRight().getInMillimeters());
			}
		}
		return result;
	}
	
	
	/**
	 * Positions the scale bar.
	 * @param treeWidth - the width of the tree positioned above 
	 * @param maxTreeY - the end of the tree positioned above on y
	 * @return the end of the scale bar on y
	 */
	private float positionScaleBar(float treeWidth, float maxTreeY) {
		ScaleBar scaleBar = document.getTree().getScaleBar();
		if (scaleBar == null) {
			return maxTreeY;
		}
		else {
			float branchLengthScale = 
				  document.getTree().getFormats().getBranchLengthScale().getInMillimeters();
			PositionData pd = scaleBar.getPosition(type);
			ScaleBarFormats f = scaleBar.getFormats();
			
			switch (f.getAlignment()) {
				case LEFT: case RIGHT:
					pd.getWidth().setInMillimeters(f.getWidth().getInMillimeters(branchLengthScale));
					break;
				case TREE_WIDTH:
					pd.getWidth().setInMillimeters(treeWidth);
					break;
			}
			
			pd.getHeight().assign(f.getHeight());
			pd.getHeight().add(f.getTextHeight().getInMillimeters());  // Beschriftung
			if (!(scaleBar.getData().isEmpty() || scaleBar.getData().getText().equals(""))) {
				pd.getHeight().add(f.getTextHeight().getInMillimeters());  // Angabe der Einheit
			}

			switch (f.getAlignment()) {
				case LEFT: case TREE_WIDTH:
					pd.getLeft().setInMillimeters(0f);
					break;
				case RIGHT:
					pd.getLeft().setInMillimeters(
							treeWidth - f.getWidth().getInMillimeters(branchLengthScale));
					break;
			}
			pd.getTop().setInMillimeters(maxTreeY + f.getTreeDistance().getInMillimeters());
			return pd.getTop().getInMillimeters() + pd.getHeight().getInMillimeters();
		}
	}
	
	
  private void movePosition(PositionData pd, float dX, float dY) {
  	pd.getLeft().add(dX);
  	pd.getTop().add(dY);
  }
	
	
	private void moveLabels(Labels labels, boolean above, float dX, float dY) {
		for (int lineNo = 0; lineNo < labels.lineCount(above); lineNo++) {
			for (int lineIndex = 0; lineIndex < labels.labelCount(above, lineNo); lineIndex++) {
				movePosition(labels.get(above, lineNo, lineIndex).getPosition(type), dX, dY);
			}
		}
	}
  
  
  private void moveSubtree(Node root, float dX, float dY) {
		if (root.hasAfferentBranch()) {
			Branch b = root.getAfferentBranch();
			movePosition(b.getPosition(type), dX, dY);
			moveLabels(b.getLabels(), true, dX, dY);
			moveLabels(b.getLabels(), false, dX, dY);
		}
		movePosition(root.getPosition(type), dX, dY);
		
		for (int i = 0; i < root.getChildren().size(); i++) {
			moveSubtree(root.getChildren().get(i), dX, dY);
		}
	}
  
  
  private void moveAll(float dX, float dY) {
  	if (!document.getTree().isEmpty()) {
  		moveSubtree(document.getTree().getPaintStart(), dX, dY);
  	}
  	
  	if (document.getTree().getFormats().getShowScaleBar()) {
  		movePosition(document.getTree().getScaleBar().getPosition(type), dX, dY);
  	}
  	
  	Legends legends = document.getTree().getLegends();
  	for (int i = 0; i < legends.size(); i++) {
  		LegendPositionData pd = legends.get(i).getPosition(type);
			movePosition(pd.getLinePos(), dX, dY);
			movePosition(pd.getTextPos(), dX, dY);
		}
  }
  
  
  /**
   * The returned value is the additional space that is necessary left of the tree, if either the root branch is invisible,
   * but carries labels or if the branch is shorter than the space its labels consume. (The latter is only relevant for
   * {@link PhylogramPositioner}, but is already implemented here automatically.)
   */
  private float invisibleRootBranchOffset() {
		Branch rootBranch = document.getTree().getPaintStart().getAfferentBranch();
	  return Math.max(0f, labelBlockWidth(rootBranch.getLabels()) - rootBranch.getPosition(type).getWidth().getInMillimeters());
  }
  
  
  /**
   * Moves the tree down by the legends overlay on the top and calculates the new document 
   * height including the possible overlay of the legends. Additionaly the tree is moves
   * to the left according to the document margin and the return value of {@link #invisibleRootBranchOffset()}.
   * 
   * @return the new overall height of the document (without document margin)
   */
  private float moveForLegends() {
  	Margin m = document.getTree().getFormats().getDocumentMargin();
  	float top = m.getTop().getInMillimeters();
  	float bottom = 0f;
  	
  	Legends legends = document.getTree().getLegends();
  	for (int i = 0; i < legends.size(); i++) {
			LegendPositionData pd = legends.get(i).getPosition(type);
			top = Math.min(top, pd.getTop().getInMillimeters());
			bottom = Math.max(bottom, pd.getBottomInMillimeters());
		}
  	float dTop = m.getTop().getInMillimeters() - top;
  	moveAll(document.getTree().getFormats().getDocumentMargin().getLeft().getInMillimeters() + invisibleRootBranchOffset(), dTop);
  	
  	NodePositionData pd = document.getTree().getPaintStart().getPosition(type);
  	float treeHeight = pd.getHeightAbove() + pd.getHeightBelow();
  	return dTop + treeHeight + Math.max(0f, bottom - (treeHeight + m.getTop().getInMillimeters()));  // letzter Summand ist dBottom
  }
	
	
	public void positionAll(Document document, float rescalingFactorX) {
		this.document = document;
		this.rescalingFactorX = rescalingFactorX;
		maxLeafWidth = 0;
		
	  if (!document.getTree().isEmpty()) {
	  	// Position tree:
			float overallWidth = calculateWidthsHeights(document.getTree().getPaintStart()); // gesamtbreite = Schritt1(Zeichenausgangspunkt);
	    Branch rootBranch = document.getTree().getPaintStart().getAfferentBranch(); 
	    if ((rootBranch != null) && !document.getTree().getFormats().getShowRooted()) {
	    	overallWidth -= rootBranch.getPosition(type).getWidth().getInMillimeters();
	    	rootBranch.getPosition(type).getWidth().setInMillimeters(0f);
	    }
	    float overallHeight = positionElements(document.getTree().getPaintStart(), overallWidth, 
	    		document.getTree().getFormats().getDocumentMargin().getTop().getInMillimeters());  // Schritt2(Zeichenausgangspunkt, gesamtbreite1, DocumentMargin.Top);
	    if (rootBranch != null) {
	    	rootBranch.getPosition(type).getLeft().setInMillimeters(0f);  // Baum wird nachher um DocumentMargin verschoben
	    }
	    overallWidth = rescaleSubtree(document.getTree().getPaintStart(), 0);
	    
	    // Position scale bar:
	    float newHeight = positionScaleBar(overallWidth, overallHeight);
	    overallWidth += maxLeafWidth;
	    if (document.getTree().getFormats().getShowScaleBar()) {
	    	overallHeight = newHeight; 
	    }
	    
	    // Position legends:
	    overallWidth = positionLegends(overallWidth);
	    overallHeight = Math.max(overallHeight, moveForLegends());
	    
	    calculatePaintDimension(overallWidth, overallHeight);
	  }
	  else {
	    calculatePaintDimension(0, 0);
	  }
	}
	
	
  private ConcretePaintableElement searchLabelBlock(Branch branch, float x, float y, 
  		float margin) {
  	
  	boolean above = false;  // Vorgabe: Gesuchtes Label befindet sich höchstens unterhalb des Asts.
  	if (y <= branch.getPosition(type).getTop().getInMillimeters()) {  // Gesuchtes Label kann höchstens oben sein.
  		above = true;
  	}
  	
		for (int lineNo = 0; lineNo < branch.getLabels().lineCount(above); lineNo++) {  //TODO Labelsuche kann ggf. effizienter implementiert werden.
			for (int lineIndex = 0; lineIndex < branch.getLabels().labelCount(above, lineNo); lineIndex++) {
				Label label = branch.getLabels().get(above, lineNo, lineIndex);
				if (label.getPosition(type).contains(x, y, margin)) {
					return label;
				}
			}
		}
		return null;
  }
  
  
  protected boolean xToHigh(float x, PositionData pd) {
  	return x < pd.getLeft().getInMillimeters();
  }
	
	
	private ConcretePaintableElement searchElementToPosition(Node root, float x, float y, 
			float margin) {
		
		NodePositionData pd = root.getPosition(type);
		float middle = pd.getTop().getInMillimeters() + 0.5f * pd.getHeight().getInMillimeters();
		if (Math2.isBetween(y, middle - pd.getHeightAbove(), middle + pd.getHeightBelow())) {
			if (root.hasAfferentBranch()) {
				if (xToHigh(x, root.getAfferentBranch().getPosition(type))) {
					return null;  // Weder Label noch kommender Unterbaum kommen in Frage.
				}
				if (root.getAfferentBranch().getPosition(type).contains(x, y, margin)) {
					if (root.getPosition(type).contains(x, y, margin)) {  // Bei Überlappung Knoten bevorzugen
						return root;
					}
					else {
						return root.getAfferentBranch();
					}
	  		}
				else {
					ConcretePaintableElement result = searchLabelBlock(root.getAfferentBranch(), x, y, margin);
					if (result != null) {
						return result;
					}
				}
			}
			
			if (xToHigh(x, pd)) {
				return null;  // Punkt liegt links vom folgenden Teilbaum.
			}
			else if (root.getPosition(type).contains(x, y, margin)) {
				return root;
			}
			else {
				for (int i = 0; i < root.getChildren().size(); i++) {
					ConcretePaintableElement result = searchElementToPosition(root.getChildren().get(i), x, y, margin);
					if (result != null) {
						return result;
					}
				}
			}
		}
		return null;
	}
	
	
	/**
	 * Determines the element to a position defined by x- and y-coordinates (e.g. to
	 * select an element by mouse).
	 * @param document the document where the position should be dertermined.
	 * @param x the x-position in mm
	 * @param y the y-position in mm
	 */
	public ConcretePaintableElement elementToPosition(Document document, float x, float y, 
			float margin) {
		
		this.document = document;
		
		// Search tree elements: 
		if (!document.getTree().isEmpty()) {
			ConcretePaintableElement result = searchElementToPosition(
					document.getTree().getPaintStart(), x, y, margin);
			if (result != null) {
				return result;
			}
		}
		
		// Check scale bar
		if (document.getTree().getFormats().getShowScaleBar() &&
				document.getTree().getScaleBar().getPosition(type).contains(x, y, margin)) {
			return document.getTree().getScaleBar();
		}
		
		// Search legends:
		Legends legends = document.getTree().getLegends();
		for (int i = 0; i < legends.size(); i++) {
			if (legends.get(i).getPosition(type).contains(x, y, margin)) {
				return legends.get(i);
			}
		}
		
		return null;
	}
}
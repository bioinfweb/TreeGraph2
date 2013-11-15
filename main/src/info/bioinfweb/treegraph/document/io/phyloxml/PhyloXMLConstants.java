/*
 * TreeGraph 2 - A feature rich editor for phylogenetic trees
 * Copyright (C) 2007-2013  Ben Stöver, Kai Müller
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
package info.bioinfweb.treegraph.document.io.phyloxml;


import javax.xml.namespace.QName;



/**
 * Provides XML constants for the phyloXML format.
 * @author Ben St&ouml;ver
 * @since 2.0.35
 */
public interface PhyloXMLConstants {
  public static final String ID_PREFIX = "phyloXML.";
  
  public static final String DEFAULT_CONFIDENCE_NAME = "Confidence ";	
  public static final String DEFAULT_TREE_NAME = "Tree ";
  public static final String BRANCH_WIDT_DATA_NAME = ID_PREFIX + "branch_width";
  
  public static final String NAMESPACE_URI = "http://www.phyloxml.org";  
  
  public static final QName TAG_ROOT = new QName(NAMESPACE_URI, "phyloxml");
  public static final QName TAG_PHYLOGENY = new QName(NAMESPACE_URI, "phylogeny");
  public static final QName ATTR_ROOTED = new QName(NAMESPACE_URI, "rooted");
  public static final QName ATTR_BRANCH_LENGTH_UNIT = new QName("branch_length_unit");
  
  public static final QName TAG_CLADE = new QName(NAMESPACE_URI, "clade");
  public static final QName ATTR_BRANCH_LENGTH = new QName("branch_length");
  
  public static final QName TAG_NAME = new QName(NAMESPACE_URI, "name");  // Subelement of phylogeny or clade
  public static final QName TAG_BRANCH_LENGTH = new QName(NAMESPACE_URI, "branch_length");
  public static final QName TAG_LINE_WIDTH = new QName(NAMESPACE_URI, "width");
  
  public static final QName TAG_CONFIDENCE = new QName(NAMESPACE_URI, "confidence");
  public static final QName ATTR_TYPE = new QName("type");
  
  public static final QName TAG_LINE_COLOR = new QName(NAMESPACE_URI, "color");
  public static final QName TAG_COLOR_RED = new QName(NAMESPACE_URI, "red");
  public static final QName TAG_COLOR_GREEN = new QName(NAMESPACE_URI, "green");
  public static final QName TAG_COLOR_BLUE = new QName(NAMESPACE_URI, "blue");
  
  public static final QName TAG_TAXONOMY = new QName(NAMESPACE_URI, "taxonomy");
  public static final QName TAG_CODE = new QName(NAMESPACE_URI, "code");
  public static final QName TAG_SCIENTIFIC_NAME = new QName(NAMESPACE_URI, "scientific_name");
  public static final QName TAG_AUTHORITY = new QName(NAMESPACE_URI, "authority");
  public static final QName TAG_COMMON_NAME = new QName(NAMESPACE_URI, "common_name");
  public static final QName TAG_SYNONYM = new QName(NAMESPACE_URI, "synonym");
  public static final QName TAG_RANK = new QName(NAMESPACE_URI, "rank");
  
  public static final QName TAG_SEQUENCE = new QName(NAMESPACE_URI, "sequence");
  public static final QName TAG_SYMBOL = new QName(NAMESPACE_URI, "symbol");
  public static final QName TAG_ACCESSION = new QName(NAMESPACE_URI, "accession");
  public static final QName TAG_LOCATION = new QName(NAMESPACE_URI, "location");
  
  public static final QName TAG_DISTRIBUTION = new QName(NAMESPACE_URI, "distribution");
  public static final QName TAG_DESCRIPTION = new QName(NAMESPACE_URI, "desc");
  
  public static final QName TAG_DATE = new QName(NAMESPACE_URI, "date");
  public static final QName TAG_VALUE = new QName(NAMESPACE_URI, "value");
  public static final QName TAG_MIN = new QName(NAMESPACE_URI, "minimum");
  public static final QName TAG_MAX = new QName(NAMESPACE_URI, "maximum");
}
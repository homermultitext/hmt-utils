package org.homermultitext.utils

import org.apache.commons.io.FilenameUtils
import edu.harvard.chs.f1k.GreekNode

import edu.holycross.shot.greekutils.GreekString
import edu.holycross.shot.greekutils.MilesianString


/** Class to tokenize text following Homer Multitext project conventions for
*   definition of character set and markup.
*/
class HmtEditorialTokenization {

  Integer debug = 0

  
  /** Empty constructor.
   */
  HmtEditorialTokenization() {
  }


  /** Gets a short phrase describing the kind of tokenization
   * done by this implementation of TokenizationSystem.
   * @returns A descriptive String.
   */
  String getDescription() {
    return "Tokenization of Greek editions following the HMT project's editorial conventions, and taking into consideration both TEI markup and Unicode character values."
  }

  /** Closure splits a String on white space, without
   * performing any analysis of token type.
   * @param str The String to tokenize.
   * @returns An ArrayList of Strings.
   */
  ArrayList splitString (str) {
    ArrayList tokes = str.split(/[\s]+/)
    if (debug > 1) {
      System.err.println ("Input str " + str)
      System.err.println ("Split is " + tokes)
    }
    return tokes
  }    

  
  /** Tokenizes a string of text taking account of the context given by tokenType.
   * @str String to tokenize
   * @urnBase The CTS URN value, as a String, of the citable node this string
   * belongs to.
   * @tokenType A String, possibly empty, classifying the tokens belonging to this 
   * node.
   * @returns A List of URN pairs.  Each pairing is an ArrayList containing two
   * identifiers.  The first is the String value of a CTS URN including subreference, 
   * identifying the token; the second is a CITE URN from one of the following
   * collections:
   * urn:cite:hmt:tokenclasses
   * urn:cite:hmt:place
   * urn:cite:hmt:pers
   */
  ArrayList tokenizeString (String str, String urnBase, String tokenType)
  throws Exception {
    println "Tokenize ${str} as ${tokenType}"
    ArrayList classifiedTokens = []
    splitString(str).each { t ->
      ArrayList pairing
      switch(tokenType) {

      case "urn:cite:hmt:tokentypes.waw":
      case "urn:cite:hmt:tokentypes.sic":
      pairing = ["${urnBase}@${t}", tokenType]
      break
	    
      case "urn:cite:hmt:tokentypes.numeric":
      try {
      } catch (Exception e) {
	MilesianString ms = new MilesianString(t, "Unicode")
	System.err.println "HmtEditorialTokenization: could not form MilesianString from string ${t}"
      }
      pairing = ["${urnBase}@${t}", tokenType]
      break

      default:
      if ((tokenType ==~ /urn:cite:hmt:place.+/) || ( tokenType ==~ /urn:cite:hmt:pers.+/) ) {
	try {
	  GreekString gs = new GreekString(t, "Unicode")
	  pairing = ["${urnBase}@${t}", tokenType]
	} catch (Exception e) {
	  System.err.println "HmtEditorialTokenization: could not form GreekString from string ${t}"
	}
	
      } else {
	
	try {
	  GreekString gs = new GreekString(t, "Unicode")
	  pairing = ["${urnBase}@${t}", "urn:cite:hmt:tokentypes.lexical"]
	} catch (Exception e) {
	  System.err.println "HmtEditorialTokenization: could not form GreekString from string ${t}"
	}
      }
      break
      }
      classifiedTokens.add(pairing)
    }
    return(classifiedTokens)
  }
  

  /** Recursively tokenizes a well-formed fragment of a document following HMT project
   * editorial conventions.  Tokenization considers both markup and type of characters.
   * @param node The root node of the fragment to tokenize.
   * @urnBase The CTS URN value, as a String, of the citable node this XML fragment
   * belongs to.
   * @tokenType A String, possibly empty, classifying the tokens belonging to this 
   * node.
   * @returns A List of URN pairs.  Each pairing is an ArrayList containing two
   * identifiers.  The first is the String value of a CTS URN including subreference, 
   * identifying the token; the second is a CITE URN from one of the following
   * collections:
   * urn:cite:hmt:tokenclasses
   * urn:cite:hmt:place
   * urn:cite:hmt:pers
   */
  ArrayList tokenizeElement(Object node, String urnBase, String tokenType)
  throws Exception {
    ArrayList classifiedTokens = []
    
    if (node instanceof java.lang.String) {
      classifiedTokens = classifiedTokens +  tokenizeString(node, urnBase, tokenType)


    } else {
      String nodeName
      if (node.name() instanceof java.lang.String) {
	nodeName = node.name()
      } else {
	nodeName = node.name().getLocalPart()
      }


      switch (nodeName) {
      case "del":
      case "abbr":
      case "figDesc":
      case "ref":
      case "note":
      // Completely ignore contents.
      break


      case "div":
      if (node.'@type' == "ref") { 	
	// omit
      } else {
	node.children().each { child ->
	  tokenizeElement(child, urnBase, "").each { tokenList ->
	    classifiedTokens.add(tokenList)
	  }
	}
      }
      break

      case "num" :
      node.children().each { child ->
	tokenizeElement(child, urnBase, "urn:cite:hmt:tokentypes.numeric").each { tokenList ->
	  classifiedTokens.add(tokenList)
	}
      }
      break

      case "sic":
      GreekNode n = new GreekNode(node)
      String wd = n.collectText()
      wd = wd.replaceAll(~/\s/, "") // generalize to all white space
      if (tokenType.size() > 0) {
	classifiedTokens.add(["${urnBase}@${wd}", "urn:cite:hmt:tokentypes.sic"])
      } 
      break


      case "w":
      GreekNode n = new GreekNode(node)
      String wd = n.collectText()
      wd = wd.replaceAll(~/\s/, "") // generalize to all white space
      if (tokenType.size() > 0) {
	classifiedTokens.add(["${urnBase}@${wd}", tokenType])
      } else {
	classifiedTokens.add(["${urnBase}@${wd}", "urn:cite:hmt:tokentypes.lexical"])
      }
      break

      case "persName":
      GreekNode n = new GreekNode(node)
      GreekString gs = new GreekString(n.collectText().replaceAll(/ /,''), "Unicode")
      classifiedTokens.add(["${urnBase}@${gs.toString(true)}", "${node.'@n'}"])
      break

      
      case "placeName":
      GreekNode n = new GreekNode(node)
      GreekString gs = new GreekString(n.collectText().replaceAll(/ /,''), "Unicode")
      classifiedTokens.add(["${urnBase}@${gs.toString(true)}", "${node.'@n'}"])
      break

      case "rs":
      if (node.'@type' == "waw") {
	node.children().each { child ->
	  tokenizeElement(child, urnBase, "urn:cite:hmt:tokentypes.waw").each { tokenList ->
	    classifiedTokens.add(tokenList)
	  }
	} 
      } else if (node.'@type' == "ethnic") {
	GreekNode n = new GreekNode(node)
	GreekString gs = new GreekString(n.collectText().replaceAll(/ /,''), "Unicode")
	String urn = node.'@n'.replace("hmt:place", "hmt:peoples")
	classifiedTokens.add(["${urnBase}@${gs.toString(true)}", urn])

      }
      break

      

      default:
      node.children().each { child ->
	tokenizeElement(child, urnBase, "").each { tokenList ->
	  classifiedTokens.add(tokenList)
	}
      }
      break
      }
      
    }
    return classifiedTokens
  }

  
  /** Tokenizes the tabular representation of a text
   * following HMT project editorial conventions.  
   * Tokenization considers both markup and type of characters.
   * @param inputFile A document in tabular format to tokenize.
   * @param separatorStr The String value used to separate columns of
   * the tabular file.  Default value is "#".
   * @returns A List of URN pairs.  Each pairing is an ArrayList containing two
   * identifiers.  The first is the String value of a CTS URN including subreference, 
   * identifying the token; the second is a CITE URN from one of the following
   * collections:
   * urn:cite:hmt:tokenclasses
   * urn:cite:hmt:place
   * urn:cite:hmt:pers
   * @throws Exception if last column of each row of the tabular file cannot be 
   * parsed as a well-formed XML fragment.
   */
  ArrayList tokenizeTabFile(File inputFile, String separatorStr) 
  throws Exception {
    return tokenizeTabString(inputFile.getText("UTF-8"), separatorStr)
  }

  ArrayList indexSubReff(ArrayList tokens) {
    ArrayList indexedTokens = []
    def reffCount = [:]
    System.err.println "Indexing "+ tokens.size() + " pairings"
    tokens.each { t ->
      String subref = t[0]
      String tokenType = t[1]
      if (reffCount.keySet().contains(subref)) {
	reffCount[subref] = reffCount[subref] + 1
      } else {
	reffCount[subref] = 1
      }
      ArrayList pair = ["${subref}[${reffCount[subref]}]",  tokenType]
      System.err.println "New pair is  " +  pair
      indexedTokens.add(pair)
    }
    return indexedTokens
  }


  /** Tokenizes following HMT project editorial conventions a String of data in 
   * the CITE architecture tabular format.
   * Tokenization considers both markup and type of characters.
   * @param tabData A String of data in CITE tabular format..
   * @param separatorStr The String value used to separate columns of
   * the tabular file.  Default value is "#".
   * @returns A List of URN pairs.  Each pairing is an ArrayList containing two
   * identifiers.  The first is the String value of a CTS URN including subreference, 
   * identifying the token; the second is a CITE URN from one of the following
   * collections:
   * urn:cite:hmt:tokenclasses
   * urn:cite:hmt:place
   * urn:cite:hmt:pers
   * @throws Exception if last column of each row of the tabular file cannot be 
   * parsed as a well-formed XML fragment.
   */
  ArrayList tokenizeTabString(String tabData, String separatorStr) 
  throws Exception {
    def replyList = []
    tabData.readLines().each { l ->
      def cols = l.split(/${separatorStr}/)
      def urnBase = cols[0]
      if (cols.size() > 5) {
	try {
	  String str = cols[5]
	  def root = new XmlParser().parseText(str)
	  def rawTokens =  	  tokenizeElement(root, urnBase, "")
	  println "RAW TOKESN " + rawTokens
	  replyList = replyList + indexSubReff(rawTokens)
	  /*
	  tokenizeElement(root, urnBase, "").each { t ->
	    System.err.println "t is " + t + " of class " + t.getClass()
	    ArrayList nodeTokens = indexSubReff(t)
	    System.err.println "NODE TOKENS " +  nodeTokens
	    replyList.add(nodeTokens)
	  }
	  */

	} catch (Exception e) {
	  System.err.println "HmtEditorialTokenization:tokenize: exception"
	  System.err.println "FAILED TO PROCESS LINE: ${l}"
	  throw e
	}

      } else {
	System.err.println "HmtEditorialTokenization: omit input line ${l}"
      }
    }
    return replyList
  }


}

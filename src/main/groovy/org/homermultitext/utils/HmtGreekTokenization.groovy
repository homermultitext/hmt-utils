package org.homermultitext.utils

import org.apache.commons.io.FilenameUtils
import edu.harvard.chs.f1k.GreekNode

import edu.holycross.shot.greekutils.GreekString

/*
Do we want to check for?
 <unclear>, <supplied>
No.  Those should be used to determine content of the edition,
not the analysis of tokenization.
*/


/** Class to tokenize text following Homer Multitext project conventions for
*   definition of character set and markup.
*/
class HmtGreekTokenization {

  Integer debug = 0

  
  /** Empty constructor.
   */
  HmtGreekTokenization() {
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
    // ADD TEST USING greeklang LIB
    def returnVal = []
    if (node instanceof java.lang.String) {
      splitString(node).each { t ->
	ArrayList pairing
	if (tokenType.size() > 0) {
	  // switch on tokenType and apply appropirate
	  // greekLang type.
	  
	  pairing = ["${urnBase}@${t}", tokenType]
	} else {
	  try {
	    GreekString gw = new GreekString(t, "Unicode")
	    pairing = ["${urnBase}@${t}", "urn:cite:hmt:tokentypes.lexical"]
	  } catch (Exception e) {
	    System.err.println "HmtGreekTokenization: could not form GreekWord from string ${t}"
	  }
	}
	returnVal.add(pairing)
      }

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
	    returnVal.add(tokenList)
	  }
	}
      }
      break

      case "num" :
      node.children().each { child ->
	tokenizeElement(child, urnBase, "urn:cite:hmt:tokentypes.numeric").each { tokenList ->
	  returnVal.add(tokenList)
	}
      }
      break

      case "sic":
      GreekNode n = new GreekNode(node)
      String wd = n.collectText()
      wd = wd.replaceAll(~/\s/, "") // generalize to all white space
      if (tokenType.size() > 0) {
	returnVal.add(["${urnBase}@${wd}", "urn:cite:hmt:tokentypes.sic"])
      } 
      break


      case "w":
      GreekNode n = new GreekNode(node)
      String wd = n.collectText()
      wd = wd.replaceAll(~/\s/, "") // generalize to all white space
      if (tokenType.size() > 0) {
	returnVal.add(["${urnBase}@${wd}", tokenType])
      } else {
	returnVal.add(["${urnBase}@${wd}", "urn:cite:hmt:tokentypes.lexical"])
      }
      break

      case "persName":
      GreekNode n = new GreekNode(node)
      String wd = n.collectText()
      //returnVal.add(["${urnBase}@${wd}", "urn:cite:hmt:pers.{node.'@n'}"])
      returnVal.add(["${urnBase}@${wd}", "${node.'@n'}"])
      break

      case "placeName":
      GreekNode n = new GreekNode(node)
      String wd = n.collectText()
      //returnVal.add(["${urnBase}@${wd}", "urn:cite:hmt:place.{node.'@n'}"])
      returnVal.add(["${urnBase}@${wd}", "${node.'@n'}"])
      break

      case "rs":
      if (node.'@type' == "waw") {
	node.children().each { child ->
	  tokenizeElement(child, urnBase, "urn:cite:hmt:tokentypes.waw").each { tokenList ->
	    returnVal.add(tokenList)
	  }
	} 
      } else if (node.'@type' == "ethnic") {
	//tokenizeElement(child, urnBase, "urn:cite:hmt:tokentypes.waw").each { tokenList ->
	//returnVal.add(tokenList)
	//}
	GreekNode n = new GreekNode(node)
	String wd = n.collectText()
	String urn = node.'@n'.replace("hmt:place", "hmt:peoples")
	returnVal.add(["${urnBase}@${wd}", urn])

      }
      break

      

      default:
      node.children().each { child ->
	tokenizeElement(child, urnBase, "").each { tokenList ->
	  returnVal.add(tokenList)
	}
      }
      break
      }
      
    }
    return returnVal
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
    if (debug > 0) {
      System.err.println "Tokenizing input file " + inputFile
    }

    def replyList = []
    inputFile.eachLine { l ->
      def cols = l.split(/${separatorStr}/)
      def urnBase = cols[0]
      if (cols.size() > 5) {
	String str = cols[5]
	def root = null
	try {
	  root = new XmlParser().parseText(str)

	} catch (Exception e) {
	  System.err.println "HmtGreekTokenization:tokenize: exception"
	  System.err.println "FAILED TO PARSE LINE: ${l} with ${cols.size()} columns"
	  System.err.println "str was " + str
	  throw e
	}

	if (root != null) {
	  tokenizeElement(root, urnBase, "").each { t ->
	    replyList.add(t)
	  }
	}


      } else {
	System.err.println "HmtGreekTokenization: omit input line ${l}"
      }
    }
    return replyList

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
  ArrayList tokenizeTabFile(String tabData, String separatorStr) 
  throws Exception {

    def replyList = []
    tabData.readLines().each { l ->
      def cols = l.split(/${separatorStr}/)
      def urnBase = cols[0]
      if (cols.size() > 5) {
	try {
	  String str = cols[5]
	  def root = new XmlParser().parseText(str)
	  tokenizeElement(root, urnBase, "").each { t ->
	    replyList.add(t)
	  }

	} catch (Exception e) {
	  System.err.println "HmtGreekTokenization:tokenize: exception"
	  System.err.println "FAILED TO PARSE LINE: ${l}"
	  throw e
	}

      } else {
	System.err.println "HmtGreekTokenization: omit input line ${l}"
      }
    }
    return replyList
  }


}
